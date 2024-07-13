package com.mycompany.gametest;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameRenderer {
    private GameMap map;
    private Player player;
    private double scale = 1.5;

    public GameRenderer(GameMap map, Player player) {
        this.map = map;
        this.player = player;
    }

    public void drawGame(Graphics g, BufferedImage offscreen, int width, int height) {
        if (offscreen == null || offscreen.getWidth() != width || offscreen.getHeight() != height) {
            offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = offscreen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        Board currentBoard = map.getCurrentBoard();
        int tileSize = (int) (Assets.getTileSize() * scale);

        int boardWidth = currentBoard.getWidth() * tileSize;
        int boardHeight = currentBoard.getHeight() * tileSize;
        int startX = (width - boardWidth) / 2;
        int offsetY = -10;
        int startY = (height - boardHeight) / 2 + offsetY;

        // Draw each layer in order
        for (int layer = 0; layer < 5; layer++) {
            drawLayer(g2d, currentBoard, startX, startY, tileSize, layer);
        }

        // Draw the player on top with the current texture ID
        BufferedImage playerTexture = Assets.getTexture(CellState.PLAYER, player.getCurrentTextureId());
        if (playerTexture != null) {
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.drawImage(playerTexture, startX + player.getX() * tileSize, startY + player.getY() * tileSize, tileSize, tileSize, null);
        }

        g.drawImage(offscreen, 0, 0, null);
        g2d.dispose();
    }

    public void drawBoard(Graphics g, BufferedImage offscreen, int width, int height) {
        if (offscreen == null || offscreen.getWidth() != width || offscreen.getHeight() != height) {
            offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = offscreen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        Board currentBoard = map.getCurrentBoard();
        int tileSize = (int) (Assets.getTileSize() * scale);

        int boardWidth = currentBoard.getWidth() * tileSize;
        int boardHeight = currentBoard.getHeight() * tileSize;
        int startX = (width - boardWidth) / 2;
        int offsetY = -10;
        int startY = (height - boardHeight) / 2 + offsetY;

        // Draw each layer in order
        for (int layer = 0; layer < 5; layer++) {
            drawLayer(g2d, currentBoard, startX, startY, tileSize, layer);
        }

        g.drawImage(offscreen, 0, 0, null);
        g2d.dispose();
    }

    private void drawLayer(Graphics2D g2d, Board board, int startX, int startY, int tileSize, int layer) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Cell cell = board.getCell(x, y, layer);
                if (cell != null) {
                    BufferedImage texture = CellState.getTexture(cell);
                    if (texture != null) {
                        g2d.drawImage(texture, startX + x * tileSize, startY + y * tileSize, tileSize, tileSize, null);
                    }
                }
            }
        }
    }
}