package org.fao.agris_sitemap.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ARN_AGRISRDF_Parser extends DefaultHandler {

	//parser
	private SAXParserFactory spf;
	private SAXParser saxParser;
	private File file;
	
	//for parsing
	private boolean isIdentifier;
	private StringBuffer buffer;

	//result
	private List<String> arns;

	/**
	 * Given a source RDF/XML file, get the list of ARNs
	 * @param xmlFile the source AGRIS RDF 2.0 XML file
	 * @param arns the list of ARNs to fill (it is the output)
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public ARN_AGRISRDF_Parser(File xmlFile, List<String> arns) throws ParserConfigurationException, SAXException, IOException{
		this.arns = arns;
		this.spf = SAXParserFactory.newInstance();
		this.spf.setNamespaceAware(false);
		this.spf.setValidating(false);
		this.spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		this.saxParser = this.spf.newSAXParser();
		this.file = xmlFile;
		try{
			this.saxParser.parse(this.file, this);
		}
		catch(SAXParseException e){
			System.out.println(xmlFile.getName());
			e.printStackTrace();
		}
		catch(FileNotFoundException e){
			System.out.println(xmlFile.getName());
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////////////////////////
	// Event handlers.
	////////////////////////////////////////////////////////////////////

	public void startElement (String namespaceURI, String localName, String rawName, Attributes atts) throws SAXException {	
		//identifier ARN
		if(rawName.equalsIgnoreCase("dct:identifier")){
			this.isIdentifier = true;
			this.buffer = new StringBuffer();
		}
	}
	
	public void endElement (String namespaceURI, String localName, String rawName) throws SAXException {
		//identifier ARN
		if(rawName.equalsIgnoreCase("dct:identifier")){
			this.isIdentifier = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals(""))
				this.arns.add(term);
		}
	}
	
	public void characters(char[] ch, int start, int length) {
		//title, journal title, abstract
		if(this.isIdentifier){
			this.buffer.append(ch, start, length);
		}
	}

}
