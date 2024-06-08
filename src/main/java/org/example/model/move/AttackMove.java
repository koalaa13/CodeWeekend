package org.example.model.move;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttackMove extends Move {
    private int targetId;

    @Override
    public String getType() {
        return "attack";
    }
}
