package com.agroknow.domain.parser.jflex;

import java.util.HashSet ;
import java.lang.StringBuilder ;

%%

%class NSDLParser %public
%standalone
%unicode

%{ 
	////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	// VARIABLES
	////////////////////////////////////////////////////////////////////////////////
	
	private HashSet<String> locations ;
	private HashSet<String> identifiers ;
	private StringBuilder tmp ;

	
	 	
 	////////////////////////////////////////////////////////////////////////////////
	// SETTERS AND GETTERS
	////////////////////////////////////////////////////////////////////////////////
  	
  	public HashSet<String> getLocations()
  	{
  		return locations ;
  	}

  	public HashSet<String> getIdentifiers()
  	{
  		return identifiers ;
  	}
	
	////////////////////////////////////////////////////////////////////////////////
	// UTILITY METHODS
	////////////////////////////////////////////////////////////////////////////////


	private String extract( String element )
	{	
		return element.substring(element.indexOf(">") + 1 , element.indexOf("</") );
	}


                                           
%}

%state NSDL
%%

"<nsdl"	{
		yybegin(NSDL) ;
		locations = new HashSet<String>() ;
		identifiers = new HashSet<String>() ;
	}

<NSDL>	
{
	"<dc:identifier xsi:type=\"dct:URI\">".+"</dc:identifier>"	
	{
		locations.add( extract( yytext() ).trim() ) ;
	}
	
	"<dc:identifier xsi:type=\"nsdl_dc:ResourceHandle\">".+"</dc:identifier>"
	{
		identifiers.add( extract( yytext() ).trim() ) ;
	}
	
	"<dc:identifier xsi:type=\"nsdl_dc:NSDLPartnerID\">".+"</dc:identifier>"
	{
		identifiers.add( extract( yytext() ).trim() ) ;
	}
}

.|\n {}
