package org.example.model.move;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class TravelMove extends Move {
    private long targetX;
    private long targetY;

    @Override
    public String getType() {
        return "move";
    }
}
