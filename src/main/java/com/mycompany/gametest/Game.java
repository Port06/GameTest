package com.mycompany.gametest;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Game extends JFrame {
    private GameMap map;
    private Player player;
    private boolean isPaused = false;
    private Menu menu;
    private GameRenderer renderer;
    private BufferedImage offscreen;
    private JPanel gamePanel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private MapEditor mapEditor;

    private Queue<TextBox> textBoxQueue;
    private TextBox currentTextBox;
    private ScheduledExecutorService textBoxScheduler;
    private ScheduledExecutorService gameUpdateScheduler;

    public Game() {
        map = new GameMap();
        Board board1 = new Board(32, 32, map);
        Board board2 = new Board(12, 12, map);

        // Set up some WALL cells for testing
        for (int i = 0; i < 32; i++) {
            board1.setCellState(0, i, CellState.WALL.getType(), i % 12, 3, -1, -1);
            if (i % 2 == 0) {
                board1.setCellState(8, i, CellState.WALL.getType(), 14, 3, -1, -1);
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

        // Configure portals and their links
        board1.setCellState(5, 5, CellState.BOARDSWITCH.getType(), 0, 3, 0, 1); // Add portal in board1 with link ID 0
        board2.setCellState(5, 5, CellState.BOARDSWITCH.getType(), 0, 3, 1, 0); // Add portal in board2 with link ID 1

        map.addBoard("MainBoard", board1);
        map.addBoard("SubBoard", board2);

        // Ensure portals are linked correctly
        board1.setPortalLink(0, board2);
        board2.setPortalLink(1, board1);

        map.setCurrentBoard("MainBoard");
        player = new Player(4, 4, board1, 5, map, this);

        Assets.init();  // Initialize textures

        setTitle("Top-Down Game");
        setSize(800, 830);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the menu and renderer
        menu = new Menu(this);
        renderer = new GameRenderer(map, player);

        // Initialize the textBoxQueue
        textBoxQueue = new LinkedList<>();

        // Initialize the card layout and panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Set up the game panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderer.drawGame(g, offscreen, getWidth(), getHeight());

                // Draw the current TextBox if it is visible
                if (currentTextBox != null && currentTextBox.isVisible()) {
                    currentTextBox.drawTextBox(g);
                }
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
                }
            }
        });

        // Create the MapEditor instance
        mapEditor = new MapEditor(this);

        // Add the game panel and map editor to the card panel
        cardPanel.add(gamePanel, "game");  // Ensure this matches the name used in openGameView
        cardPanel.add(mapEditor.getEditorPanel(), "editor");  // Ensure this matches the name used in openMapEditor

        // Set the card panel as the content pane
        setContentPane(cardPanel);

        // Initialize the player animation scheduler
        startGameLoop();

        // Add focus listeners for debugging
        gamePanel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("GamePanel gained focus");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("GamePanel lost focus");
            }
        });

        mapEditor.getEditorPanel().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("MapEditor panel gained focus");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("MapEditor panel lost focus");
            }
        });

        menu.getMenuPanel().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("MenuPanel gained focus");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("MenuPanel lost focus");
            }
        });

        setVisible(true);
    }

    private void startGameLoop() {
        gameUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
        gameUpdateScheduler.scheduleAtFixedRate(this::repaint, 0, 400, TimeUnit.MILLISECONDS);
    }

    private void stopGameLoop() {
        if (gameUpdateScheduler != null && !gameUpdateScheduler.isShutdown()) {
            gameUpdateScheduler.shutdownNow();
        }
    }

    public void pauseGame() {
        isPaused = true;
        stopGameLoop();
        menu.showMenu("game");
        repaint();
    }

    public void resumeGame() {
        isPaused = false;
        startGameLoop();
        menu.hideMenu();
    }
    
    public void openGameView() {
        cardLayout.show(cardPanel, "game");
        gamePanel.requestFocusInWindow();
        startGameLoop();
        repaint();
    }

    public void pauseMapEditor() {
        isPaused = true;
        stopGameLoop();
        menu.showMenu("editor");
        mapEditor.requestFocusInMapEditor(); // Request focus for map editor panel
        // Optionally disable any game controls or actions during map editor pause
        repaint();
    }
    
    public void openMapEditor() {
        cardLayout.show(cardPanel, "editor");
        mapEditor.requestFocusInMapEditor();
        startGameLoop();
        repaint();
    }

    public void resumeMapEditor() {
        isPaused = false;
        startGameLoop();
        menu.hideMenu();
    }

    public void addTextBox(String text, int x, int y, int width, int height) {
        TextBox newTextBox = new TextBox(x, y, width, height);
        newTextBox.showText(text);
        textBoxQueue.add(newTextBox);
        if (currentTextBox == null) {
            showNextTextBox();
        }
    }

    private void showNextTextBox() {
        if (!textBoxQueue.isEmpty()) {
            currentTextBox = textBoxQueue.poll();
            currentTextBox.showText(currentTextBox.text);
            repaint();

            // Calculate display time based on queue size
            int queueSize = textBoxQueue.size();
            long displayTime = Math.max(250, (long) (2500 - (queueSize * 300))); // Minimum display time is 250ms

            textBoxScheduler = Executors.newSingleThreadScheduledExecutor();
            textBoxScheduler.schedule(this::showNextTextBox, displayTime, TimeUnit.MILLISECONDS);
        } else {
            currentTextBox = null;
            if (textBoxScheduler != null && !textBoxScheduler.isShutdown()) {
                textBoxScheduler.shutdownNow();
            }
        }
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }
    
    public GameMap getMap() {
        return map;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Menu getMenu() {
        return menu;
    }
    
    public boolean getPaused() {
        return isPaused;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public MapEditor getMapEditor() {
        return mapEditor;
    }
}