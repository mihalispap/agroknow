package org.fao.agris_indexer.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.fao.agris_indexer.util.AGRISCentres;
import org.fao.agris_indexer.util.Cleaner;
import org.fao.agris_indexer.util.AGRISAPTypeTranslation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @deprecated it refers to Solr index 1.0. The current Solr index is based on AGRIS RDF 2.0
 * SAX parsing of the AGRIS AP XML files to index. 
 * It requires the source file and a SolrServer instance.
 * @author celli
 *
 */
public class AGRISAP_Parser extends DefaultHandler {

	//documents created
	private List<SolrInputDocument> docs;
	private SolrInputDocument tmpDoc;

	//parser
	private SAXParserFactory spf;
	private SAXParser saxParser;
	private File file;

	//server
	SolrServer solrSrv;

	//flag for contents
	private boolean isTitle;
	private boolean isTitleAlternative;
	private boolean isTitleSupplement;
	private boolean isJournalTitle;
	private boolean isConference;

	private boolean isAbstract;
	private boolean isLanguage;
	private boolean isAgrovoc;
	private boolean isSubject;
	private boolean isOtherKeys;
	private boolean isFullText;
	private boolean isType;

	private boolean isISSN;
	private boolean isISBN;

	private boolean isAuthor;
	private boolean isCorporate;
	private boolean isPublisher;
	private boolean isPublicationPlace;
	private boolean isPublicationDate;
	private boolean isRealPublicationDate;

	//for auto-suggestions: agrovoc, asc or title
	private Set<String> autosuggestionAgrovoc;
	private String autosuggestionAsc;
	private String autosuggestionFree;
	private Set<String> suggestionsAdded;

	//faceting
	private String agrovocLanguage=null;
	private boolean isFaceting;

	//to read the entire content
	private StringBuffer buffer;

	//create the parser for the document and start parsing
	public AGRISAP_Parser(String filename, SolrServer solr, Set<String> suggestionsAdded) {
		this(new File(filename), solr, suggestionsAdded);
	}

	public AGRISAP_Parser(File xmlFile, SolrServer solr, Set<String> suggestionsAdded) {
		this.solrSrv = solr;
		try {
			this.spf = SAXParserFactory.newInstance();
			this.spf.setNamespaceAware(false);
			this.spf.setValidating(false);
			this.spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			this.saxParser = this.spf.newSAXParser();
			this.file = xmlFile;

			//flags
			this.isTitle = false;
			this.isJournalTitle = false;
			this.isLanguage = false;
			this.isTitleAlternative = false;
			this.isTitleSupplement = false;
			this.isConference = false;
			this.isAbstract = false;
			this.isISSN = false;
			this.isISBN = false;
			this.isCorporate = false;
			this.isSubject = false;
			this.isAuthor = false;
			this.isAgrovoc = false;
			this.isOtherKeys = false;
			this.isFullText = false;
			this.isPublicationDate = false;
			this.isPublicationPlace = false;
			this.isPublisher = false;
			this.isRealPublicationDate = false;
			this.suggestionsAdded = suggestionsAdded;
			this.autosuggestionAgrovoc = new HashSet<String>();
			this.isFaceting = false;
			this.isType = false;

			//parsing
			this.saxParser.parse(xmlFile, this);
		}
		catch(SAXParseException e) {
			System.out.println("\nParsing Error: "+ e.getMessage());
			e.printStackTrace();
			System.out.println(xmlFile.getName());
		}
		catch(Exception e) {
			System.out.println("\nGeneric Exception: "+ e.getMessage());
			e.printStackTrace();
			System.out.println(xmlFile.getName());
		}
	}

	////////////////////////////////////////////////////////////////////
	// Event handlers.
	////////////////////////////////////////////////////////////////////

	/*
	 * Called when the Parser starts parsing the Current XML File.
	 * Initialize the document list
	 */
	public void startDocument () throws SAXException{
		this.docs = new ArrayList<SolrInputDocument>();
		this.tmpDoc = null;
	}

	/*
	 * Called when the Parser Completes parsing the Current XML File.
	 * Add documents to index
	 */
	public void endDocument () throws SAXException{
		try {
			if(this.docs.size()>0){
				this.solrSrv.add(this.docs);
				System.out.println(this.file.getName());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
			System.out.println(this.docs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recognizes an XML element to index
	 * @param namespaceURI <code>String</code> namespace URI this element is associated with, or an empty <code>String</code>
	 * @param localName <code>String</code> name of element (with no namespace prefix, if one is present)
	 * @param rawName <code>String</code> XML 1.0 version of element name: [namespace prefix]:[localName]
	 * @param atts <code>Attributes</code> list for this element
	 * @throws <code>SAXException</code> when things go wrong
	 */
	public void startElement (String namespaceURI, String localName, String rawName, Attributes atts) throws SAXException {	

		//ARN, center, URI, date
		if(rawName.equalsIgnoreCase("ags:resource")){
			tmpDoc = new SolrInputDocument();
			//look for ARN
			String arn = atts.getValue("ags:ARN");
			if(arn!=null){
				tmpDoc.addField("ARN", arn);
				tmpDoc.addField("center", arn.substring(0, 2));

				//look for centerkey
				if(arn.length()>=12){
					String centerKey = arn.substring(6, 7);
					centerKey = AGRISCentres.checkScieloSubcenters(arn.substring(0, 2), centerKey);
					//System.out.println(arn.substring(0, 2));
					//System.out.println(centerKey);
					tmpDoc.addField("centerkey", centerKey);
				}
				else 
					tmpDoc.addField("centerkey", AGRISCentres.getCenterKeyByARN(arn));

				//URI and date
				String absolutePath = file.getAbsolutePath();
				String[] fileParts = absolutePath.split("/");
				if(fileParts==null || fileParts.length<3)
					fileParts = absolutePath.split("\\\\");
				String path = fileParts[fileParts.length-3]+"/" + fileParts[fileParts.length-2]+"/"+fileParts[fileParts.length-1];
				tmpDoc.addField("URI", path);
				//file path: something/YEAR/CN/filename.xml
				tmpDoc.addField("date", fileParts[fileParts.length-3]);
			}
			else {
				tmpDoc = null;
				System.out.println("ARN expected: "+file.getAbsolutePath());
			}
		}

		//title
		if(rawName.equalsIgnoreCase("dc:title") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isTitle = true;
		}

		//journal title
		if(rawName.equalsIgnoreCase("ags:citationTitle") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isJournalTitle = true;
		}

		//title alternative
		if(rawName.equalsIgnoreCase("dcterms:alternative") && tmpDoc!=null){
			this.isTitle = false;	//to avoid to consider the father tag
			this.isTitleAlternative = true;
			this.buffer = new StringBuffer();
		}

		//conference
		if(rawName.equalsIgnoreCase("ags:creatorConference") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isConference = true;
		}

		//title supplement
		if(rawName.equalsIgnoreCase("ags:titleSupplement") && tmpDoc!=null){
			this.isTitle = false;	//to avoid to consider the father tag
			this.isTitleSupplement = true;
			this.buffer = new StringBuffer();
		}

		//author
		if(rawName.equalsIgnoreCase("dc:creator") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isAuthor = true;
		}

		if(rawName.equalsIgnoreCase("ags:creatorPersonal") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isAuthor = true;
		}

		//corporate author
		if(rawName.equalsIgnoreCase("ags:creatorCorporate") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isAuthor = false;
			this.isCorporate = true;
		}

		if(rawName.equalsIgnoreCase("ags:creatorConference") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isAuthor = false;
		}

		//publisherName
		if(rawName.equalsIgnoreCase("ags:publisherName")){
			this.buffer = new StringBuffer();
			this.isPublisher = true;
		}

		//publicationPlace
		if(rawName.equalsIgnoreCase("ags:publisherPlace")){
			this.buffer = new StringBuffer();
			this.isPublicationPlace = true;
		}

		//publication date
		if(rawName.equalsIgnoreCase("dcterms:dateIssued")){
			this.buffer = new StringBuffer();
			this.isPublicationDate = true;
		}
		
		//type
		if(rawName.equalsIgnoreCase("dc:type")){
			this.buffer = new StringBuffer();
			this.isType = true;
		}

		//real publication date
		if(rawName.equalsIgnoreCase("ags:citationChronology")){
			this.buffer = new StringBuffer();
			this.isRealPublicationDate = true;
		}

		//fulltext
		if(rawName.equalsIgnoreCase("dc:identifier")){
			String scheme = atts.getValue("scheme");
			if(scheme!=null && scheme.equalsIgnoreCase("dcterms:URI")){
				this.buffer = new StringBuffer();
				this.isFullText = true;
			}
		}

		//agrovoc
		if(rawName.equalsIgnoreCase("ags:subjectThesaurus") && tmpDoc!=null){
			this.isOtherKeys = false;	//set to true if dc:subject is without children
			String scheme = atts.getValue("scheme");
			String lang = atts.getValue("xml:lang");	//no agrovoc URL
			if(scheme!=null && scheme.equalsIgnoreCase("ags:AGROVOC") && lang!=null && !lang.equals("")){
				this.buffer = new StringBuffer();
				this.isAgrovoc = true;
				//check language for faceting
				if(this.agrovocLanguage==null)
					this.agrovocLanguage = lang;
				else if(this.agrovocLanguage.equals(lang))
					this.isFaceting = true;
			}
		}

		//other keys
		if(rawName.equalsIgnoreCase("ags:subjectThesaurus") && tmpDoc!=null){
			this.isOtherKeys = false;	//set to true if dc:subject is without children
			String scheme = atts.getValue("scheme");
			String lang = atts.getValue("xml:lang");	//no agrovoc URL
			if(scheme!=null && !scheme.equalsIgnoreCase("ags:AGROVOC") && lang!=null && !lang.equals("")){
				this.buffer = new StringBuffer();
				this.isOtherKeys = true;
			}
		}

		//other keys only in dc:subject
		if(rawName.equalsIgnoreCase("dc:subject") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isOtherKeys = true;
		}

		//subject classification
		if(rawName.equalsIgnoreCase("ags:subjectClassification") && tmpDoc!=null){
			this.isOtherKeys = false;	//set to true if dc:subject is without children
			String scheme = atts.getValue("scheme");
			if(scheme!=null && scheme.equalsIgnoreCase("ags:ASC")){
				this.buffer = new StringBuffer();
				this.isSubject = true;
			}
		}

		//language
		if(rawName.equalsIgnoreCase("dc:language") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isLanguage = true;
		}

		//abstract
		if(rawName.equalsIgnoreCase("dcterms:abstract") && tmpDoc!=null){
			this.buffer = new StringBuffer();
			this.isAbstract = true;
		}

		//isbn
		if(rawName.equalsIgnoreCase("dc:identifier") && tmpDoc!=null){
			String scheme = atts.getValue("scheme");
			if(scheme!=null && scheme.equalsIgnoreCase("ags:ISBN")){
				this.buffer = new StringBuffer();
				this.isISBN = true;
			}
		}

		//ISSN
		if(rawName.equalsIgnoreCase("ags:citationIdentifier") && tmpDoc!=null){
			String scheme = atts.getValue("scheme");
			if(scheme!=null && (scheme.equalsIgnoreCase("ags:ISSN") || scheme.equalsIgnoreCase("bibo:eissn"))){
				this.buffer = new StringBuffer();
				this.isISSN = true;
			}
		}
	}


	/**
	 * Buffers management and end of record
	 * @param namespaceURI <code>String</code> URI of namespace this element is associated with
	 * @param localName <code>String</code> name of element without prefix
	 * @param rawName <code>String</code> name of element in XML 1.0 form
	 * @throws <code>SAXException</code> when things go wrong
	 */
	public void endElement (String namespaceURI, String localName, String rawName) throws SAXException {
		//end of record: add document to list
		if(rawName.equalsIgnoreCase("ags:resource")){
			if(tmpDoc!=null){
				//autosuggestions
				this.addAutosuggestionsDetails();	
				//add document
				this.docs.add(tmpDoc);
				tmpDoc = null;
			}
		}

		//TITLE buffer
		if(rawName.equalsIgnoreCase("dc:title")){
			this.isTitle = false;
			if(this.buffer!=null){
				String term = new String(this.buffer);
				if(term!=null && !term.trim().equals("")){
					term = Cleaner.trimLeft(term);
					term = Cleaner.trimRight(term);
					tmpDoc.addField("title", term);
				}
			}
		}

		//title alternative
		if(rawName.equalsIgnoreCase("dcterms:alternative")){
			this.isTitleAlternative = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("alternative", term);
		}

		//title supplement
		if(rawName.equalsIgnoreCase("ags:titleSupplement")){
			this.isTitleSupplement = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("titleSupplement", term);
		}

		//language
		if(rawName.equalsIgnoreCase("dc:language")){
			this.isLanguage = false;
			String term = new String(this.buffer);
			term = Cleaner.filterDirtyLanguages(term);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("language", term);	
		}

		//isbn
		if(rawName.equalsIgnoreCase("dc:identifier") && this.isISBN){
			this.isISBN = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("ISBN", term);
		}

		//author buffer
		if((rawName.equalsIgnoreCase("dc:creator") && this.isAuthor==true)
				|| rawName.equalsIgnoreCase("ags:creatorPersonal")){
			this.isAuthor = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				term = Cleaner.trimLeft(term);
				term = Cleaner.trimRight(term);
				tmpDoc.addField("author", term);	
			}
		}

		//corporate author buffer
		if(rawName.equalsIgnoreCase("ags:creatorCorporate")){
			this.isCorporate = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				term = Cleaner.trimLeft(term);
				term = Cleaner.trimRight(term);
				tmpDoc.addField("corporateAuthor", term);
			}
		}

		//agrovoc buffer
		if(rawName.equalsIgnoreCase("ags:subjectThesaurus") && this.isAgrovoc){
			this.isAgrovoc = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				term = Cleaner.trimLeft(term);
				term = Cleaner.trimRight(term);
				tmpDoc.addField("agrovoc", term, 100);
				//autosuggestion: split /
				String terms[] = term.split("/");
				for(String t: terms){
					t = Cleaner.trimLeft(t);
					t = Cleaner.trimRight(t);
					this.autosuggestionAgrovoc.add(t);
					//faceting
					if(this.isFaceting)
						tmpDoc.addField("agrovoc_facet", t);
				}
			}
			this.isFaceting=false;
		}

		//other keys buffer
		if((rawName.equalsIgnoreCase("ags:subjectThesaurus") || rawName.equalsIgnoreCase("dc:subject")) && this.isOtherKeys){
			this.isOtherKeys = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				tmpDoc.addField("other_key", term);
				if(this.autosuggestionFree==null)
					this.autosuggestionFree = term;
			}
		}

		//subject classification buffer
		if(rawName.equalsIgnoreCase("ags:subjectClassification") && this.isSubject){
			this.isSubject = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				//clean ASC
				List<String> cleanedTerm = Cleaner.cleanASC(term);
				for(String s: cleanedTerm){
					if(this.autosuggestionAsc==null)
						this.autosuggestionAsc = s;
					tmpDoc.addField("subject", s);
				}
			}
		}

		// JOURNAL TITLE buffer
		if(rawName.equalsIgnoreCase("ags:citationTitle")){
			this.isJournalTitle = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				term = Cleaner.trimLeft(term);
				term = Cleaner.trimRight(term);
				tmpDoc.addField("citationTitle", term);
				tmpDoc.addField("type", "Journal Article");
			}
		}

		// ABSTRACT buffer
		if(rawName.equalsIgnoreCase("dcterms:abstract")){
			this.isAbstract = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				tmpDoc.addField("abstract", term);
			}
		}

		//PUBLISHER BUFFER
		if(rawName.equalsIgnoreCase("ags:publisherName")){
			this.isPublisher = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("publisher", term);
		}

		//publicationPlace
		if(rawName.equalsIgnoreCase("ags:publisherPlace")){
			this.isPublicationPlace = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("publicationPlace", term);
		}

		//publication date
		if(rawName.equalsIgnoreCase("dcterms:dateIssued")){
			this.isPublicationDate = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				//naive cleaning
				if(term.startsWith("(") && term.endsWith(")"))
					term = term.substring(1, term.length()-1);
				tmpDoc.addField("publicationDate", term);
			}
		}
		
		//type
		if(rawName.equalsIgnoreCase("dc:type")){
			this.isType = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				//conversion
				term = AGRISAPTypeTranslation.translate(term);
				tmpDoc.addField("type", term);
			}
		}

		//real publication date
		if(rawName.equalsIgnoreCase("ags:citationChronology")){
			this.isRealPublicationDate = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				//naive cleaning, remove parenthesis
				if(term.startsWith("(") && term.endsWith(")"))
					term = term.substring(1, term.length()-1);
				//try to remove old date
				tmpDoc.removeField("publicationDate");
				tmpDoc.addField("publicationDate", term);
			}
		}

		//fulltext buffer
		if(rawName.equalsIgnoreCase("dc:identifier") && this.isFullText){
			this.isFullText = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				if(term.startsWith("http") || term.startsWith("ftp") ||term.startsWith("www"))
					tmpDoc.addField("fulltext", term);
			}
		}

		//ISSN buffer
		if(rawName.equalsIgnoreCase("ags:citationIdentifier") && this.isISSN){
			this.isISSN = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")){
				//naive issn cleaning
				if(!Cleaner.isISSN(term))
					term = Cleaner.cleanISSN(term);
				//add only if it is an ISSN
				if(Cleaner.isISSN(term)){
					tmpDoc.addField("ISSN", term);
					tmpDoc.addField("type", "Journal Article");
				}
			}
		}

		// CONFERENCE
		if(rawName.equalsIgnoreCase("ags:creatorConference") && this.isConference){
			this.isConference = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				tmpDoc.addField("conference", term);
		}
	}

	/**
	 * @param ch <code>char[]</code> character array with character data
	 * @param start <code>int</code> index in array where data starts.
	 * @param length <code>int</code> length of data in array.
	 * @throws <code>SAXException</code> when things go wrong
	 */
	public void characters(char[] ch, int start, int length) {
		//title, journal title, abstract
		if(this.isTitle || this.isJournalTitle || this.isAbstract || this.isAuthor || this.isCorporate 
				|| this.isAgrovoc || this.isSubject || this.isOtherKeys || this.isPublisher || this.isPublicationPlace 
				|| this.isPublicationDate || this.isFullText || this.isISSN || this.isLanguage || this.isTitleAlternative
				|| this.isTitleSupplement || this.isISBN || this.isRealPublicationDate || this.isConference || this.isType){
			this.buffer.append(ch, start, length);
		}
	}

	/*
	 * For autosuggestions
	 */
	private void addAutosuggestionsDetails(){
		//check if autosuggestions has been enabled
		if(this.suggestionsAdded!=null){
			boolean added = false;
			//autosuggestions
			if(this.autosuggestionAgrovoc.size()>0){
				for(String s: this.autosuggestionAgrovoc){
					if(!this.suggestionsAdded.contains(s.toLowerCase())){
						tmpDoc.addField("autosuggestion", s.toLowerCase());
						added = true;
						this.suggestionsAdded.add(s.toLowerCase());
						break;
					}
				}
			}
			if(!added && this.autosuggestionAsc!=null){
				if(!this.suggestionsAdded.contains(this.autosuggestionAsc.toLowerCase())){
					tmpDoc.addField("autosuggestion", this.autosuggestionAsc.toLowerCase());
					this.suggestionsAdded.add(this.autosuggestionAsc.toLowerCase());
					added = true;
				}
			}
			if(!added && this.autosuggestionFree!=null){
				if(!this.suggestionsAdded.contains(this.autosuggestionFree.toLowerCase())){
					tmpDoc.addField("autosuggestion", this.autosuggestionFree.toLowerCase());
					this.suggestionsAdded.add(this.autosuggestionFree.toLowerCase());
					added = true;
				}
			}
		}
		this.autosuggestionAgrovoc = new HashSet<String>();
		this.autosuggestionAsc = null;
		this.autosuggestionAsc = null;
	}

	/*
	 * Example of usage
	 * public static void main(String[] args) throws SolrServerException, IOException{
	 * SolrServer server = IndexServerFactory.startSolr();
	 * String filename = "C:/Users/celli/Desktop/TestSolr/IT/IT0501.xml";
	 * new SaxIndexParser(filename, server);
	 * server.optimize();
	 * }
	 */

}
