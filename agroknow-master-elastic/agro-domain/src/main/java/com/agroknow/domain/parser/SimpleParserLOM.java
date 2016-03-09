package com.agroknow.domain.parser;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.parser.jflex.LOMParser;

import java.io.FileInputStream;
import java.io.IOException;

public class SimpleParserLOM extends AbstractParser {

    public SimpleMetadata loadInternal(FileInputStream fileReader) throws IOException  {
        LOMParser parser = new LOMParser(fileReader);
        parser.yylex();
        return new SimpleMetadata(parser.getLocations(), parser.getIdentifiers());
    }
}
