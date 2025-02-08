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
    private boolean gamePanelVisible;

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
        menuPanel.setFocusable(false);
        menuPanel.setOpaque(true);
        menuPanel.setBackground(new Color(128, 128, 128, 192));
        menuPanel.setLayout(null);

        // Create the resume button and position it within the menu panel
        resumeButton = new JButton("Resume");
        resumeButton.setBounds(game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
        resumeButton.addActionListener(e -> {
            if (game.getPaused()) {
                System.out.println(game.getPaused());
                game.resumeGame();
                game.getGamePanel().requestFocusInWindow();
                hideMenu();
            } else {
                game.resumeMapEditor();
                game.getMapEditor().getEditorPanel().requestFocusInWindow();
                hideMenu();
            }
        });
        menuPanel.add(resumeButton);

        // Create the map editor button and position it within the menu panel
        mapEditorButton = new JButton("Map Editor");
        mapEditorButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
        mapEditorButton.addActionListener(e -> {
            if (game.getPaused()) {
                game.openMapEditor();
            }
        });
        menuPanel.add(mapEditorButton);

        // Create the switch to game button and position it within the menu panel
        switchToGameButton = new JButton("Go to Game");
        switchToGameButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
        switchToGameButton.addActionListener(e -> {
            if (game.getPaused()) {
                game.openGameView();
            }
        });
        switchToGameButton.setVisible(false); // Initially hidden
        menuPanel.add(switchToGameButton);

        // Add the menu panel to the game's layered pane
        game.getLayeredPane().add(menuPanel, JLayeredPane.PALETTE_LAYER);
        menuPanel.setVisible(false);

        // Add a component listener to handle resizing
        game.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                menuPanel.setBounds(0, game.getHeight() / 3, game.getWidth(), game.getHeight() / 3);

                // Update bounds of the buttons
                resumeButton.setBounds(game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
                mapEditorButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);
                switchToGameButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 120, 30);

                menuPanel.repaint();
            }
        });
    }

    public void showMenu(String context) {
        switch (context) {
            case "game":
                mapEditorButton.setVisible(true);
                switchToGameButton.setVisible(false);
                game.getGamePanel().setEnabled(false);
                break;
            case "editor":
                mapEditorButton.setVisible(false);
                switchToGameButton.setVisible(true);
                game.getGamePanel().setEnabled(true);
                break;
        }

        gamePanelVisible = true;
        menuPanel.setVisible(true);
    }

    public void hideMenu() {
        menuPanel.setVisible(false);
        game.repaint();
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw semi-transparent background
        g2d.setColor(new Color(128, 128, 128, 192));
        g2d.fillRect(0, game.getHeight() / 3, game.getWidth(), game.getHeight() / 3);
    }

    public boolean isGamePanelVisible() {
        return gamePanelVisible;
    }

    public JPanel getMenuPanel() {
        return menuPanel;
    }
}