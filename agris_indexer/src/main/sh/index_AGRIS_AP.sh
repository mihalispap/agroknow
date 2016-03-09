#!/bin/sh
#
# ###################### DEPRECATED ##########################
#
# It does not check the DTD
# Script for execute indexing application 
# The application can index the entire repository, or a specific year, or a specific center.
# In case it indexes a specific year, big centers are excluded (see value of exclude_string in the Default.properties file). They have to be indexed by specific center.
#


cd bin 

if [ $# -ne 1 ]  # number of arguments passed to script
then
    echo "Use default source directory"
    exec java -classpath ".:../resources:../lib/*" -Xss64M -Xmx2048M org.fao.agris_indexer.IndexXML
else
    echo "Use \"$1\" XML source directory"
    exec java -classpath ".:../resources:../lib/*" -Xss64M -Xmx2048M org.fao.agris_indexer.IndexXML $1
fi

exit