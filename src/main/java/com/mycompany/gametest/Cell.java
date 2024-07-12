package com.mycompany.gametest;

import java.awt.image.BufferedImage;

public class Cell {
    private CellState state;
    private int textureId; // Texture ID for WALL cells

    public Cell(CellState state, int textureId) {
        this.state = state;
        this.textureId = textureId;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
}