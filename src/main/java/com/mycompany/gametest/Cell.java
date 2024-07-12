package com.mycompany.gametest;

public class Cell {
    private CellState state;
    private int textureId;
    private int portalLinkId;  // Identificador de portal para vincular los portales

    public Cell(CellState state, int textureId) {
        this.state = state;
        this.textureId = textureId;
        this.portalLinkId = -1;  // Valor predeterminado para células que no son portales
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

    public int getPortalLinkId() {
        return portalLinkId;
    }

    public void setPortalLinkId(int portalLinkId) {
        this.portalLinkId = portalLinkId;
    }
}