package org.example.model.move;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TravelMove extends Move {
    private int targetX;
    private int targetY;

    @Override
    public String getType() {
        return "move";
    }
}
