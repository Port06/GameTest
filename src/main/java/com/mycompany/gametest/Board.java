package com.mycompany.gametest;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;

public class Board extends JPanel {
    private int width;
    private int height;
    private Cell[][] backgroundLayer;
    private Cell[][] middleLayer;
    private Cell[][] foregroundLayer;
    private Cell[][] playerLayer;
    private Cell[][] topLayer;
    private Map<Integer, Board> portalLinks;  // Map to store portal links
    private GameMap gameMap;  // Reference to the GameMap instance
    

    public Board(int width, int height, GameMap gameMap) {
        this.width = width;
        this.height = height;
        this.backgroundLayer = new Cell[width][height];
        this.middleLayer = new Cell[width][height];
        this.foregroundLayer = new Cell[width][height];
        this.playerLayer = new Cell[width][height];
        this.topLayer = new Cell[width][height];
        this.portalLinks = new HashMap<>();
        this.gameMap = gameMap;  // Set the GameMap instance
        initializeEmptyBoard();
    }

    private void initializeEmptyBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                backgroundLayer[x][y] = new Cell(CellState.GRASS, 0, -1, -1); // Initialize with EMPTY textureId
                middleLayer[x][y] = new Cell(CellState.EMPTY, 0, -1, -1); // Initialize with EMPTY textureId
                foregroundLayer[x][y] = new Cell(CellState.EMPTY, 0, -1, -1); // Initialize with EMPTY textureId
                playerLayer[x][y] = new Cell(CellState.EMPTY, 0, -1, -1); // Initialize with EMPTY textureId
                topLayer[x][y] = new Cell(CellState.EMPTY, 0, -1, -1); // Initialize with EMPTY textureId
            }
        }
    }

    public void setCellState(int x, int y, int type, int textureId, int layer, int entryPortalId, int exitPortalId) {
        if (isValidCell(x, y)) {
            Cell[][] targetLayer;
            switch (layer) {
                case 0:
                    targetLayer = backgroundLayer;
                    break;
                case 1:
                    targetLayer = middleLayer;
                    break;
                case 2:
                    targetLayer = foregroundLayer;
                    break;
                case 3:
                    targetLayer = playerLayer;
                    break;
                case 4:
                    targetLayer = topLayer;
                    break;
                default:
                    System.out.println("Invalid layer: " + layer); // Debug statement
                    return; // Do nothing if layer is invalid
            }
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
                case 4: // WATER
                    cell = new Cell(CellState.WATER, textureId, -1, -1);
                    break;
                case 5:
                    cell =  new Cell(CellState.GRASS, textureId, -1, -1);
                    break;
                default:
                    System.out.println("Invalid type: " + type); // Debug statement
                    return; // Do nothing if type is invalid
            }
            targetLayer[x][y] = cell;
            repaint();
        } else {
            System.out.println("Invalid cell position: (" + x + ", " + y + ")"); // Debug statement
        }
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Cell getCell(int x, int y, int layer) {
        if (isValidCell(x, y)) {
            switch (layer) {
                case 0:
                    return backgroundLayer[x][y];
                case 1:
                    return middleLayer[x][y];
                case 2:
                    return foregroundLayer[x][y];
                case 3:
                    return playerLayer[x][y];
                case 4:
                    return topLayer[x][y];
                default:
                    return null;
            }
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
    
    public boolean containsPlayer(Player player) {
        // Assuming you have a way to determine the player's position on the board
        int playerX = player.getX();
        int playerY = player.getY();
        return isInBounds(playerX, playerY);
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
}