package com.agroknow.domain.parser.jflex;

import java.util.HashSet ;
import java.lang.StringBuilder ;

%%

%class DCParser %public
%standalone
%unicode

%{ 
	////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	// VARIABLES
	////////////////////////////////////////////////////////////////////////////////
	
	private HashSet<String> locations = new HashSet<String>() ;
	private HashSet<String> identifiers =  new HashSet<String>() ;
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

%state DC
%%

"<dc:identifier>http://".+"</dc:identifier>"	
{
		locations.add( extract( yytext() ).trim() ) ;
}

"<dc:identifier>ftp://".+"</dc:identifier>"	
{
		locations.add( extract( yytext() ).trim() ) ;
}

"<dc:identifier>".+"</dc:identifier>"	
{
		identifiers.add( extract( yytext() ).trim() ) ;
}

.|\n {}
