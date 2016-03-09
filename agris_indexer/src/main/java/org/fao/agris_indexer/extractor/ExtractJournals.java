package org.fao.agris_indexer.extractor;

import java.util.HashSet;
import java.util.Set;

import org.jfcutils.files.write.TXTWriter;
import org.jfcutils.http.GETHttpRequest;

public class ExtractJournals {

	/**
	 * Extracts all journals in a txt file. Jornals titles are not disambugated, only DISTINCT on raw data
	 * @param args
	 */
	public static void main(String[] args){
		try {
			String outputFile = "C:/Users/celli/Desktop/journals.txt";
			
			int maxNum = 6327302;
			int rows = 100000;
			int start = 0;
			
			String url = "http://lprapp14:9090/solr_agris/collection1/select?q=citationTitle%3A[*+TO+*]&fl=citationTitle&wt=csv&indent=true&csv.newline=%3B%3B&rows="+rows;
			
			GETHttpRequest req = new GETHttpRequest();
			//create unique set
			Set<String> allJournals = new HashSet<String>();
			while(start<maxNum){
				url = url + "&start="+start;
				for(String s: req.getUrlContentWithRedirect(url, 15000).split(";;"))
					if(!s.equals("citationTitle"))
						allJournals.add(s);
				start = start + rows;
				System.out.print(start + " ");
			}
			System.out.println();
			System.out.println(allJournals.size());
			
			TXTWriter writer = new TXTWriter();
			writer.writeLines(allJournals, outputFile);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
