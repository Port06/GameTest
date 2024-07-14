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
        cardPanel.add(gamePanel, "Game");
        cardPanel.add(mapEditor.getEditorPanel(), "MapEditor");

        // Set the card panel as the content pane
        setContentPane(cardPanel);

        // Start the player animation scheduler
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::repaint, 0, 400, TimeUnit.MILLISECONDS);

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

    public void pauseGame() {
        isPaused = true;
        menu.showMenu("game");
        requestFocusInWindow();
        repaint();
    }

    public void resumeGame() {
        isPaused = false;
        menu.hideMenu();

        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            System.out.println("resumeGame: gamePanel focus=" + gamePanel.isFocusOwner());
        });

        repaint();
    }

    public void pauseMapEditor() {
        isPaused = true;
        menu.showMenu("editor");
        mapEditor.requestFocusInMapEditor(); // Request focus for map editor panel
        // Optionally disable any game controls or actions during map editor pause
        repaint();
    }

    public void resumeMapEditor() {
        isPaused = false;
        menu.hideMenu();

        SwingUtilities.invokeLater(() -> {
            mapEditor.getEditorPanel().requestFocusInWindow(); // Request focus for editorPanel
            System.out.println("resumeMapEditor: mapEditor panel focus=" + mapEditor.getEditorPanel().isFocusOwner());
        });

        repaint();
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
            long displayTime = Math.max(250, (long) (2500 - (queueSize * 300))); // Minimum display time is 0.75 seconds
            textBoxScheduler = Executors.newSingleThreadScheduledExecutor();
            textBoxScheduler.schedule(this::hideCurrentTextBox, displayTime, TimeUnit.MILLISECONDS);
        } else {
            currentTextBox = null;
        }
    }

    private void hideCurrentTextBox() {
        if (currentTextBox != null) {
            currentTextBox.hideText();
            repaint();

            // Show the next text box if available
            showNextTextBox();
        }
    }

     public void openMapEditor() {
        menu.hideMenu();
        mapEditor.loadBoard(map.getCurrentBoard().getName());
        cardLayout.show(cardPanel, "MapEditor");

        SwingUtilities.invokeLater(() -> {
            mapEditor.getEditorPanel().requestFocusInWindow(); // Request focus for editorPanel
            System.out.println("openMapEditor: mapEditor panel focus=" + mapEditor.getEditorPanel().isFocusOwner());
        });

        repaint();
    }


    public void openGameView() {
        menu.hideMenu();
        isPaused = false;
        map.setCurrentBoard(player.getCurrentBoard().getName());
        cardLayout.show(cardPanel, "Game");

        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow(); // Request focus for gamePanel
            System.out.println("openGameView: gamePanel focus=" + gamePanel.isFocusOwner());
        });

        repaint();
    }

    public GameMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }
    
    public boolean getPaused() {
        return isPaused;
    }
    
    public Menu getMenu() {
        return menu;
    }
    
    public JPanel getGamePanel() {
        return gamePanel;
    }
    
    public MapEditor getMapEditor() {
        return mapEditor;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}