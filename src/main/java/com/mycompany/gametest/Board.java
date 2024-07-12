package com.mycompany.gametest;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class Board extends JPanel {
    private int width;
    private int height;
    private Cell[][] cells;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        initializeEmptyBoard();
    }

    private void initializeEmptyBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(CellState.EMPTY, 0); // Initialize with EMPTY textureId
            }
        }
    }

    public void setCellState(int x, int y, int type, int textureId) {
        if (isValidCell(x, y)) {
            switch (type) {
                case 0:  // EMPTY
                    cells[x][y].setState(CellState.EMPTY);
                    cells[x][y].setTextureId(0);  // No texture for EMPTY state
                    break;
                case 1:  // WALL
                    cells[x][y].setState(CellState.WALL);
                    cells[x][y].setTextureId(textureId);
                    break;
                case 2:  // PLAYER
                    cells[x][y].setState(CellState.PLAYER);
                    cells[x][y].setTextureId(textureId);
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

    public Cell getCell(int x, int y) {
        if (isValidCell(x, y)) {
            return cells[x][y];
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