package org.fao.agris_indexer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.fao.agris_indexer.datasource.SolrFactory;
import org.fao.agris_indexer.parser.AGRISAP_Parser;
import org.fao.agris_indexer.util.Cleaner;

/**
 * This application is the main responsible for indexing: it indexes all XML files in the specified folder.
 * The location of the Solr server is read from the default.properties file.
 * The default application can index the entire repository. 
 * Using a parameter allows the application to index another root directory, or a specific year ($BASE/YEAR), or a specific center ($BASE/YEAR/CENTER_CODE).
 * In case it indexes a specific year, big centers like AV, OV, US and GB are excluded. They have to be indexed by specific center.
 * 
 * It does not check the DTD
 * @author celli
 * @deprecated
 */
public class IndexXML {

	//Solr Server
	private SolrServer server;

	//For autosuggestions
	private Set<String> autosuggestions;

	//To exclude AV, OV and US
	private static String exclude_string = "";
	private static String[] exclude_centers;

	//pattern to mtch years
	private final String year_pattern = "[1-2][0-9][0-9][0-9]";

	public IndexXML() throws IOException{
		server = SolrFactory.startSolr();
	}

	/**
	 * Application entry point. If the input is a specific folder, deletes previously indexed documents.
	 * @param args source XML location
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public static void main(String[] args) throws IOException, SolrServerException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		//check centers to exclude
		String toExclude = Defaults.getString("exclude_string");
		if(toExclude!=null){
			String[] toExcludeArr = toExclude.split(",");
			exclude_centers = Arrays.copyOf(toExcludeArr, toExcludeArr.length);
			for(String s: toExcludeArr)
				exclude_string = exclude_string + "-URI:"+s.toUpperCase()+" ";
			exclude_string = Cleaner.trimRight(exclude_string);
		}

		IndexXML indexer = new IndexXML();
		if(args.length==1) {
			indexer.runSpecificIndexer(args[0]);
		}
		else
			indexer.indexAllXML();

		System.out.println("Start Date Time : " + dateFormat.format(date));
		System.out.println("End Date Time : " + dateFormat.format(new Date()));
	}

	/*
	 * Index the directory passes as parameter
	 */
	private void runSpecificIndexer(String path) throws SolrServerException, IOException{
		//find year and center
		String[] pathParts = path.split("/");
		if(pathParts==null || pathParts.length<2)
			pathParts = path.split("\\\\");

		//check if we are indexing a year or a specific center
		//needed to exclude big centers if indexing a year
		boolean isYear = false;
		if(Pattern.matches(year_pattern, pathParts[pathParts.length-1]))
			isYear = true;

		//build deletion query
		boolean queryFound = false;
		String delQuery = this.buildDelQuery(pathParts);
		if(delQuery!=null)
			queryFound = true;

		//deletion and indexing
		if(queryFound){
			System.out.println("The delete query is: "+delQuery);
			this.deleteByQuery(delQuery);
			this.indexAllXML(path, isYear);
		}
		else {
			System.out.println("The path is not correct");
			System.out.println("You can index a directory with XML AGRIS AP files");
			System.out.println("Files path sould be: something/YEAR/CENTER_CODE/filename.xml");
		}	
	}

	/*
	 * Build the deletion query
	 * Case 1: something/YEAR
	 * Case 2: something/YEAR/CENTER_CODE
	 */
	private String buildDelQuery(String[] pathParts){
		String delQuery = null;
		//case 1
		if(pathParts.length>=1 && Pattern.matches(year_pattern, pathParts[pathParts.length-1])){
			delQuery = "+date:"+pathParts[pathParts.length-1]+" "+exclude_string;
		} 
		//case 2
		else {
			if(pathParts.length>=2 && Pattern.matches(year_pattern, pathParts[pathParts.length-2])){
				delQuery = "+date:"+pathParts[pathParts.length-2]+" +URI:"+pathParts[pathParts.length-1];
			}
		}
		return delQuery;
	}

	/**
	 * Delete records, given a query
	 * @param query the query that causes deletion
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void deleteByQuery(String query) throws SolrServerException, IOException{
		server.deleteByQuery(query);
		server.commit();
	}

	/**
	 * Indexes all XMLs in the folder specified in the configuration file, and subfolders
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public void indexAllXML() throws SolrServerException, IOException{
		autosuggestions = new HashSet<String>();//autosuggestions only when indexing the whole repository
		String xmlPath = Defaults.getString("XmlInput");
		this.indexFolder(xmlPath, false);
	}

	/**
	 * Indexes all XMLs in the folder specified as argument, and subfolders
	 * @param path the path of input documents to be indexed
	 * @param isYear true if we are indexing a year so to exclude big centers 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void indexAllXML(String path, boolean isYear) throws SolrServerException, IOException{
		this.autosuggestions = null; //disable autosuggestions
		this.indexFolder(path, isYear);
	}

	/**
	 * Indexes all XMLs in the specified folder and subfolders
	 * @param xmlPath the specific path of the folder
	 * @param isYear true if we are indexing a year so to exclude big centers 
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public void indexFolder(String xmlPath, boolean isYear) throws SolrServerException, IOException{
		File dir = new File(xmlPath);

		//print exclusion
		if(isYear)
			System.out.println("!!! The indexing process will exclude: "+Arrays.asList(exclude_centers));
		if(dir.exists()){
			//counter to commit
			int numberToCommit = 0;
			this.indexFolder(dir, isYear, numberToCommit);

			//server optimization
			System.out.println();
			System.out.println("----> optimization");
			this.server.commit();
			this.server.optimize(true, true, 3);
		} else {
			System.out.println("The directory "+xmlPath+" does not exist!");
		}
	}

	/*
	 * Private recursive method: it does not optimize the server but commits every 500 records
	 */
	private void indexFolder(File dir, boolean isYear, int numberToCommit) throws SolrServerException, IOException{
		//check condition:
		//if we are indexing a year and this is a big center, skip!
		if(isYear && Arrays.asList(exclude_centers).contains(dir.getName().toUpperCase())){
			return;
		}
		else {
			System.out.print("// Directory "+dir.getName());
			//look for XML files in the current directory and sort
			File[] listFiles = dir.listFiles();
			Arrays.sort(listFiles);
			//directories
			Set<File> listDirs = new TreeSet<File>();
			//files added
			int filesAdded = 0;
			//scan files
			for(File contentFile: listFiles){
				String filename = contentFile.getName().toLowerCase();
				try {
					if(filename.contains(".xml")){
						filesAdded++;
						numberToCommit++;
						new AGRISAP_Parser(contentFile, this.server, autosuggestions);
						//commit every 500 documents
						if(numberToCommit%500==0)
							this.server.commit();
					}
					else {
						if(contentFile.isDirectory())
							listDirs.add(contentFile);
					}
				} catch (Exception e){
					System.out.println("\nException: "+ e.getMessage());
					e.printStackTrace();
					System.out.println("----> "+filename);
				}
			}
			//commit directory
			System.out.println(": added "+filesAdded+" files");
			//recursive application
			listFiles = null;
			for(File subDir: listDirs){
				this.indexFolder(subDir, isYear, numberToCommit);
			}
		}
	}

}
