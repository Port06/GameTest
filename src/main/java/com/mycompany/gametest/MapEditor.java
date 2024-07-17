package com.mycompany.gametest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.HashMap;

public class MapEditor {

    private JPanel editorPanel;
    private JComboBox<String> boardSelector;
    private JPanel mapPanel;
    private JPanel assetPanel; // Asset panel declaration
    private JScrollPane assetScrollPane; // Scroll pane for the asset panel
    private Game game;
    private GameRenderer gameRenderer;
    private BufferedImage offscreen;

    public MapEditor(Game game) {
        this.game = game;
        this.gameRenderer = new GameRenderer(game.getMap(), game.getPlayer());
        initializeEditorPanel();

        // Ensure the map panel is focusable and requests focus properly
        mapPanel.setFocusable(true);
        mapPanel.requestFocusInWindow(); // Ensure map panel requests focus immediately
    }

    private void initializeEditorPanel() {
        editorPanel = new JPanel(new BorderLayout());

        // Create the board selector
        boardSelector = new JComboBox<>(game.getMap().getBoardNames().toArray(new String[0]));
        boardSelector.addActionListener(e -> {
            String selectedBoard = (String) boardSelector.getSelectedItem();
            loadBoard(selectedBoard);
            mapPanel.requestFocusInWindow(); // Request focus back to map panel after selecting board
        });

        // Create the map panel
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (game.getMap().getCurrentBoard() != null) {
                    gameRenderer.drawBoard(g, offscreen, getWidth(), getHeight());
                }
            }
        };

        mapPanel.setFocusable(true);
        mapPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    game.pauseMapEditor();
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    // If the menu is not visible, pause the game and show the menu
                    if (!game.getMenu().isGamePanelVisible()) {
                        System.out.println("Opening menu");
                        game.pauseMapEditor();
                    }
                }
            }
        });

        // Create the asset panel with GridBagLayout
        assetPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // Reduce padding between elements
        gbc.gridy = 0; // Start from the first row

        // Populate the asset panel with images
        populateAssetPanel();

        // Create the scroll pane for the asset panel
        assetScrollPane = new JScrollPane(assetPanel);
        assetScrollPane.setPreferredSize(new Dimension(0, 100)); // Set height to 100
        assetScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        assetScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        editorPanel.add(boardSelector, BorderLayout.NORTH);
        editorPanel.add(mapPanel, BorderLayout.CENTER);
        editorPanel.add(assetScrollPane, BorderLayout.SOUTH);

        // Request focus for the map panel after it is added to the editor panel
        mapPanel.requestFocusInWindow();
    }

    private void populateAssetPanel() {
        System.out.println("Populating asset panel...");
        assetPanel.removeAll(); // Clear existing components

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between elements
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align components to the top-left corner
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally

        int col = 0;
        int row = 0;

        // Iterate through all states in Assets.textures
        for (CellState state : Assets.textures.keySet()) {
            Map<Integer, BufferedImage> textureMap = Assets.textures.get(state);
            for (int textureId : textureMap.keySet()) {
                BufferedImage texture = textureMap.get(textureId);
                if (texture != null) {
                    System.out.println("Loading texture for state " + state + " with ID: " + textureId);
                    Image scaledImage = texture.getScaledInstance(32, 32, Image.SCALE_SMOOTH); // Resize image to 32x32
                    ImageIcon icon = new ImageIcon(scaledImage);
                    JLabel textureLabel = new JLabel(icon);
                    textureLabel.setToolTipText(state + " ID: " + textureId);

                    // Reset constraints for each label
                    gbc.gridx = col; // Set column for current component
                    gbc.gridy = row; // Set row for current component

                    // Add texture label to asset panel with current GridBagConstraints
                    assetPanel.add(textureLabel, gbc);

                    // Increment column index
                    col++;

                    // Check if we need to wrap to the next row
                    if (col % 2 == 0) {
                        // Increment row index and reset column index
                        row++;
                        col = 0;
                    }
                } else {
                    System.out.println("Texture is null for state " + state + " with ID: " + textureId);
                }
            }
        }

        assetPanel.revalidate();
        assetPanel.repaint();
        System.out.println("Asset panel populated.");
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    public JPanel getMapPanel() {
        return mapPanel;
    }

    public void loadBoard(String boardName) {
        if (!game.getMenu().isGamePanelVisible()) {
            game.getMap().setCurrentBoard(boardName);
            editorPanel.requestFocusInWindow(); // Ensure focus for editorPanel
            mapPanel.repaint();
        }
    }

    public void openEditor() {
        game.openMapEditor();
        editorPanel.requestFocusInWindow(); // Ensure focus for editorPanel
    }

    public void requestFocusInMapEditor() {
        mapPanel.requestFocusInWindow(); // Ensure map panel requests focus
    }
}