package org.example.model.move;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class TravelMove extends Move {
    private int targetX;
    private int targetY;

    @Override
    public String getType() {
        return "move";
    }
}
