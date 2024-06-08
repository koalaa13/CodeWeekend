package org.example.model.move;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AttackMove extends Move {
    private int targetId;

    @Override
    public String getType() {
        return "attack";
    }
}
