package org.fao.agris_indexer.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class offers static methods to clean a string, even representing a specific concept like a language
 * @author celli
 *
 */
public class Cleaner {

	/**
	 * Remove beginning whitespaces
	 * @param s the string to clean
	 * @return the string without beginning whitespaces
	 */
	public static String trimLeft(String s) {
		return s.replaceAll("^\\s+", "");
	}

	/**
	 * Remove ending whitespaces
	 * @param s the string to clean
	 * @return the string without ending whitespaces
	 */
	public static String trimRight(String s) {
		return s.replaceAll("\\s+$", "");
	} 
	
	/**
	 * Clean ASC returning a clean list of codes
	 */
	public static List<String> cleanASC(String term){
		List<String> result = new LinkedList<String>();
		//check term
		if(term!=null){
			term = term.replaceAll("#", "");
			if(term.length()>2){
				if(term.length()>=3){//ok
					result.add(term);
				}
				else {
					//split semicolon
					String[] terms = term.split(";");
					//split comma
					if(terms.length==1)
						terms = term.split(",");
					//split :
					//if(terms.length==1)
					//	terms = term.split(":");
					//split b
					if(terms.length==1)
						terms = term.split(" b ");
					for(String s: terms){
						s = s.replaceAll("\t", "");
						s = s.replaceAll(" ", "");
						s = Cleaner.trimLeft(s);
						s = Cleaner.trimRight(s);
						if(s.length()>=3){
							result.add(s);
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Filter languages, removing String that are not languages. Useful in the indexing process.
	 * @param docLanguage the language
	 * @return the language or null if the source string is not a language
	 */
	public static String filterDirtyLanguages(String docLanguage){
		if(docLanguage!=null){
			docLanguage = docLanguage.toLowerCase();
			if(docLanguage.contains("7") || docLanguage.contains("026803") || docLanguage.contains("5") || docLanguage.contains("8")
					 || docLanguage.contains("1") || docLanguage.contains("9") || docLanguage.contains("0") || docLanguage.contains("6")
					 || docLanguage.contains("3") || docLanguage.contains("2") || docLanguage.equals("e")
					 || docLanguage.equals("tableaux") || docLanguage.equals("from") || docLanguage.equals("summary only"))
				return null;
		}
		return docLanguage;
	}
	
	/**
	 * Return a cleaned issn
	 * @param issn the issn to clean
	 * @return a cleaned issn
	 */
	public static String cleanISSN(String issn){
		String result = issn.replace("(", "");
		result = result.replace(")", "");
		result = result.replace("/", "");
		result = result.replace(".", "");
		result = result.replace("Print", "");
		result = result.replace("print", "");
		result = result.replace("online", "");
		result = result.replace("Online", "");
		result = result.replace(" ", "");
		result = result.replace("\t", "");
		result = result.replace("_", "-");
		result = result.replace("O", "0");
		result = result.trim();
		result = result.toUpperCase();
		if(result.length()==10){
			int pos = result.lastIndexOf("-");
			if(pos!=-1)
				result = result.substring(0, pos)+result.substring(pos+1, result.length());
		}
		if(result.length()==8 && result.indexOf("-")==-1){
			result = result.substring(0, 4)+"-"+result.substring(4, result.length());
		}
		return result;
	}
	
	/**
	 * Check if the issn is syntactically correct
	 * @param issn the issn to check
	 * @return true if the issn is syntactically correct
	 */
	public static boolean isISSN(String issn){
		String pattern = "[0-9][0-9][0-9][0-9][/-][0-9][0-9][0-9][0-9[X]]";
		return Pattern.matches(pattern, issn);
	}

}
