package com.agroknow.domain.parser;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.parser.jflex.DCParser;

import java.io.FileInputStream;
import java.io.IOException;

public class SimpleParserDC extends AbstractParser {

    public SimpleMetadata loadInternal(FileInputStream fileReader) throws IOException {
        DCParser parser = new DCParser(fileReader);
        // parse configuration file
        parser.yylex();
        return new SimpleMetadata(parser.getLocations(), parser.getIdentifiers());
    }
}
