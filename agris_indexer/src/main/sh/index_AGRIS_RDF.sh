#!/bin/sh
#
# Requirements:
# - filesystem strucure: $BASE/YEAR/rdf_files
# - file names: starting with the 2 digits of the center
#
# Script for execute indexing application of AGRIS RDF/XML files
# The location of the Solr server is read from the default.properties file.
# By default, the application indexs the entire repository avaulable at the position specified in the default.properties file. 
# Using a parameter allows the application to index another root directory, or a specific year directory ($BASE/YEAR).
# In case of indexing a specific year, a list of centers to be exluded can be decalared in default.properties
#
# Examples of usage:
# ./index_AGRIS_RDF.sh
# ./index_AGRIS_RDF.sh /work/agris/RDF_Output/2016
#

cd bin 

if [ $# -ne 1 ]  # number of arguments passed to script
then
    echo "Use default source directory"
    exec java -classpath ".:../resources:../lib/*" -Xss64M -Xmx2048M org.fao.agris_indexer.IndexRDF
else
    echo "Use \"$1\" XML source directory"
    exec java -classpath ".:../resources:../lib/*" -Xss64M -Xmx2048M org.fao.agris_indexer.IndexRDF $1
fi

exit