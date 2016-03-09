package com.agroknow.domain.parser.factory;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.parser.AbstractParser;
import com.agroknow.domain.parser.Parser;
import com.agroknow.domain.parser.ParserException;
import com.agroknow.domain.parser.SimpleParserAGRIF;
import com.agroknow.domain.parser.SimpleParserAKIF;
import com.agroknow.domain.parser.SimpleParserDC;
import com.agroknow.domain.parser.SimpleParserLOM;
import com.agroknow.domain.parser.SimpleParserMODS;
import com.agroknow.domain.parser.SimpleParserNSDL;
import com.agroknow.domain.parser.json.CustomObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class SimpleMetadataParserFactory {
    public static final String MODS = "mods";
    public static final String AKIF = "akif";
    public static final String LOM = "lom";
    public static final String NSDL = "nsdl";
    public static final String DC = "dc";
    public static final String AGRIF = "agrif";
    private static final Map<String, AbstractParser> PARSERS_MAP = new HashMap<String, AbstractParser>();
    private static final Validator VALIDATOR;
    
    static {
        // we usually use the Factory when out of a DI environment (like Spring)
        // so we must instansiate the dependencies and pass them through by
        // hand. Currently, in Parser(s), we only depend on ObjectMapper.
        CustomObjectMapper mapper = new CustomObjectMapper();
        mapper.init();
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        PARSERS_MAP.put(AGRIF, new SimpleParserAGRIF(mapper, VALIDATOR));
        PARSERS_MAP.put(MODS, new SimpleParserMODS());
        PARSERS_MAP.put(LOM, new SimpleParserLOM());
        PARSERS_MAP.put(NSDL, new SimpleParserNSDL());
        PARSERS_MAP.put(DC, new SimpleParserDC());
        PARSERS_MAP.put(AKIF, new SimpleParserAKIF(mapper, VALIDATOR));
    }

    private SimpleMetadataParserFactory(){
    }
    
    public static Parser getParser(String metadataType) {
        return PARSERS_MAP.get(metadataType);
    }

    public static SimpleMetadata load(String metadataType, String filePath) throws ParserException {
        if (!PARSERS_MAP.containsKey(metadataType)) {
            throw new ParserException("Unsupported Metadata Format : \"" + metadataType + "\" !");
        }
        return PARSERS_MAP.get(metadataType).load(filePath);
    }
    
    public static void updateFileUrls(String metadataType, String filePath, String identifier, Map<String, Boolean> urlsStatusMap) throws ParserException, IOException {
        if (!PARSERS_MAP.containsKey(metadataType)) {
            throw new ParserException("Unsupported Metadata Format : \"" + metadataType + "\" !");
        }
        PARSERS_MAP.get(metadataType).updateFileUrls(filePath, identifier, urlsStatusMap);
    }

    public static boolean isValidMetadataType(String metadataType) {
        return PARSERS_MAP.containsKey(metadataType);
    }
}
