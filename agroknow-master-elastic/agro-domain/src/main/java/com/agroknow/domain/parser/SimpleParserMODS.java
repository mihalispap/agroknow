package com.agroknow.domain.parser;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.parser.jflex.MODSParser;

import java.io.FileInputStream;
import java.io.IOException;

public class SimpleParserMODS extends AbstractParser {

    @Override
    public SimpleMetadata loadInternal(FileInputStream fileReader) throws IOException {
        MODSParser parser = new MODSParser(fileReader);
        // parse configuration file
        parser.yylex();
        return new SimpleMetadata(parser.getLocations(), parser.getIdentifiers());
    }
}