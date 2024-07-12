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

    public Player(int startX, int startY, Board startBoard, int totalTextures) {
        this.x = startX;
        this.y = startY;
        this.currentBoard = startBoard;
        this.totalTextures = totalTextures;
        this.currentTextureId = 0; // Start with the first texture

        // Set the initial state
        this.currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId);

        // Initialize the animation scheduler
        initAnimation();
    }

    private void initAnimation() {
        animationScheduler = Executors.newSingleThreadScheduledExecutor();
        animationScheduler.scheduleAtFixedRate(this::updateTexture, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void updateTexture() {
        // Update the texture ID
        currentTextureId = (currentTextureId + 1) % totalTextures;

        // Update the board with the new texture ID
        currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId);
    }

    public void move(int deltaX, int deltaY) {
        int newX = x + deltaX;
        int newY = y + deltaY;

        if (isInBounds(newX, newY) && currentBoard.getCell(newX, newY).getState() == CellState.EMPTY) {
            currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0);
            x = newX;
            y = newY;
            currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId);
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