package org.fao.agris_sitemap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.fao.agris_indexer.Defaults;
import org.fao.agris_sitemap.parser.ARN_AGRISRDF_Parser;
import org.xml.sax.SAXException;

/**
 * Builds the sitemap files. Requires two command line parameters: the location
 * of the RDF/XML repository and the folder that will contain the sitemaps.
 * The location of the RDF/XML repository must be a directory that has the name of a year
 * (e.g. 1999/) or the parent directory that contains years directories.
 * 
 * e.g.: java org.fao.agris_sitemap.SitemapsGen /work/agris/RDF_Output/2016/ /work/htdocs/sitemap/
 * 
 * @author celli
 */
public class SitemapsGen {

	private File currentXmlDir;	//the directory containing XML files
	private File sitemap;	//map the sitemap directory

	private BufferedWriter out;
	private String filePathPrefix;	//prefix for filename

	//for sitemap files dimension
	private int numRecords;
	private int fileSuffix;

	//maximum number of URLs per file
	private int maxArnPerFile;

	//for XML sitemap file
	private final String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private final String urlset = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";

	//addresses
	private final String headerQueryRDF = Defaults.getString("mashup_prefix");

	/**
	 * arg[0] is the path to the XML repository
	 * arg[1] is the path to the directory where sitemaps will be placed
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException{
		if(args.length==2){
			//repository's path
			String path = args[0];
			String sitemap = args[1];
			File xmlDir = new File(path);
			if(xmlDir.exists()){
				File mapDir = new File(sitemap);
				//start creation
				(new SitemapsGen(xmlDir, mapDir)).startCreation();
			}
			else {
				System.out.println("Source XML directory "+args[0]+ "does not exist");
			}
		}
	}

	/**
	 * @param xmlD the directory containing XML files
	 * @param map the sitemap directory
	 */
	public SitemapsGen(File xmlD, File map){
		this.currentXmlDir = xmlD;
		this.sitemap = map;
		this.maxArnPerFile = Integer.parseInt(Defaults.getString("max_urls_per_file"));
	}

	/*
	 * Start the creation of an XML file for each year
	 */
	private void startCreation() throws IOException, ParserConfigurationException, SAXException{
		//look for XML files in the current directory
		File[] listFiles = currentXmlDir.listFiles();
		//working directory
		this.sitemap.mkdir();	

		//try to see if we are at second level, i.e. the directory is a year directory
		if(currentXmlDir.isDirectory() && (currentXmlDir.getName().startsWith("19") || currentXmlDir.getName().startsWith("20"))){
			//initialize parameters
			this.numRecords = 0;
			this.fileSuffix = 0;
			//start
			this.filePathPrefix = this.sitemap+"/sitemap"+currentXmlDir.getName()+"_";
			String outputFile = this.filePathPrefix+this.fileSuffix+".xml";
			System.out.println("Sitemap file: "+outputFile);

			this.initializeOutputFile(outputFile);
			this.createSiteMapForDirectory(currentXmlDir);
			out.write("</urlset>");
			out.flush();
		} else {
			//try to see if current directory contains year directories
			for(File containedFile: listFiles){
				//for each year
				String containedFilename = containedFile.getName().toLowerCase();
				//currentXmlDir contains year directories
				if(containedFile.isDirectory() && (containedFilename.startsWith("19") || containedFilename.startsWith("20"))){
					//initialize parameters
					this.numRecords = 0;
					this.fileSuffix = 0;
					//start
					this.filePathPrefix = this.sitemap+"/sitemap"+containedFilename+"_";
					String outputFile = this.filePathPrefix+this.fileSuffix+".xml";
					System.out.println("Sitemap file: "+outputFile);

					this.initializeOutputFile(outputFile);
					this.createSiteMapForDirectory(containedFile);
					out.write("</urlset>");
					out.flush();
				} 
			}
		}	
	}

	/*
	 * Initialize a sitemap file
	 */
	private void initializeOutputFile(String path) throws IOException{
		out = new BufferedWriter(new FileWriter(path));
		out.write(this.header);
		out.newLine();
		out.write(this.urlset);
		out.newLine();
	}

	/*
	 * Recursive creation of the sitemap
	 */
	private void createSiteMapForDirectory(File dir) throws ParserConfigurationException, SAXException, IOException{
		//look for XML files in the current directory
		File[] listFiles = dir.listFiles();
		//list of sub-directories
		Set<File> listDirs = new TreeSet<File>();
		//scan files
		for(File contentFile: listFiles){
			String filename = contentFile.getName().toLowerCase();
			if(filename.toLowerCase().contains(".xml") || filename.toLowerCase().contains(".rdf")){
				//Parse the XML file to extract URLs
				this.parseXML(contentFile);
			}
			else {
				//is a directory
				if(contentFile.isDirectory())
					listDirs.add(contentFile);
			}
		}
		//recursive application
		listFiles = null;
		for(File subDir: listDirs)
			this.createSiteMapForDirectory(subDir);
	}

	/*
	 * Parse the XML file to extract URLs
	 */
	private void parseXML(File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		//find all ARN of current file
		List<String> arns = new ArrayList<String>();
		new ARN_AGRISRDF_Parser(xmlFile, arns);

		//check current file dimension
		this.numRecords += arns.size();

		//if the threshold is not reached, write current ARNs
		if(this.numRecords < this.maxArnPerFile){
			//build urls
			this.writeURL(arns);
		}
		else {
			//truncate
			int currentNumberRecordFile = this.numRecords - arns.size();
			if(currentNumberRecordFile < this.maxArnPerFile){
				int toIndexLimit = this.maxArnPerFile - currentNumberRecordFile;
				List<String> subItems = arns.subList(0, toIndexLimit);
				arns = arns.subList(toIndexLimit, arns.size());
				this.writeURL(subItems);
			}
			//finilize current file
			out.write("</urlset>");
			out.flush();

			//create new file
			this.numRecords = arns.size();
			this.fileSuffix++;
			String outputFile = this.filePathPrefix+this.fileSuffix+".xml";
			System.out.println("Sitemap file: "+outputFile);
			this.initializeOutputFile(outputFile);
			//build urls
			this.writeURL(arns);
		}
	}

	/*
	 * Write a set of URL on the output file
	 */
	private void writeURL(List<String> arns) throws IOException{
		for(String arn: arns){
			//AGRIS RDF URL
			out.write("<url><loc>"+this.headerQueryRDF+arn+"</loc></url>");
			out.newLine();
		}
	}

}
