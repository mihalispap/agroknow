package org.fao.agris_indexer.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.parsers.DOMParser;
import org.fao.agris_indexer.Defaults;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Finds a center from the given ARN. Manage the map of all AGRIS centers.
 * @author celli
 *
 */
public class AGRISCentres {

	// The XML files containing the center translation mappings.
	public static final String FILE_INPUTCENTRES = Defaults.getString("AgrisCentresFile");

	// A private method for holding information about an AGRIS Centre
	private static Hashtable<String, String> buildCentreHash(Element centre) {
		Hashtable<String, String> centreHash = new Hashtable<String, String>();
		// Index all child nodes from centre node.
		NodeList children = centre.getElementsByTagName("*");
		for (int i = 0; i < children.getLength(); i++) {
			String key   = children.item(i).getNodeName();
			if(children.item(i).getFirstChild()!=null){
				String value = children.item(i).getFirstChild().getNodeValue();
				centreHash.put(key, value);
			}
		}
		return centreHash;
	}

	// The list of AGRIS Centres
	private static ArrayList<Hashtable<String, String>> _agrisCentreList;

	static {
		DOMParser parser = new DOMParser();
		// Parse the translation file.
		try {
			InputStream inCentStr = new URL(FILE_INPUTCENTRES).openStream();
			InputSource source = new InputSource(inCentStr);
			parser.parse(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Get the document out of the parser and setup the hashtable.
		Document document = parser.getDocument();
		_agrisCentreList = new ArrayList<Hashtable<String, String>>();
		// For all agris centres
		NodeList centres = document.getElementsByTagName("GILWD_ADMIN_INPUTCENTER");
		for (int i = 0; i < centres.getLength(); i++) {
			_agrisCentreList.add(buildCentreHash((Element) centres.item(i)));
		}
	}

	/**
	 * The public interface: get a centre reference from an ARN.
	 * @param arn the ARN of the record
	 * @return the map with the information of the found center, like CENTERCITY, COORDINATES, CENTERNAME, CENTRE_CODE_KEY...
	 */
	public static Hashtable<String, String> getCentreByARN(String arn) {
		Hashtable<String, String> bestMatch = new Hashtable<String, String>();
		if(arn!=null){
			// First two chars of ARN is country code.
			String country = arn.substring(0, 2);
			// IF it's a 12-character ARN, get the centre subcode.
			String subCentreCode;
			if (arn.length() >= 12)
				subCentreCode = arn.substring(6, 7);
			else
				subCentreCode = "0";
			
			//check special case: Scielo
			subCentreCode = checkScieloSubcenters(country, subCentreCode);
			
			//perform the search
			for (Hashtable<String, String> centre : _agrisCentreList) {
				String countryCode   = centre.get("COUNTRYCODE");
				String centreCodeKey = centre.get("CENTRE_CODE_KEY");
				if (country.equalsIgnoreCase(countryCode)) {
					// Exact match
					if (subCentreCode.equalsIgnoreCase(centreCodeKey)) {
						bestMatch = centre;
						break;
					}
					// Otherwise, match on national centre.
					else if (centreCodeKey.equals("0")) {
						bestMatch = centre;
					}
				}
			}
			//second step: no center found (<1998)
			if(bestMatch==null || bestMatch.size()==0){
				for (Hashtable<String, String> centre : _agrisCentreList) {
					String countryCode = centre.get("COUNTRYCODE");
					if (country.equalsIgnoreCase(countryCode)){
						bestMatch = centre;
						break;
					}
				}
			}
		}
		return bestMatch;
	}
	
	/**
	 * Get the country code of a center given the ARN
	 * @param arn the ARN of the record
	 * @return the country code of a center
	 */
	public static String getCenterKeyByARN(String arn){
		//look for center
		Hashtable<String, String> bestMatch = getCentreByARN(arn);
		if(bestMatch!=null && bestMatch.size()!=0){
			return bestMatch.get("CENTRE_CODE_KEY");
		}
		return null;
	}

	/**
	 * Returns the map of all the AGRIS centers
	 * @return the map of all the AGRIS centers
	 */
	public static List<Hashtable<String, String>> getAgrisCentreList() {
		return _agrisCentreList;
	}
	
	/**
	 * Compute Scielo subCentreCode after the refactoring of the AGRIS centers file,
	 * i.e. after the merging of Scielo National subcenter.
	 * @param country
	 * @param subCentreCode
	 * @return the subcentercode of a Scielo record (exception in the AGRIS centers database)
	 */
	public static String checkScieloSubcenters(String country, String subCentreCode){
		if(country.toLowerCase().equals("xs"))
			subCentreCode = "0";
		else if(country.toLowerCase().equals("ar") && 
				subCentreCode.toLowerCase().compareTo("a")>=0 &&
				subCentreCode.toLowerCase().compareTo("m")<=0)
			subCentreCode = "A";
		else if(country.toLowerCase().equals("cl") && 
				subCentreCode.toLowerCase().compareTo("a")>=0 &&
				subCentreCode.toLowerCase().compareTo("k")<=0)
			subCentreCode = "A";
		else if(country.toLowerCase().equals("co") && 
				subCentreCode.toLowerCase().compareTo("a")>=0 &&
				subCentreCode.toLowerCase().compareTo("h")<=0 &&
				!subCentreCode.equalsIgnoreCase("f"))
			subCentreCode = "A";
		else if(country.toLowerCase().equals("cu") && 
				(
				subCentreCode.equalsIgnoreCase("z") ||
				subCentreCode.equalsIgnoreCase("t") ||
				subCentreCode.equalsIgnoreCase("v") ||
				subCentreCode.equalsIgnoreCase("u") ||
				subCentreCode.equalsIgnoreCase("a")
				))
			subCentreCode = "Z";
		return subCentreCode;
	}

}