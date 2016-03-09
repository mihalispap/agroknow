package com.agroknow.domain.parser.jflex;

import java.util.HashSet ;
import java.lang.StringBuilder ;

%%

%class LOMILOXParser %public
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

%state ILOX
%state HEADER
%state IDENTIFIER
%state ITEM
%state LOCATION
%%

"<work"	
{
		yybegin(HEADER) ;
		locations = new HashSet<String>() ;
		identifiers = new HashSet<String>() ;
}

<HEADER>	
{
	"<identifier"
	{
		yybegin( IDENTIFIER ) ;
		tmp = new StringBuilder() ;
	}
	
	"<description>"
	{
		yybegin( ILOX ) ;
	}
}

<ILOX>
{
	"<item>"
	{
		yybegin( ITEM ) ;
	}
}

<IDENTIFIER>	
{
	"<catalog>".+"</catalog>"
	{
		tmp.append( extract( yytext() ).trim() ) ;
	}
	
	"<entry>".+"</entry>"
	{
		tmp.append( extract( yytext() ).trim() ) ;
	}
	
	"</identifier>"
	{
		identifiers.add( tmp.toString() ) ;
		yybegin( HEADER ) ;
	}
}
				
<ITEM>	
{
	"<location>"
	{
		yybegin( LOCATION );
	}
	
	"</item>"
	{
		yybegin( ILOX ) ;
	}
}

<LOCATION>
{
	"<uri>".+"</uri>"
	{
		locations.add( extract( yytext() ).trim() ) ;
	}
	
	"</location>"
	{
		yybegin( ITEM ) ;
	}
}

.|\n {}
