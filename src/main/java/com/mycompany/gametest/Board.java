package com.mycompany.gametest;

import com.mycompany.gametest.CellState;
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
                backgroundCells[x][y] = new Cell(CellState.EMPTY, 0); // Initialize with EMPTY textureId
                foregroundCells[x][y] = new Cell(CellState.EMPTY, 0); // Initialize with EMPTY textureId
            }
        }
    }

    public void setCellState(int x, int y, int type, int textureId, boolean isForeground, int portalLinkId) {
        if (isValidCell(x, y)) {
            Cell[][] targetCells = isForeground ? foregroundCells : backgroundCells;
            Cell cell = targetCells[x][y];
            switch (type) {
                case 0:  // EMPTY
                    cell.setState(CellState.EMPTY);
                    cell.setTextureId(0);
                    cell.setPortalLinkId(-1);  // No portal for EMPTY state
                    break;
                case 1:  // WALL
                    cell.setState(CellState.WALL);
                    cell.setTextureId(textureId);
                    cell.setPortalLinkId(-1);  // No portal for WALL state
                    break;
                case 2:  // PLAYER
                    cell.setState(CellState.PLAYER);
                    cell.setTextureId(textureId);
                    cell.setPortalLinkId(-1);  // No portal for PLAYER state
                    break;
                case 3:  // BOARDSWITCH (PORTAL)
                    cell.setState(CellState.BOARDSWITCH);
                    cell.setTextureId(textureId);
                    cell.setPortalLinkId(portalLinkId);  // Set the portal link ID
                    break;
                default:
                    break;
            }
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