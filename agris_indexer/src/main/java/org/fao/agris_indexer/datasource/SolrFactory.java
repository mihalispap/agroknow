package org.fao.agris_indexer.datasource;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.fao.agris_indexer.Defaults;

/**
 * This class builds an instance of the Solr server to index data
 * @author celli
 *
 */
public class SolrFactory {

	//address
	private final static String solr_server_url = Defaults.getString("SolrServer");

	//instance
	private static SolrServer server;

	//singleton for the default server
	public static synchronized SolrServer startSolr() throws MalformedURLException{
		if(server==null)
			startRemoteSolr(solr_server_url);
		return server;
	}

	private static void startRemoteSolr(String remotePath) throws MalformedURLException{
		HttpSolrServer  solr = new HttpSolrServer(remotePath);
		//DefaultHttpClient client = (DefaultHttpClient) solr.getHttpClient();
		//UsernamePasswordCredentials defaultcreds = new UsernamePasswordCredentials("user", "user123");
		//client.getCredentialsProvider().setCredentials(AuthScope.ANY, defaultcreds);
		solr.setRequestWriter(new BinaryRequestWriter());	//java binary format
		server = solr;
	}

	/*
	 * TEST
	 */
	public static void main(String[] args) throws MalformedURLException, SolrServerException{
		SolrServer server = SolrFactory.startSolr();
		System.out.println(server.toString());
		SolrQuery slrQuery = new SolrQuery("rice");
		slrQuery.setStart(new Integer(0));	
		slrQuery.setRows(new Integer(3)); 
		QueryResponse response = server.query(slrQuery);
		SolrDocumentList list = response.getResults();
		System.out.println(list.getNumFound());
	}

}
