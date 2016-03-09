package com.agroknow.domain.parser;

import com.agroknow.domain.SimpleMetadata;

import java.io.IOException;
import java.util.Map;

/**
 * @author aggelos
 */
public interface Parser {

    SimpleMetadata load(String filePath) throws ParserException;
    
    void updateFileUrls(String filePath, String identifier, Map<String, Boolean> urlsStatusMap) throws ParserException, IOException;
}
