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

        System.out.println("Attempting to move player to: (" + newX + ", " + newY + ")");

        if (isInBounds(newX, newY)) {
            Cell targetCell = currentBoard.getCell(newX, newY, true);

            System.out.println("Target cell state: " + targetCell.getState());

            // Revisar si la casilla destino está vacía o es un portal
            if (targetCell.getState() == CellState.EMPTY || targetCell.getState() == CellState.BOARDSWITCH) {
                System.out.println("Target cell is empty or a portal");

                // Limpiar la posición actual del jugador en la capa de primer plano
                currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, true, -1);
                // Limpiar la posición actual del jugador en la capa de fondo (si es necesario)
                currentBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, false, -1);

                if (targetCell.getState() == CellState.BOARDSWITCH) {
                    System.out.println("Entering portal");

                    // Manejar la transición del portal
                    int portalLinkId = targetCell.getPortalLinkId();
                    System.out.println("Portal link ID: " + portalLinkId);

                    Board targetBoard = currentBoard.getPortalLink(portalLinkId);

                    if (targetBoard != null) {
                        System.out.println("Switching to new board: " + targetBoard.getName());

                        // Mover al nuevo tablero
                        x = 4; // Posición de destino de ejemplo
                        y = 4; // Posición de destino de ejemplo

                        // Limpiar la nueva posición del jugador en el tablero actual antes de cambiar
                        targetBoard.setCellState(x, y, CellState.EMPTY.getType(), 0, true, -1);

                        // Cambiar al nuevo tablero
                        currentBoard = targetBoard;
                        map.setCurrentBoard(targetBoard.getName()); // Suponiendo que Board tiene getName()
                    } else {
                        System.out.println("Target board is null");
                    }
                } else {
                    // Actualizar la nueva posición del jugador en la capa de primer plano
                    x = newX;
                    y = newY;
                }

                // Establecer la posición del jugador en la capa de primer plano
                currentBoard.setCellState(x, y, CellState.PLAYER.getType(), currentTextureId, true, -1);
            } else {
                System.out.println("Target cell is not empty or a portal");
            }
        } else {
            System.out.println("New position is out of bounds");
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