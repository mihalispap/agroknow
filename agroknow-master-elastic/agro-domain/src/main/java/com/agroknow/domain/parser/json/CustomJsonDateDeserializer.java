package com.agroknow.domain.parser.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class CustomJsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        String date = parser.getText();
        try {
            return DateUtils.parseDate(date, new String[]{"yyyy-MM-dd", "yyyy-MM", "yyyy", "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"});
        } catch (ParseException e) {
            throw new JsonParseException("Error parsing date " + date, parser.getCurrentLocation(), e);
        }
    }
}
