package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class Field {
    private List<Monster> monsters;
    private int width;
    private int height;

}
