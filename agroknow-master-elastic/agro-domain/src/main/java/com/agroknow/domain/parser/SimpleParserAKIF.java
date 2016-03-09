package com.agroknow.domain.parser;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.akif.Akif;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.validation.Validator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SimpleParserAKIF extends AbstractParser {

    public SimpleParserAKIF(ObjectMapper mapper, Validator validator) {
        super(mapper, validator);
    }

    @Override
    protected SimpleMetadata loadInternal(FileInputStream fileReader) throws IOException {
        return getMapper().reader(Akif.class).readValue(fileReader);
    }

    @Override
    protected boolean updateFileUrlsStatus(JSONObject jsonObject, Map<String, Boolean> urlsStatusMap) {
        boolean fileChanged = false;
        JSONArray expressions = (JSONArray) jsonObject.get("expressions");
        for (Object expression : expressions) {
            JSONArray manifestations = (JSONArray) ((JSONObject) expression).get("manifestations");
            for (Object manifestation : manifestations) {
                JSONArray items0 = (JSONArray) ((JSONObject) manifestation).get("items");
                for (Object item : items0) {
                    JSONObject itemJson = (JSONObject) item;
                    String url = (String) itemJson.get("url");
                    Boolean broken = (Boolean) itemJson.get("broken");
                    if (urlsStatusMap.containsKey(url) && broken != urlsStatusMap.get(url)) {
                        itemJson.put("broken", urlsStatusMap.get(url));
                        fileChanged = true;
                    }
                }
            }
        }
        if (fileChanged) {
            jsonObject.put("lastUpdateDate", getDateFormatter().format(new Date(System.currentTimeMillis())));
        }
        return fileChanged;
    }
}
