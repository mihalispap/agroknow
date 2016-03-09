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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.fao.agris_indexer.datasource.SolrFactory;
import org.fao.agris_indexer.parser.AGRISRDF_Parser;

/**
 * Requirements:
 * - filesystem strucure: $BASE/YEAR/rdf_files
 * - file names: starting with the 2 digits of the center
 * 
 * This application is the main responsible for indexing: it indexes all RDF/XML files in the specified folder.
 * The location of the Solr server is read from the default.properties file.
 * The default application can index the entire repository. 
 * Using a parameter allows the application to index another root directory, or a specific year ($BASE/YEAR).
 * In case of indexing a specific year, a list of centers to be exluded can be decalared in default.properties
 * 
 * @author celli
 *
 */
public class IndexRDF {
	
	private final static Logger log = Logger.getLogger(IndexRDF.class.getName());

	//Solr Server
	private SolrServer server;

	//For autosuggestions
	private Set<String> autosuggestions;

	//To exclude big centers in case of year indexing
	private static String exclude_from_remove_query = "";
	private static String[] exclude_centers;
	
	private static final int max_commit_files = 200; 

	//pattern to match years
	private final String year_pattern = "[1-2][0-9][0-9][0-9]";

	public IndexRDF() throws IOException{
		server = SolrFactory.startSolr();
	}

	/**
	 * Application entry point. If the input is a specific folder, deletes previously indexed documents.
	 * A specific folder may be the root, or a specific year.
	 * The expected diretoty structure is: $BASE/year/rdf_files
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
			toExclude = toExclude.toUpperCase();
			String[] toExcludeArr = toExclude.split(",");
			exclude_centers = Arrays.copyOf(toExcludeArr, toExcludeArr.length);
			for(String s: toExcludeArr)
				if(s!=null && s.trim().length()>0)
					exclude_from_remove_query = exclude_from_remove_query + " -fsURI:"+s.toUpperCase();
		}

		//start indexing
		IndexRDF indexer = new IndexRDF();
		if(args.length==1) {
			indexer.runSpecificIndexer(args[0]);
		}
		else
			indexer.indexAllXML();

		System.out.println();
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

		//check if we are indexing a year
		boolean isYear = false;
		if(Pattern.matches(year_pattern, pathParts[pathParts.length-1]))
			isYear = true;

		//build deletion query
		String delQuery = null;
		if(isYear)
			delQuery = this.buildDelQuery(pathParts[pathParts.length-1]);

		//deletion and indexing
		if(delQuery!=null || !isYear){
			System.out.println("The delete query is: "+delQuery);
			if(delQuery!=null)
				this.deleteByQuery(delQuery);
			this.indexAllXML(path, isYear);
		}
		else {
			log.log(Level.WARNING, "The path is not correct");
			log.log(Level.WARNING, "You can index a directory with AGRIS RDF/XML files");
			log.log(Level.WARNING, "Files path sould be: $BASE/YEAR/filename.rdf or $BASE/YEAR/filename.xml");
		}	
	}

	/*
	 * Build the deletion query
	 */
	private String buildDelQuery(String year){
		return "+date:" + year + exclude_from_remove_query;
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
	 * @param isYear true excludes autosuggestions 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void indexAllXML(String path, boolean isYear) throws SolrServerException, IOException{
		if(isYear)
			this.autosuggestions = null; //disable autosuggestions
		this.indexFolder(path, isYear);
	}

	/**
	 * Indexes all XMLs in the specified folder and subfolders
	 * @param xmlPath the specific path of the folder
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public void indexFolder(String xmlPath, boolean isYear) throws SolrServerException, IOException{
		File dir = new File(xmlPath);

		//print exclusion
		if(isYear)
			System.out.println("!!! The indexing process will exclude: "+Arrays.asList(exclude_centers));

		//parse the directory
		if(dir.exists()){
			//counter to commit
			int numberToCommit = 0;
			this.indexFolder(dir, numberToCommit, isYear);

			//server optimization
			System.out.println();
			System.out.println("##### optimization...");
			this.server.commit();
			this.server.optimize(true, true, 3);
		} else {
			log.log(Level.WARNING, "The directory "+xmlPath+" does not exist!");
		}
	}

	/*
	 * Private recursive method: it does not optimize the server but commits every 500 records
	 */
	private void indexFolder(File dir, int numberToCommit, boolean isYear) throws SolrServerException, IOException{
		System.out.println("// Directory "+dir.getName());
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
				if(filename.contains(".rdf") || filename.contains(".xml")){
					//check if the file starts with the center code of a center to exclude
					if(!isYear || !Arrays.asList(exclude_centers).contains(filename.substring(0, 2).toUpperCase())){
						filesAdded++;
						numberToCommit++;
						//new org.fao.agris_indexer.parser.AGRISAP_Parser(contentFile, this.server, autosuggestions);
						new AGRISRDF_Parser(contentFile, this.server, autosuggestions);
						//commit every max_commit_files documents
						if(numberToCommit%max_commit_files==0)
							this.server.commit();
					}
				}
				else {
					if(contentFile.isDirectory())
						listDirs.add(contentFile);
				}
			} catch (Exception e){
				log.log(Level.WARNING, "\nException: "+ e.getMessage());
				log.log(Level.WARNING, e.toString());
				log.log(Level.WARNING, "----> "+filename);
			}
		}
		//commit directory
		System.out.println("--->>> added "+filesAdded+" files");
		System.out.println();
		//recursive application
		listFiles = null;
		for(File subDir: listDirs){
			this.indexFolder(subDir, numberToCommit, isYear);
		}
	}

}
