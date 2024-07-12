package com.mycompany.gametest;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;

public class Board extends JPanel {
    private int width;
    private int height;
    private Cell[][] backgroundCells;
    private Cell[][] foregroundCells;
    private Map<Integer, Board> portalLinks;  // Map to store portal links
    private GameMap gameMap;  // Reference to the GameMap instance
    

    public Board(int width, int height, GameMap gameMap) {
        this.width = width;
        this.height = height;
        this.backgroundCells = new Cell[width][height];
        this.foregroundCells = new Cell[width][height];
        this.portalLinks = new HashMap<>();
        this.gameMap = gameMap;  // Set the GameMap instance
        initializeEmptyBoard();
    }

    private void initializeEmptyBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                backgroundCells[x][y] = new Cell(CellState.EMPTY, 0, -1, -1); // Initialize with EMPTY textureId
                foregroundCells[x][y] = new Cell(CellState.EMPTY, 0, -1, -1); // Initialize with EMPTY textureId
            }
        }
    }

    public void setCellState(int x, int y, int type, int textureId, boolean isForeground, int entryPortalId, int exitPortalId) {
        if (isValidCell(x, y)) {
            Cell[][] targetCells = isForeground ? foregroundCells : backgroundCells;
            Cell cell;

            switch (type) {
                case 0:  // EMPTY
                    cell = new Cell(CellState.EMPTY, 0, -1, -1);
                    break;
                case 1:  // WALL
                    cell = new Cell(CellState.WALL, textureId, -1, -1);
                    break;
                case 2:  // PLAYER
                    cell = new Cell(CellState.PLAYER, textureId, -1, -1);
                    break;
                case 3:  // BOARDSWITCH (PORTAL)
                    cell = new Cell(CellState.BOARDSWITCH, textureId, entryPortalId, exitPortalId);  // Use entry and exit IDs
                    break;
                default:
                    return; // Do nothing if type is invalid
            }
            targetCells[x][y] = cell; // Set the cell correctly
            repaint();
        }
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Cell getCell(int x, int y, boolean isForeground) {
        if (isValidCell(x, y)) {
            return isForeground ? foregroundCells[x][y] : backgroundCells[x][y];
        }
        return null;
    }
    
    public String getName() {
        for (Map.Entry<String, Board> entry : gameMap.getBoards().entrySet()) {
            if (entry.getValue().equals(this)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public void setPortalLink(int portalLinkId, Board targetBoard) {
        portalLinks.put(portalLinkId, targetBoard);
    }

    public Board getPortalLink(int portalLinkId) {
        return portalLinks.get(portalLinkId);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    // Method to retrieve the GameMap instance associated with this board
    public GameMap getGameMap() {
        return gameMap;
    }
}