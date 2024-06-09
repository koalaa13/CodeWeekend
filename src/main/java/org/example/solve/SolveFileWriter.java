package org.example.solve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.example.Config;
import org.example.model.move.Move;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolveFileWriter {
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;
    private final String fileName;

    public SolveFileWriter(String fileName) {
        this.fileName = fileName;
        objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    public void writeToFile(List<Move> moves) throws IOException {
        Map<String, List<Move>> map = Map.of("moves", moves);
        objectWriter.writeValue(new File(Config.TEST_FOLDER + fileName), map);
    }
}
