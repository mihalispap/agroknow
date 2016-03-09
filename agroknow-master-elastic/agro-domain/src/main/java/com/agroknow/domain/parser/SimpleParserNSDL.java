package com.agroknow.domain.parser;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.parser.jflex.NSDLParser;

import java.io.FileInputStream;
import java.io.IOException;

public class SimpleParserNSDL extends AbstractParser {

    @Override
    public SimpleMetadata loadInternal(FileInputStream fileReader) throws IOException {
        NSDLParser parser = new NSDLParser(fileReader);
        // parse configuration file
        parser.yylex();
        return new SimpleMetadata(parser.getLocations(), parser.getIdentifiers());
    }
}