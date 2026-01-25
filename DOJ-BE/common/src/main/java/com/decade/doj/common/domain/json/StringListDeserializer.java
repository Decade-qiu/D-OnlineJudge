package com.decade.doj.common.domain.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.START_ARRAY) {
            List<String> values = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                values.add(parser.getValueAsString());
            }
            return values;
        }

        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
            if (value == null || value.isBlank()) {
                return List.of();
            }
            return List.of(value);
        }

        return context.readValue(parser, context.getTypeFactory().constructCollectionType(List.class, String.class));
    }
}
