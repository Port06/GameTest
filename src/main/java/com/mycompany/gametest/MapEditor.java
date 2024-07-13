package com.mycompany.gametest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditor {

    private JPanel editorPanel;
    private JComboBox<String> boardSelector;
    private JPanel mapPanel;
    private Game game;
    private GameRenderer gameRenderer;
    private BufferedImage offscreen;

    public MapEditor(Game game) {
        this.game = game;
        this.gameRenderer = new GameRenderer(game.getMap(), game.getPlayer());
        initializeEditorPanel();
        mapPanel.requestFocusInWindow(); // Ensure map panel requests focus immediately
    }

    private void initializeEditorPanel() {
        editorPanel = new JPanel(new BorderLayout());

        // Create the board selector
        boardSelector = new JComboBox<>(game.getMap().getBoardNames().toArray(new String[0]));
        boardSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBoard = (String) boardSelector.getSelectedItem();
                loadBoard(selectedBoard);
                mapPanel.requestFocusInWindow(); // Request focus back to map panel after selecting board
            }
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

        editorPanel.add(boardSelector, BorderLayout.NORTH);
        editorPanel.add(mapPanel, BorderLayout.CENTER);

        // Request focus for the map panel after it is added to the editor panel
        mapPanel.requestFocusInWindow();
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    public void loadBoard(String boardName) {
        if (!game.getMenu().isGamePanelVisible()) { // Check if the game panel menu is not visible
            game.getMap().setCurrentBoard(boardName);
            mapPanel.requestFocusInWindow();
            mapPanel.repaint();
        }
    }

    public void openEditor() {
        game.openMapEditor();
        mapPanel.requestFocusInWindow(); // Request focus when the map editor is opened
    }
    
    public void requestFocusInMapEditor() {
        // Request focus for the map panel itself
        mapPanel.requestFocusInWindow();
    }
}