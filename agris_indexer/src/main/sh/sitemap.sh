#!/bin/sh
# 
# Script for execute sitemap application 
# Usage: ./sitemaps.sh /work/agris/RDF_Output/2016/ /work/htdocs/sitemap/

cd bin

if [ $# -ne 2 ]  # number of arguments passed to script
then
	echo "****************************************************"
    echo "You must specify the path to the XML repository and the path to the directory where sitemaps will be placed"
    echo "Example of usage: ./sitemaps.sh /work/agris/XML_Output/2012/ /work/htdocs/sitemap/"
    echo "****************************************************"
else
    echo "Using \"$1\" repository. Sitemaps will be put into \"$2\" "
    exec java -classpath ".:../resources:../lib/*" -Xss64M -Xmx2048M org.fao.agris_sitemap.SitemapsGen $1 $2
fi

exit