package org.example.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Config;

import java.io.File;
import java.io.IOException;

public abstract class Parser<T> {
    protected final ObjectMapper objectMapper;

    protected Parser() {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected JsonNode parseJson(File file) throws IOException {
        return objectMapper.readTree(file);
    }

    public T parse(String filename) throws IOException {
        return parse(new File(Config.TEST_FOLDER + filename));
    }

    public abstract T parse(File file) throws IOException;
}
