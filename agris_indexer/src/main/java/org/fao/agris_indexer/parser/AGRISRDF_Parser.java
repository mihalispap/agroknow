package org.fao.agris_indexer.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.fao.agris_indexer.util.Cleaner;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX parsing of the AGRIS RDF/XML 2.0 files to index. 
 * It requires the source file and a SolrServer instance.
 * Some fields contain values only in attributes: this is the case of URIs, like for centers (dct:source)
 * @author celli
 *
 */
public class AGRISRDF_Parser extends DefaultHandler {

	private final static Logger log = Logger.getLogger(AGRISRDF_Parser.class.getName());

	////////////////////////////////////////////////////////////////////
	// Variables. 
	////////////////////////////////////////////////////////////////////

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
	private boolean isAlternative;
	private boolean isIdentifier;
	private boolean isDateSubmitted;
	private boolean isDatePublication;

	private final String agrisSerialPrefix = "http://aims.fao.org/serials";
	private boolean isJournal;
	private boolean isCitationTitle;
	private boolean isPartOfUrl;
	private boolean isISSN;
	private boolean isConference;
	private boolean isConferenceTitle;

	private boolean isAuthor;
	private boolean isAuthorName;
	private boolean isOrg;
	private boolean isOrgName;
	private boolean isPublisher;
	private boolean isPublisherName;

	private boolean isSubject;
	private boolean isDescription;
	private boolean isAbstract;
	private boolean isLanguage;
	private boolean isVolume;
	private boolean isIssue;
	private boolean ispgStart;
	private boolean ispgEnd;
	private boolean isType;
	private boolean isExtent;
	private boolean isMedium;
	private boolean isIsbn;
	private boolean isDoi;
	private boolean isRight;
	private boolean isFulltext;

	//for auto-suggestions: agrovoc, asc or title
	private Set<String> autosuggestionAgrovoc;
	private String autosuggestionAsc;
	private String autosuggestionFree;
	private Set<String> suggestionsAdded;

	//language attributes management
	private final String default3digitLang = "eng";
	private String tmpLanguage=null;

	//faceting
	private boolean isFaceting;

	//to read the entire content
	private StringBuffer buffer;

	////////////////////////////////////////////////////////////////////
	// Constructors.
	////////////////////////////////////////////////////////////////////

	//create the parser for the document and start parsing
	public AGRISRDF_Parser(String filename, SolrServer solr, Set<String> suggestionsAdded) {
		this(new File(filename), solr, suggestionsAdded);
	}

	public AGRISRDF_Parser(File xmlFile, SolrServer solr, Set<String> suggestionsAdded) {
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
			this.isAlternative = false;
			this.isDateSubmitted = false;
			this.isIdentifier = false;
			this.isJournal = false;
			this.isCitationTitle = false;
			this.isISSN = false;
			this.isConference = false;
			this.isConferenceTitle = false;
			this.isAuthorName = false;
			this.isAuthor = false;
			this.isOrg = false;
			this.isOrgName = false;
			this.isPublisherName = false;
			this.isPublisher = false;
			this.isDatePublication = false;
			this.isDescription = false;
			this.isAbstract = false;
			this.isSubject = false;
			this.isLanguage = false;
			this.isVolume = false;
			this.isIssue = false;
			this.isType = false;
			this.isExtent = false;
			this.isMedium = false;
			this.isIsbn = false;
			this.isDoi = false;
			this.isRight = false;
			this.isFulltext = false;
			this.isPartOfUrl = false;
			this.ispgStart = false;
			this.ispgEnd = false;

			this.suggestionsAdded = suggestionsAdded;
			this.autosuggestionAgrovoc = new HashSet<String>();
			this.isFaceting = false;

			//parsing
			this.saxParser.parse(xmlFile, this);
		}
		catch(SAXParseException e) {
			log.log(Level.WARNING, "\nParsing Error: "+ e.getMessage());
			log.log(Level.WARNING, e.toString());
			log.log(Level.WARNING, xmlFile.getName());
		}
		catch(Exception e) {
			log.log(Level.WARNING, "\nGeneric Exception: "+ e.getMessage());
			e.printStackTrace();
			log.log(Level.WARNING, e.toString());
			log.log(Level.WARNING, xmlFile.getName());
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
				System.out.println("\t--> "+this.file.getName());
			}
		} catch (SolrServerException e) {
			log.log(Level.WARNING, e.toString());
			log.log(Level.WARNING, this.docs.toString());
		} catch (IOException e) {
			log.log(Level.WARNING, e.toString());
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

		if(rawName.equalsIgnoreCase("bibo:Article")){
			tmpDoc = new SolrInputDocument();
			//look for URI
			String rdfURI = atts.getValue("rdf:about");
			if(rdfURI!=null){
				tmpDoc.addField("URI", rdfURI);

				//file system URI for AGRIS AP XML files
				if(file.getName()!=null && file.getName().length()>2) {
					String filePrefix = file.getName().substring(0, 2).toUpperCase();
					String absolutePath = file.getAbsolutePath();
					String[] fileParts = absolutePath.split("/");
					if(fileParts==null || fileParts.length<2)
						fileParts = absolutePath.split("\\\\");
					String path = fileParts[fileParts.length-2]+"/"+filePrefix+"/"+fileParts[fileParts.length-1];
					tmpDoc.addField("fsURI", path);
				}
			}
			else {
				tmpDoc = null;
				log.log(Level.WARNING, "Record URI expected: " + file.getAbsolutePath());
			}
		}
		else
			//title
			if(rawName.equalsIgnoreCase("dct:title") && tmpDoc!=null){
				//check if it is a journal or a conference
				if(this.isJournal){
					this.isCitationTitle = true;
				}
				else if(this.isConference){
					this.isConferenceTitle = true;
				}
				else {
					//record title, look for language
					String lang = atts.getValue("xml:lang");
					if(lang!=null && lang.length()>0)
						this.tmpLanguage = lang;
					else
						this.tmpLanguage = this.default3digitLang;
					this.isTitle = true;
				}
				this.buffer = new StringBuffer();
			}
			else 
				//title alternative
				if(rawName.equalsIgnoreCase("dct:alternative") && tmpDoc!=null){
					this.isAlternative = true;
					this.buffer = new StringBuffer();
				}
				else 
					//identifier ARN
					if(rawName.equalsIgnoreCase("dct:identifier") && tmpDoc!=null){
						this.isIdentifier = true;
						this.buffer = new StringBuffer();
					}
					else 
						//date submitted
						if(rawName.equalsIgnoreCase("dct:dateSubmitted") && tmpDoc!=null){
							this.isDateSubmitted = true;
							this.buffer = new StringBuffer();
						}
						else 
							//source: no needs for buffer and end element, everything in the attribute
							if(rawName.equalsIgnoreCase("dct:source") && tmpDoc!=null){
								String uri = atts.getValue("rdf:resource");
								if(uri!=null && uri.length()>0)
									tmpDoc.addField("center", uri);
							}
							else 
								//journal: in case of URI, no needs for buffer and end element, everything in the attribute
								if(rawName.equalsIgnoreCase("dct:isPartOf") && tmpDoc!=null){
									String uri = atts.getValue("rdf:resource");
									if(uri!=null && uri.length()>0){
										if(uri.startsWith(this.agrisSerialPrefix))
											tmpDoc.addField("serial", uri);
									}
									else
										this.isPartOfUrl = true;
								}
								else 
									//journal blank node: no string buffer, only flag
									if(rawName.equalsIgnoreCase("bibo:Journal") && tmpDoc!=null){
										this.isJournal = true;
										//if there is a joirnal, it is not the isPartOfUrl
										this.isPartOfUrl = false;
									}
									else 
										//journal ISSN
										if(rawName.equalsIgnoreCase("bibo:ISSN") && tmpDoc!=null){
											this.isISSN = true;
											this.buffer = new StringBuffer();
										}
										else 
											//conference blank node: no string buffer, only flag
											if(rawName.equalsIgnoreCase("bibo:Conference") && tmpDoc!=null){
												this.isConference = true;
											}
											else 
												//creator blank node: no string buffer, only flag
												if(rawName.equalsIgnoreCase("dct:creator") && tmpDoc!=null){
													this.isAuthor = true;
												}
												else 
													//publisher blank node: no string buffer, only flag
													if(rawName.equalsIgnoreCase("dct:publisher") && tmpDoc!=null){
														this.isPublisher = true;
													}
													else 
														//corporate creator organization blank node: no string buffer, only flag
														if(rawName.equalsIgnoreCase("foaf:organization") && tmpDoc!=null && this.isAuthor){
															this.isOrg = true;
															this.isAuthor = false;
														}
														else
															//foaf:name, many cases
															if(rawName.equalsIgnoreCase("foaf:name") && tmpDoc!=null){
																if(this.isPublisher)
																	this.isPublisherName = true;
																else if(this.isOrg)
																	this.isOrgName = true;
																else if(this.isAuthor)
																	this.isAuthorName = true;
																this.buffer = new StringBuffer();
															}
															else 
																//date of publication
																if(rawName.equalsIgnoreCase("dct:issued") && tmpDoc!=null){
																	this.isDatePublication = true;
																	this.buffer = new StringBuffer();
																}
																else 
																	//agrovoc uri: no needs for buffer and end element, everything in the attribute
																	if(rawName.equalsIgnoreCase("dct:subject") && tmpDoc!=null){
																		String uri = atts.getValue("rdf:resource");
																		if(uri!=null && uri.length()>0)
																			tmpDoc.addField("agrovoc", uri);
																	}
																	else 
																		//string subject
																		if(rawName.equalsIgnoreCase("dc:subject") && tmpDoc!=null){
																			String lang = atts.getValue("xml:lang");
																			this.buffer = new StringBuffer();
																			this.isSubject = true;
																			//faceting only english keywords or no language keywords
																			if(lang==null || this.default3digitLang.equalsIgnoreCase(lang))
																				this.isFaceting = true;
																		}
																		else
																			//description
																			if(rawName.equalsIgnoreCase("dct:description") && tmpDoc!=null){
																				this.isDescription = true;
																				this.buffer = new StringBuffer();
																			}
																			else
																				//abstract
																				if(rawName.equalsIgnoreCase("bibo:abstract") && tmpDoc!=null){
																					//record abstract, look for language
																					String lang = atts.getValue("xml:lang");
																					if(lang!=null && lang.length()>0)
																						this.tmpLanguage = lang;
																					else
																						this.tmpLanguage = this.default3digitLang;
																					this.isAbstract = true;
																					this.buffer = new StringBuffer();
																				}
																				else
																					//language
																					if(rawName.equalsIgnoreCase("dct:language") && tmpDoc!=null){
																						this.isLanguage = true;
																						this.buffer = new StringBuffer();
																					}
																					else
																						//volume
																						if(rawName.equalsIgnoreCase("bibo:volume") && tmpDoc!=null){
																							this.isVolume = true;
																							this.buffer = new StringBuffer();
																						}
																						else
																							//issue
																							if(rawName.equalsIgnoreCase("bibo:issue") && tmpDoc!=null){
																								this.isIssue = true;
																								this.buffer = new StringBuffer();
																							}
																							else
																								//pageStart
																								if(rawName.equalsIgnoreCase("bibo:pageStart") && tmpDoc!=null){
																									this.ispgStart = true;
																									this.buffer = new StringBuffer();
																								}
																								else
																									//pageEnd
																									if(rawName.equalsIgnoreCase("bibo:pageEnd") && tmpDoc!=null){
																										this.ispgEnd = true;
																										this.buffer = new StringBuffer();
																									}
																									else
																										//type
																										if(rawName.equalsIgnoreCase("dct:type") && tmpDoc!=null){
																											this.isType = true;
																											this.buffer = new StringBuffer();
																										}
																										else
																											//extent
																											if(rawName.equalsIgnoreCase("dct:extent") && tmpDoc!=null){
																												this.isExtent = true;
																												this.buffer = new StringBuffer();
																											}
																											else
																												//medium
																												if(rawName.equalsIgnoreCase("dct:medium") && tmpDoc!=null){
																													this.isMedium = true;
																													this.buffer = new StringBuffer();
																												}
																												else
																													//isbn
																													if(rawName.equalsIgnoreCase("bibo:isbn") && tmpDoc!=null){
																														this.isIsbn = true;
																														this.buffer = new StringBuffer();
																													}
																													else
																														//doi
																														if(rawName.equalsIgnoreCase("bibo:doi") && tmpDoc!=null){
																															this.isDoi = true;
																															this.buffer = new StringBuffer();
																														}
																														else
																															//right
																															if(rawName.equalsIgnoreCase("dct:rights") && tmpDoc!=null){
																																this.isRight = true;
																																this.buffer = new StringBuffer();
																															}
																															else
																																//fulltext
																																if(rawName.equalsIgnoreCase("bibo:uri") && tmpDoc!=null){
																																	this.isFulltext = true;
																																	this.buffer = new StringBuffer();
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
		if(rawName.equalsIgnoreCase("bibo:Article")){
			if(tmpDoc!=null){
				//autosuggestions
				this.addAutosuggestionsDetails();	
				//add document
				this.docs.add(tmpDoc);
				tmpDoc = null;
			}
		}
		else if(tmpDoc!=null){
			//TITLEs 
			if(rawName.equalsIgnoreCase("dct:title")){
				//record title
				if(this.isTitle){
					this.isTitle = false;
					if(this.buffer!=null && this.tmpLanguage!=null){
						String term = new String(this.buffer);
						if(term!=null && !term.trim().equals("")){
							term = Cleaner.trimLeft(term);
							term = Cleaner.trimRight(term);
							tmpDoc.addField("title_"+this.tmpLanguage, term);
						}
					}
					this.tmpLanguage = null;
				}
				else 
					//journal
					if(this.isCitationTitle){
						this.isCitationTitle = false;
						if(this.buffer!=null){		
							String term = new String(this.buffer);
							if(term!=null && !term.trim().equals("")){
								term = Cleaner.trimLeft(term);
								term = Cleaner.trimRight(term);
								tmpDoc.addField("citationTitle", term);
							}
						}
					}
					else
						//conference
						if(this.isConferenceTitle){
							this.isConferenceTitle = false;
							if(this.buffer!=null){		
								String term = new String(this.buffer);
								if(term!=null && !term.trim().equals("")){
									term = Cleaner.trimLeft(term);
									term = Cleaner.trimRight(term);
									tmpDoc.addField("conference", term);
								}
							}
						}
			}
			else 
				//title alternative
				if(rawName.equalsIgnoreCase("dct:alternative")){
					this.isAlternative = false;
					String term = new String(this.buffer);
					if(term!=null && !term.trim().equals(""))
						tmpDoc.addField("alternative", term);
				}
				else 
					//identifier ARN
					if(rawName.equalsIgnoreCase("dct:identifier")){
						this.isIdentifier = false;
						String term = new String(this.buffer);
						if(term!=null && !term.trim().equals(""))
							tmpDoc.addField("ARN", term);
					}
					else 
						//date submitted
						if(rawName.equalsIgnoreCase("dct:dateSubmitted")){
							this.isDateSubmitted = false;
							String term = new String(this.buffer);
							if(term!=null && !term.trim().equals(""))
								tmpDoc.addField("date", term);
						}
						else 
							//close journal box
							if(rawName.equalsIgnoreCase("bibo:Journal")){
								this.isJournal = false;
							}
							else 
								//ISSN
								if(rawName.equalsIgnoreCase("bibo:ISSN")){
									this.isISSN = false;
									String term = new String(this.buffer);
									if(term!=null && !term.trim().equals(""))
										tmpDoc.addField("ISSN", term);
								}
								else 
									//isPartOfUrl
									if(rawName.equalsIgnoreCase("dct:isPartOf") && this.isPartOfUrl){
										this.isPartOfUrl = false;
										String term = new String(this.buffer);
										if(term!=null && !term.trim().equals(""))
											tmpDoc.addField("partOf", term);
									}
									else 
										//close conference box
										if(rawName.equalsIgnoreCase("bibo:Conference")){
											this.isConference = false;
										}
										else 
											//close creator box
											if(rawName.equalsIgnoreCase("dct:creator")){
												this.isAuthor = false;
												this.isOrg = false;
											}
											else 
												//close publisher box
												if(rawName.equalsIgnoreCase("dct:publisher")){
													this.isPublisher = false;
												}
												else
													//foaf:name: many options
													if(rawName.equalsIgnoreCase("foaf:name")){
														if(this.isAuthorName){
															this.isAuthorName = false;
															String term = new String(this.buffer);
															if(term!=null && !term.trim().equals(""))
																tmpDoc.addField("author", term);
														} else
															if(this.isOrgName){
																this.isOrgName = false;
																String term = new String(this.buffer);
																if(term!=null && !term.trim().equals(""))
																	tmpDoc.addField("corporateAuthor", term);
															} else if(this.isPublisherName){
																this.isPublisherName = false;
																String term = new String(this.buffer);
																if(term!=null && !term.trim().equals(""))
																	tmpDoc.addField("publisher", term);
															}
													}
													else 
														//publication date
														if(rawName.equalsIgnoreCase("dct:issued")){
															this.isDatePublication = false;
															String term = new String(this.buffer);
															if(term!=null && !term.trim().equals(""))
																tmpDoc.addField("publicationDate", term);
														}
														else
															//string subjects, including agrovoc
															if(rawName.equalsIgnoreCase("dc:subject")){
																this.isSubject = false;
																String term = new String(this.buffer);
																if(term!=null && !term.trim().equals("")){
																	term = Cleaner.trimLeft(term);
																	term = Cleaner.trimRight(term);
																	tmpDoc.addField("subject", term, 100);
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
															else 
																//description
																if(rawName.equalsIgnoreCase("dct:description")){
																	this.isDescription = false;
																	String term = new String(this.buffer);
																	if(term!=null && !term.trim().equals(""))
																		tmpDoc.addField("description", term);
																}
																else
																	//abstract
																	if(rawName.equalsIgnoreCase("bibo:abstract")){
																		this.isAbstract = false;
																		if(this.buffer!=null && this.tmpLanguage!=null){
																			String term = new String(this.buffer);
																			if(term!=null && !term.trim().equals("")){
																				tmpDoc.addField("abstract_"+this.tmpLanguage, term);
																			}
																		}
																		this.tmpLanguage = null;
																	}
																	else
																		//language
																		if(rawName.equalsIgnoreCase("dct:language")){
																			this.isLanguage = false;
																			String term = new String(this.buffer);
																			if(term!=null && !term.trim().equals(""))
																				tmpDoc.addField("language", term);	
																		}
																		else
																			//volume
																			if(rawName.equalsIgnoreCase("bibo:volume")){
																				this.isVolume = false;
																				String term = new String(this.buffer);
																				if(term!=null && !term.trim().equals("")){
																					tmpDoc.addField("volume", term);
																				}
																			}
																			else
																				//issue
																				if(rawName.equalsIgnoreCase("bibo:issue")){
																					this.isIssue = false;
																					String term = new String(this.buffer);
																					if(term!=null && !term.trim().equals("")){
																						tmpDoc.addField("issue", term);
																					}
																				}
																				else
																					//page start
																					if(rawName.equalsIgnoreCase("bibo:pageStart")){
																						this.ispgStart = false;
																						String term = new String(this.buffer);
																						if(term!=null && !term.trim().equals("")){
																							tmpDoc.addField("pageStart", term);
																						}
																					}
																					else
																						//page end
																						if(rawName.equalsIgnoreCase("bibo:pageEnd")){
																							this.ispgEnd = false;
																							String term = new String(this.buffer);
																							if(term!=null && !term.trim().equals("")){
																								tmpDoc.addField("pageEnd", term);
																							}
																						}
																						else
																							//type
																							if(rawName.equalsIgnoreCase("dct:type")){
																								this.isType = false;
																								String term = new String(this.buffer);
																								if(term!=null && !term.trim().equals("")){
																									tmpDoc.addField("type", term);
																								}
																							}
																							else
																								//extent
																								if(rawName.equalsIgnoreCase("dct:extent") && tmpDoc!=null){
																									this.isExtent = false;
																									String term = new String(this.buffer);
																									if(term!=null && !term.trim().equals("")){
																										tmpDoc.addField("extent", term);
																									}
																								}
																								else
																									//medium
																									if(rawName.equalsIgnoreCase("dct:medium") && tmpDoc!=null){
																										this.isMedium = false;
																										String term = new String(this.buffer);
																										if(term!=null && !term.trim().equals("")){
																											tmpDoc.addField("medium", term);
																										}
																									}
																									else
																										//isbn
																										if(rawName.equalsIgnoreCase("bibo:isbn") && tmpDoc!=null){
																											this.isIsbn = false;
																											String term = new String(this.buffer);
																											if(term!=null && !term.trim().equals("")){
																												tmpDoc.addField("ISBN", term);
																											}
																										}
																										else
																											//doi
																											if(rawName.equalsIgnoreCase("bibo:doi") && tmpDoc!=null){
																												this.isDoi = false;
																												String term = new String(this.buffer);
																												if(term!=null && !term.trim().equals("")){
																													tmpDoc.addField("doi", term);
																												}
																											}
																											else
																												//right
																												if(rawName.equalsIgnoreCase("dct:rights") && tmpDoc!=null){
																													this.isRight = false;
																													String term = new String(this.buffer);
																													if(term!=null && !term.trim().equals("")){
																														tmpDoc.addField("rigth", term);
																													}
																												}
																												else
																													//fulltext
																													if(rawName.equalsIgnoreCase("bibo:uri") && tmpDoc!=null){
																														this.isFulltext = false;
																														String term = new String(this.buffer);
																														if(term!=null && !term.trim().equals("")){
																															tmpDoc.addField("fulltext", term);
																														}
																													}
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
		if(this.isTitle || this.isAlternative || this.isDateSubmitted || this.isIdentifier
				|| this.isISSN || this.isCitationTitle || this.isConferenceTitle || this.isPartOfUrl
				|| this.isOrgName || this.isAuthorName || this.isPublisherName || this.isDatePublication
				|| this.isSubject || this.isDescription || this.isAbstract || this.ispgEnd || this.ispgStart
				|| this.isLanguage || this.isVolume || this.isIssue || this.isType
				|| this.isExtent || this.isMedium || this.isIsbn || this.isDoi || this.isRight || this.isFulltext
				){
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

	public static void main(String[] args) throws SolrServerException, IOException{
		SolrServer server = SolrFactory.startSolr();
		//String filename = "Z:/RDF_Output/2015/CN2015_0.rdf";
		String filename = "C:/Users/celli/Desktop/PH2015_0.rdf";
		new AGRISRDF_Parser(filename, server, null);
		server.optimize();
	}
	 */ 

}
