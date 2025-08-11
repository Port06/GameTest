package com.mycompany.gametest;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Class that has the main logic of the game such as creating a initial board, player movement
// and much more. (Will be improved on in the future).
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
    private TextBox textbox;
    private ScheduledExecutorService gameUpdateScheduler;
    
    //Player initializacion variable
    private int startX = 4;
    private int startY = 4;
    private int numberOfLayers = 5;

    public Game() {
        
        Assets.init(); // Initialize textures for the game
        
        
        // Initializacion of classes
        
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
        TextBox textBox = new TextBox(250, 50, 300, 20, this);  // 'this' being your Game instance
        this.player = new Player(startX, startY, board1, numberOfLayers, map, this, textBox);
        
        /*
        -------------------------------------------------------------------------------------------------
        */

        // Window customization
        setTitle("Top-Down Game");
        setSize(800, 830);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the menu and renderer
        menu = new Menu(this);
        renderer = new GameRenderer(map, player);

        // Initialize the card layout and panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Set up the game panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderer.drawGame(g, offscreen, getWidth(), getHeight());
                textBox.drawActiveTextBox(g);
            }
        };
        gamePanel.setFocusable(true);
        setupKeyBindingsWithInputMap(gamePanel);

        // Create the MapEditor instance
        mapEditor = new MapEditor(this);
        
        gamePanel.setFocusable(true);
        mapEditor.getEditorPanel().setFocusable(true);

        // Add the game panel and map editor to the card panel
        cardPanel.add(gamePanel, "game");  // Ensure this matches the name used in openGameView
        cardPanel.add(mapEditor.getEditorPanel(), "editor");  // Ensure this matches the name used in openMapEditor

        // Set the card panel as the content pane
        setContentPane(cardPanel);

        // Initialize the player animation scheduler
        startGameLoop();
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
        // Remove key listeners to ensure a clean state on resume.
        for (KeyListener kl : gamePanel.getKeyListeners()) {
            gamePanel.removeKeyListener(kl);
        }
        menu.showMenu("game");
        repaint();
    }

    public void resumeGame() {
        isPaused = false;
        setupKeyBindingsWithInputMap(gamePanel);  // Reattach keybindings
        startGameLoop();
        menu.hideMenu();
        // Use invokeLater to ensure the focus request is processed after UI updates.
        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            System.out.println("Focus regained in gamePanel.");
        });
        System.out.println("Juego ya no pausado");
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
        mapEditor.getEditorPanel().requestFocusInWindow();
        startGameLoop();
        repaint();
    }

    public void resumeMapEditor() {
        isPaused = false;
        startGameLoop();
        menu.hideMenu();
    }
    
    // Here all the keybinds are difened for player movement
    private void setupKeyBindingsWithInputMap(JPanel panel) {
        // Use the "WHEN_IN_FOCUSED_WINDOW" condition so key actions work as long as the window is active.
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        // Remove any existing bindings (optional, if you want a clean slate)
        inputMap.clear();
        actionMap.clear();

        // Bind the UP key (and W) to an action
        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("W"), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    player.move(0, -1);
                    panel.repaint();
                }
            }
        });

        // Bind the DOWN key (and S)
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("S"), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    player.move(0, 1);
                    panel.repaint();
                }
            }
        });

        // Bind the LEFT key (and A)
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    player.move(-1, 0);
                    panel.repaint();
                }
            }
        });

        // Bind the RIGHT key (and D)
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke("D"), "moveRight");
        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    player.move(1, 0);
                    panel.repaint();
                }
            }
        });

        // Bind ESCAPE to pause the game
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "pauseGame");
        actionMap.put("pauseGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }
        });
    }
    
    // Getters of all variables required for others classes
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