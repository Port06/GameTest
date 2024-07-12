package com.mycompany.gametest;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class Board extends JPanel {
    private int width;
    private int height;
    private Cell[][] backgroundCells;
    private Cell[][] foregroundCells;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.backgroundCells = new Cell[width][height];
        this.foregroundCells = new Cell[width][height];
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

    public void setCellState(int x, int y, int type, int textureId, boolean isForeground) {
        if (isValidCell(x, y)) {
            Cell[][] targetCells = isForeground ? foregroundCells : backgroundCells;
            switch (type) {
                case 0:  // EMPTY
                    targetCells[x][y].setState(CellState.EMPTY);
                    targetCells[x][y].setTextureId(0);  // No texture for EMPTY state
                    break;
                case 1:  // WALL
                    targetCells[x][y].setState(CellState.WALL);
                    targetCells[x][y].setTextureId(textureId);
                    break;
                case 2:  // PLAYER
                    targetCells[x][y].setState(CellState.PLAYER);
                    targetCells[x][y].setTextureId(textureId);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}