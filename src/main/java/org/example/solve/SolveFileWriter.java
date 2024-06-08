package org.example.solve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.example.core.Util;
import org.example.model.move.Move;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolveFileWriter {
    private final ObjectMapper objectMapper;

    public SolveFileWriter() {
        objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public void writeToFile(List<Move> moves, String fileName) throws IOException {
        Map<String, List<Move>> map = Map.of("moves", moves);
        objectMapper.writeValue(new File(Util.TEST_FOLDER + fileName), map);
    }
}
