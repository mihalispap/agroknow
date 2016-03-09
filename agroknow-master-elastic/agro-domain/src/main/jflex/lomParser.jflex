package com.agroknow.domain.parser.jflex;

import java.util.HashSet ;
import java.lang.StringBuilder ;

%%

%class LOMParser %public
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

%state LOM
%state GENERAL
%state IDENTIFIER
%state TECHNICAL
%%

"<lom"	{
		yybegin(LOM) ;
		locations = new HashSet<String>() ;
		identifiers = new HashSet<String>() ;
	}

<LOM>	{
			"<general"	
			{
				yybegin( GENERAL ) ;	
			}
			"<technical"
			{
				yybegin( TECHNICAL ) ;	
			}
		}

<GENERAL>	{
				"<identifier"
				{
					yybegin( IDENTIFIER ) ;
					tmp = new StringBuilder() ;
				}
				"</general>"
				{
					yybegin( LOM ) ;
				}
			}

<IDENTIFIER>	{
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
				    	yybegin( GENERAL ) ;
				    }
				}
				
<TECHNICAL>	{
				"<location>".+"</location>"
				{
					locations.add( extract( yytext() ).trim() ) ;
				}
				"</technical>"
				{
					yybegin( LOM ) ;
				}
			}

.|\n {}
