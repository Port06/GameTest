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
    private Game game; // Add a reference to the Game
    private long lastMoveTime; // Store the last move time
    private static final long MOVE_COOLDOWN = 500; // Cooldown period in milliseconds
    private static final long WATER_MOVE_COOLDOWN = 1250; // Cooldown period when moving into water

    public Player(int startX, int startY, Board startBoard, int totalTextures, GameMap map,  Game game) {
        this.x = startX;
        this.y = startY;
        this.currentBoard = startBoard;
        this.totalTextures = totalTextures;
        this.currentTextureId = 0; // Start with the first texture
        this.map = map; // Initialize the GameMap reference
        this.game = game; // Initialize the Game reference
        this.lastMoveTime = 0; // Initialize the last move time

        // Set the initial state in the foreground layer
        this.currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, 3, -1, -1);

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
        currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, 3, -1, -1);
    }

    public void move(int deltaX, int deltaY) {
        long currentTime = System.currentTimeMillis();
        long cooldownPeriod = MOVE_COOLDOWN;
        
        if (currentTime - lastMoveTime < MOVE_COOLDOWN) {
            return; // Do not allow movement if the cooldown period has not passed
        }

        int newX = x + deltaX;
        int newY = y + deltaY;

        if (isInBounds(newX, newY)) {
            Cell targetCell = currentBoard.getCell(newX, newY, 3);
            Cell targetCellLayer1 = currentBoard.getCell(newX, newY, 1);
            
            // Determine the cooldown period based on the target cell state (for water slowdown)
            if (targetCellLayer1.getState() == CellState.WATER) {
                cooldownPeriod = WATER_MOVE_COOLDOWN;
            }

            if (currentTime - lastMoveTime < cooldownPeriod) {
                return; // Do not allow movement if the cooldown period has not passed
            }

            // Check if the target cell is empty or a portal
            if (targetCell.getState() == CellState.EMPTY || targetCell.getState() == CellState.BOARDSWITCH) {
                // Clear the current position of the player in the foreground layer
                currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, 3, -1, -1);

                if (targetCell.isPortal()) {
                    // Handle portal transition
                    int exitId = targetCell.getExitPortalId();
                    Board targetBoard = currentBoard.getPortalLink(exitId);

                    if (targetBoard != null) {
                        System.out.println("Switching to new board: " + targetBoard.getName());

                        // Find the portal position in the new board
                        int targetX = -1;
                        int targetY = -1;
                        boolean found = false;

                        for (int x = 0; x < targetBoard.getWidth(); x++) {
                            for (int y = 0; y < targetBoard.getHeight(); y++) {
                                Cell cell = targetBoard.getCell(x, y, 3);
                                if (cell != null && cell.getEntryPortalId() == exitId) {
                                    targetX = x;
                                    targetY = y;
                                    found = true;
                                    break; // Exit inner loop
                                }
                            }
                            if (found) break; // Exit outer loop if found
                        }

                        if (found) {
                            // Move to the new board
                            x = targetX; // Position of the portal
                            y = targetY + 1; // Position just below the portal

                            // Clear the new position of the player in the current board before changing
                            targetBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, 3, -1, -1);

                            // Change to the new board
                            currentBoard = targetBoard;
                            map.setCurrentBoard(targetBoard.getName());
                            
                            game.addTextBox("Has cambiado al tablero: " + targetBoard.getName(), 250, 50, 300, 20);
                            
                        } else {
                            System.out.println("Entry portal not found in target board");
                        }
                    } else {
                        System.out.println("Target board is null");
                    }
                } else {
                    // Update the new position of the player in the foreground layer
                    x = newX;
                    y = newY;
                }

                // Set the player's position in the foreground layer
                currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, 3, -1, -1);

                // Update the last move time
                lastMoveTime = System.currentTimeMillis();
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
    
    public Board getCurrentBoard() {
        return currentBoard;
    }

    public void stopAnimation() {
        if (animationScheduler != null && !animationScheduler.isShutdown()) {
            animationScheduler.shutdown();
        }
    }
}