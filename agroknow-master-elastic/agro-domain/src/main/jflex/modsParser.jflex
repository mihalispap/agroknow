package com.agroknow.domain.parser.jflex;

import java.util.HashSet ;
import java.lang.StringBuilder ;

%%

%class MODSParser %public
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

%state MODS
%%

"<mods:mods"	{
		yybegin( MODS ) ;
		locations = new HashSet<String>() ;
		identifiers = new HashSet<String>() ;
	}

<MODS>	
{
	"<mods:identifier type=\"uri\">".+"</mods:identifier>"	
	{
		locations.add( extract( yytext() ).trim() ) ;
		identifiers.add( extract( yytext() ).trim() ) ;
	}
	
	"<mods:identifier type=\"issn\">".+"</mods:identifier>"
	{
		identifiers.add( extract( yytext() ).trim() ) ;
	}
}

.|\n {}
