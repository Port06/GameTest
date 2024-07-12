package com.mycompany.gametest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Player {
    private int x;
    private int y;
    private Board currentBoard;
    private int currentTextureId;
    private int totalTextures;
    private ScheduledExecutorService animationScheduler;
    private GameMap map; // Add a reference to GameMap

    public Player(int startX, int startY, Board startBoard, int totalTextures, GameMap map) {
        this.x = startX;
        this.y = startY;
        this.currentBoard = startBoard;
        this.totalTextures = totalTextures;
        this.currentTextureId = 0; // Start with the first texture
        this.map = map; // Initialize the GameMap reference

        // Set the initial state in the foreground layer
        this.currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, true, -1);

        // Initialize the animation scheduler
        initAnimation();
    }

    private void initAnimation() {
        animationScheduler = Executors.newSingleThreadScheduledExecutor();
        animationScheduler.scheduleAtFixedRate(this::updateTexture, 0, 400, TimeUnit.MILLISECONDS);
    }

    private void updateTexture() {
        // Update the texture ID
        currentTextureId = (currentTextureId + 1) % totalTextures;

        // Update the board with the new texture ID in the foreground layer
        currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, true, -1);
    }

    public void move(int deltaX, int deltaY) {
        int newX = x + deltaX;
        int newY = y + deltaY;

        if (isInBounds(newX, newY)) {
            Cell targetCell = currentBoard.getCell(newX, newY, true);
            if (targetCell.getState() == CellState.EMPTY) {
                // Clear the current player position in the foreground layer
                currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, true, -1);
                // Clear the current player position in the background layer (if necessary)
                currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, false, -1);

                // Update player's new position in the foreground layer
                x = newX;
                y = newY;
                currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, true, -1);
            } else if (targetCell.getState() == CellState.BOARDSWITCH) {
                // Handle portal transition
                int portalLinkId = targetCell.getPortalLinkId();
                Board targetBoard = map.getBoardByPortalLinkId(portalLinkId);

                if (targetBoard != null) {
                    // Clear the current player position in the foreground layer
                    currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, true, -1);
                    // Clear the current player position in the background layer (if necessary)
                    currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, false, -1);

                    // Move to the new board
                    currentBoard = targetBoard;
                    x = 4;  // Example target position
                    y = 4;  // Example target position

                    // Set player's position in the new board's foreground layer
                    currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, true, -1);
                }
            }
        }
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < currentBoard.getWidth() && y >= 0 && y < currentBoard.getHeight();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCurrentTextureId() {
        return currentTextureId;
    }

    public void stopAnimation() {
        if (animationScheduler != null && !animationScheduler.isShutdown()) {
            animationScheduler.shutdown();
        }
    }
}