package com.mycompany.gametest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Menu {
    private Game game;
    private JPanel menuPanel;
    private JButton resumeButton;
    private JButton mapEditorButton;
    private JButton switchToGameButton;
    private boolean gamePanelVisible; // Add this variable

    public Menu(Game game) {
        this.game = game;

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
        menuPanel.setLayout(null); // Use absolute positioning for the buttons

        // Create the resume button and position it within the menu panel
        resumeButton = new JButton("Resume");
        resumeButton.setBounds(game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30); // Position the button
        resumeButton.addActionListener(e -> {
            if (game.getPaused()) {
                game.resumeGame();
                menuPanel.requestFocusInWindow(); // Ensure focus is set to the menu panel
            } else {
                game.resumeMapEditor();
                menuPanel.requestFocusInWindow(); // Ensure focus is set to the menu panel
            }
        });
        menuPanel.add(resumeButton);

        // Create the map editor button and position it within the menu panel
        mapEditorButton = new JButton("Map Editor");
        mapEditorButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30); // Position the button
        mapEditorButton.addActionListener(e -> {
            if (game.getPaused()) {
                game.openMapEditor();
            }
        });
        menuPanel.add(mapEditorButton);

        // Create the switch to game button and position it within the menu panel
        switchToGameButton = new JButton("Go to Game");
        switchToGameButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30); // Position the button
        switchToGameButton.addActionListener(e -> {
            if (game.getPaused()) {
                game.openGameView();
            }
        });
        switchToGameButton.setVisible(false); // Initially hidden
        menuPanel.add(switchToGameButton);

        // Add the menu panel to the game's layered pane
        game.getLayeredPane().add(menuPanel, JLayeredPane.PALETTE_LAYER);
        menuPanel.setVisible(false); // Initially hidden

        // Add a component listener to handle resizing
        game.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update menu panel size and position
                menuPanel.setBounds(0, game.getHeight() / 3, game.getWidth(), game.getHeight() / 3);

                // Update bounds of the buttons
                resumeButton.setBounds(game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
                mapEditorButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
                switchToGameButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);

                // Repaint to apply the changes
                menuPanel.repaint();
            }
        });
    }

    public void showMenu(String context) {
        switch (context) {
            case "game":
                mapEditorButton.setVisible(true);
                switchToGameButton.setVisible(false);

                // Disable interaction with the game panel
                game.getGamePanel().setEnabled(false);  // Replace with the correct method to get game panel

                break;
            case "editor":
                mapEditorButton.setVisible(false);
                switchToGameButton.setVisible(true);

                // Enable interaction with the game panel
                game.getGamePanel().setEnabled(true);  // Replace with the correct method to get game panel

                break;
        }

        gamePanelVisible = true; // Set the variable to true when menu is shown
        menuPanel.setVisible(true);
        menuPanel.requestFocusInWindow(); // Ensure focus is set to the menu panel
    }

    public void hideMenu() {
        gamePanelVisible = false; // Set the variable to false when menu is hidden
        menuPanel.setVisible(false);
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw semi-transparent background
        g2d.setColor(new Color(128, 128, 128, 192)); // Grayish with some transparency
        g2d.fillRect(0, game.getHeight() / 3, game.getWidth(), game.getHeight() / 3); // 1/3 of the height centered vertically
    }

    // Getter for the gamePanelVisible variable
    public boolean isGamePanelVisible() {
        return gamePanelVisible;
    }
}