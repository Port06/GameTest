package com.mycompany.gametest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game extends JFrame {
    private GameMap map;
    private Player player;
    private double scale = 2.0;  // Scaling factor for the board
    private BufferedImage offscreen;

    public Game() {
        map = new GameMap();
        Board board1 = new Board(24, 24);
        Board board2 = new Board(10, 10);

        // Set up some WALL cells for testing
        for (int i = 0; i < 24; i++) {
            board1.setCellState(0, i, CellState.WALL.getType(), i % 12);
        }

        map.addBoard("board1", board1);
        map.addBoard("board2", board2);

        map.setCurrentBoard("board1");
        player = new Player(4, 4, map.getCurrentBoard(), 5);

        Assets.init();  // Initialize textures

        setTitle("Top-Down Game");
        setSize(800, 830);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        player.move(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        player.move(0, 1);
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        player.move(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        player.move(1, 0);
                        break;
                }
                repaint();
            }
        });

        // Start the player animation scheduler
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::repaint, 0, 400, TimeUnit.MILLISECONDS);

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        // Resize the offscreen image if necessary
        if (offscreen == null || offscreen.getWidth() != getWidth() || offscreen.getHeight() != getHeight()) {
            offscreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = offscreen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clear the offscreen image with a solid color (e.g., white)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        Board currentBoard = map.getCurrentBoard();
        int tileSize = (int) (Assets.getTileSize() * scale);

        // Calculate the starting x and y positions to center the board
        int boardWidth = currentBoard.getWidth() * tileSize;
        int boardHeight = currentBoard.getHeight() * tileSize;
        int startX = (getWidth() - boardWidth) / 2;
        int startY = (getHeight() - boardHeight) / 2;

        // Draw the board
        for (int x = 0; x < currentBoard.getWidth(); x++) {
            for (int y = 0; y < currentBoard.getHeight(); y++) {
                Cell cell = currentBoard.getCell(x, y);
                if (cell != null) {
                    BufferedImage texture = CellState.getTexture(cell);
                    g2d.drawImage(texture, startX + x * tileSize, startY + y * tileSize, tileSize, tileSize, null);
                }
            }
        }

        // Draw the player on top with the current texture ID
        BufferedImage playerTexture = Assets.getTexture(CellState.PLAYER, player.getCurrentTextureId());
        if (playerTexture != null) {
            System.out.println("Drawing player texture with ID: " + player.getCurrentTextureId());
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.drawImage(playerTexture, startX + player.getX() * tileSize, startY + player.getY() * tileSize, tileSize, tileSize, null);
        } else {
            System.out.println("Player texture is null for ID: " + player.getCurrentTextureId());
        }

        // Draw the offscreen image to the screen
        g.drawImage(offscreen, 0, 0, null);
        g2d.dispose();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}