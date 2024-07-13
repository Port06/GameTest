package com.mycompany.gametest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.Queue;

public class Game extends JFrame {
    private GameMap map;
    private Player player;
    private double scale = 1.5;  // Scaling factor for the board
    private BufferedImage offscreen;
    private boolean isPaused = false;
    private JButton resumeButton;
    private JPanel gamePanel;
    private JPanel menuPanel;
    
    public Queue<TextBox> textBoxQueue = new LinkedList<>(); // Use a queue for TextBoxes
    private TextBox currentTextBox; // Track the currently displayed TextBox


    public Game() {
        map = new GameMap();
        Board board1 = new Board(32, 32, map);
        Board board2 = new Board(12, 12, map);

        // Set up some WALL cells for testing
        for (int i = 0; i < 32; i++) {
            board1.setCellState(0, i, CellState.WALL.getType(), i % 12, 4,-1, -1);
            
            if (i % 2 == 0) {
                board1.setCellState(8, i, CellState.WALL.getType(), 14, 4, -1, -1);
            }
        }
        
        board1.setCellState(15, 15, CellState.WATER.getType(), 0, 1, -1, -1);
        board1.setCellState(15, 16, CellState.WATER.getType(), 0, 1, -1, -1);
        board1.setCellState(15, 17, CellState.WATER.getType(), 0, 1, -1, -1);
        board1.setCellState(16, 15, CellState.WATER.getType(), 1, 1, -1, -1);
        board1.setCellState(16, 16, CellState.WATER.getType(), 1, 1, -1, -1);
        board1.setCellState(16, 17, CellState.WATER.getType(), 1, 1, -1, -1);
        board1.setCellState(17, 15, CellState.WATER.getType(), 2, 1, -1, -1);
        board1.setCellState(17, 16, CellState.WATER.getType(), 2, 1, -1, -1);
        board1.setCellState(17, 17, CellState.WATER.getType(), 2, 1, -1, -1);
        
        // Configurar portales y sus enlaces
        board1.setCellState(5, 5, CellState.BOARDSWITCH.getType(), 0, 3, 0, 1); // Añadir portal en board1 con link ID 0
        board2.setCellState(5, 5, CellState.BOARDSWITCH.getType(), 0, 3, 1, 0); // Añadir portal en board2 con link ID 1

        map.addBoard("MainBoard", board1);
        map.addBoard("SubBoard", board2);

        // Asegúrate de vincular correctamente los portales
        board1.setPortalLink(0, board2);
        board2.setPortalLink(1, board1);

        map.setCurrentBoard("MainBoard");
        player = new Player(4, 4, board1, 5, map, this);

        Assets.init();  // Initialize textures

        setTitle("Top-Down Game");
        setSize(800, 830);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the game panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isPaused) {
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
                        case KeyEvent.VK_ESCAPE:
                            pauseGame();
                            break;
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resumeGame();
                }
            }
        });
        
        // Create the menu panel
        menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMenu(g);
            }
        };
        menuPanel.setOpaque(true);
        menuPanel.setBackground(new Color(128, 128, 128, 192)); // Grayish with some transparency
        menuPanel.setBounds(0, getHeight() / 3, getWidth(), getHeight() / 3); // Create a stripe in the middle of the window
        menuPanel.setLayout(null); // Use absolute positioning for the button

        // Create the resume button and center it within the menu panel
        resumeButton = new JButton("Resume");
        resumeButton.setBounds((menuPanel.getWidth() - 100) / 2, (menuPanel.getHeight() - 30) / 2, 100, 30); // Center the button
        resumeButton.addActionListener(e -> resumeGame());
        menuPanel.add(resumeButton);

        // Initially show the game panel
        setContentPane(gamePanel);

        // Add the menu panel to the layered pane
        getLayeredPane().add(menuPanel, JLayeredPane.PALETTE_LAYER);
        menuPanel.setVisible(false); // Initially hidden

        // Start the player animation scheduler
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::repaint, 0, 400, TimeUnit.MILLISECONDS);

        setVisible(true);
    }
    
    public void addTextBox(String message, int x, int y, int width, int height) {
        TextBox textBox = new TextBox(x, y, width, height); // Create TextBox with specified parameters
        textBox.setText(message);
        textBoxQueue.add(textBox); // Add to the queue

        // If there's no current TextBox, display this one immediately
        if (currentTextBox == null) {
            displayNextTextBox();
        }
    }
    
     private void displayNextTextBox() {
        if (!textBoxQueue.isEmpty()) {
            currentTextBox = textBoxQueue.poll();
            currentTextBox.show();

            // Determine the duration based on the queue size
            int queueSize = textBoxQueue.size();
            long displayDuration = Math.max(3000 - (queueSize * 2250), 500); // Base time of 3000ms, decrease by 500ms per item, min 1000ms

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                currentTextBox.hide();
                currentTextBox = null; // Clear current TextBox
                displayNextTextBox(); // Display the next TextBox
            }, displayDuration, TimeUnit.MILLISECONDS); // Adjusted duration
        }
    }
     
    private void pauseGame() {
        isPaused = true;
        menuPanel.setVisible(true); // Show the menu panel
        repaint();
    }

    private void resumeGame() {
        isPaused = false;
        menuPanel.setVisible(false); // Hide the menu panel
        repaint();
    }

    private void drawGame(Graphics g) {
        if (offscreen == null || offscreen.getWidth() != getWidth() || offscreen.getHeight() != getHeight()) {
            offscreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = offscreen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        Board currentBoard = map.getCurrentBoard();
        int tileSize = (int) (Assets.getTileSize() * scale);

        int boardWidth = currentBoard.getWidth() * tileSize;
        int boardHeight = currentBoard.getHeight() * tileSize;
        int startX = (getWidth() - boardWidth) / 2;
        int offsetY = -20;
        int startY = (getHeight() - boardHeight) / 2 + offsetY;

        // Draw each layer in order with debug statements
        for (int layer = 0; layer < 5; layer++) {
            drawLayer(g2d, currentBoard, startX, startY, tileSize, layer);
        }

        // Draw the player on top with the current texture ID
        BufferedImage playerTexture = Assets.getTexture(CellState.PLAYER, player.getCurrentTextureId());
        if (playerTexture != null) {
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.drawImage(playerTexture, startX + player.getX() * tileSize, startY + player.getY() * tileSize, tileSize, tileSize, null);
        }

        if (currentTextBox != null) {
            currentTextBox.draw(g2d);
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
                    } else {
                        // Nothing
                    }
                } else {
                    // Nothing
                }
            }
        }
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw semi-transparent background
        g2d.setColor(new Color(128, 128, 128, 192)); // Grayish with some transparency
        g2d.fillRect(0, getHeight() / 3, getWidth(), getHeight() / 3); // 1/3 of the height centered vertically
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}