package com.mycompany.gametest;

import java.awt.image.BufferedImage;

public enum CellState {
    EMPTY(0, 0),
    WALL(1, 0),
    PLAYER(2, 0),
    BOARDSWITCH(3, 0),
    WATER(4, 0),
    GRASS(5, 0);

    private int type;
    private int texture;

    CellState(int type, int texture) {
        this.type = type;
        this.texture = texture;
    }

    public int getType() {
        return type;
    }

    public int getTexture() {
        return texture;
    }

    public static BufferedImage getTexture(Cell cell) {
        CellState state = cell.getState();
        int textureId = cell.getTextureId();
        BufferedImage texture = Assets.getTexture(state, textureId);
        if (texture == null) {
            System.out.println("Texture not found for state: " + state + " with textureId: " + textureId); // Debug statement
        }
        return texture;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static CellState getByTypeAndTexture(int type, int texture) {
        for (CellState state : values()) {
            if (state.getType() == type && state.getTexture() == texture) {
                return state;
            }
        }
        return EMPTY;  // Default to EMPTY if not found
    }
}