package org.fao.agris_indexer.util;

/**
 * Translate dc:type code into String
 * @author celli
 *
 */
public class AGRISAPTypeTranslation {
	
	public static String translate(String code){
		String lowerCode = code.toLowerCase();
		if(code.equalsIgnoreCase("N"))
			return "Numerical data";
		if(code.equalsIgnoreCase("C"))
			return "Standard";
		if(code.equalsIgnoreCase("D"))
			return "Drawing";
		if(code.equalsIgnoreCase("E"))
			return "Summary";
		if(code.equalsIgnoreCase("K")
				|| lowerCode.contains("conference"))
			return "Conference";
		if(code.equalsIgnoreCase("P"))
			return "Patent";
		if(code.equalsIgnoreCase("Q"))
			return "Lit. Review";
		if(code.equalsIgnoreCase("R"))
			return "Directory";
		if(code.equalsIgnoreCase("X"))
			return "Extension";
		if(code.equalsIgnoreCase("T"))
			return "Thesaurus";
		if(code.equalsIgnoreCase("M"))
			return "News";
		if(code.equalsIgnoreCase("H")
				|| lowerCode.contains("handbook"))
			return "Handbook/Manual";
		if(code.equalsIgnoreCase("A") || lowerCode.contains("report"))
			return "Report";
		if(code.equalsIgnoreCase("U")
				|| lowerCode.contains("thesis")
				|| lowerCode.contains("tesi")
				|| lowerCode.contains("teses")
				|| lowerCode.contains("master")
				|| lowerCode.contains("msc")
				|| lowerCode.contains("phd")
				|| lowerCode.contains("doctoral")
				|| lowerCode.contains("dissertation"))
			return "Thesis";
		if(code.equalsIgnoreCase("V"))
			return "Non-Conventional";
		if(code.equalsIgnoreCase("Y"))
			return "Maps or Atlases";
		if(code.equalsIgnoreCase("G"))
			return "Graphics";
		if(code.equalsIgnoreCase("W"))
			return "Web site";
		if(code.equalsIgnoreCase("L"))
			return "Film";
		if(code.equalsIgnoreCase("A"))
			return "Encyclopaedia";
		if(code.equalsIgnoreCase("Y"))
			return "Speech";
		if(code.equalsIgnoreCase("O") || lowerCode.contains("dictionary"))
			return "Dictionary";
		if(code.equalsIgnoreCase("B"))
			return "Directory";
		if(code.equalsIgnoreCase("F") || lowerCode.contains("image") || lowerCode.contains("picture")
				|| lowerCode.contains("photo"))
			return "Image";
		if(code.equalsIgnoreCase("G"))
			return "Manuscript";
		if(code.equalsIgnoreCase("S") || lowerCode.contains("musical"))
			return "Sound/music";
		if(code.equalsIgnoreCase("Z") || lowerCode.contains("bibliograph"))
			return "Bibliography";
		if(lowerCode.contains("article")
				|| lowerCode.contains("articolo")
				|| lowerCode.contains("artigo")
				|| lowerCode.contains("journal")
				|| lowerCode.contains("peer")
				|| lowerCode.contains("monograph")
				|| lowerCode.contains("meeting"))
			return "Journal Article";
		if(lowerCode.contains("book"))
			return "Book";
		if(lowerCode.contains("preprint"))
			return "Preprint";
		if(lowerCode.contains("working"))
			return "Working Paper";
		if(lowerCode.contains("event"))
			return "Event";
		return "Other";
	}

}
