package com.mycompany.gametest;

// Class that defines what is a cell with it's related features
// like the textures and id's
public class Cell {
    private CellState state;
    private int textureId;
    private int entryPortalId;  // Entry portal ID
    private int exitPortalId;   // Exit portal ID

    public Cell(CellState state, int textureId, int exitPortalId, int entryPortalId) {
        this.state = state;
        this.textureId = textureId;
        this.entryPortalId = entryPortalId; // Set entry portal ID
        this.exitPortalId = exitPortalId;   // Set exit portal ID
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

    public int getEntryPortalId() {
        return entryPortalId;
    }

    public void setEntryPortalId(int entryPortalId) {
        this.entryPortalId = entryPortalId;
    }
    
    public int getExitPortalId() {
        return exitPortalId;
    }

    public void setExitPortalId(int exitPortalId) {
        this.exitPortalId = exitPortalId;
    }
    
    public boolean isPortal() {
        return state == CellState.BOARDSWITCH;
    }
}