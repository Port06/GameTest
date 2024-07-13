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
    private MapEditor mapEditor;

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
        menuPanel.setLayout(null); // Use absolute positioning for the button

        // Create the resume button and position it within the menu panel
        resumeButton = new JButton("Resume");
        resumeButton.setBounds(game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 100, 30); // Position the button
        resumeButton.addActionListener(e -> game.resumeGame());
        menuPanel.add(resumeButton);

        // Create the map editor button and position it within the menu panel
        mapEditorButton = new JButton("Map Editor");
        mapEditorButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 100, 30); // Position the button
        mapEditorButton.addActionListener(e -> openMapEditor());  // Add action listener to call openMapEditor
        menuPanel.add(mapEditorButton);

        // Create the MapEditor instance
        mapEditor = new MapEditor();

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
                resumeButton.setBounds(game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 100, 30);
                mapEditorButton.setBounds(3 * game.getWidth() / 4 - 50, (menuPanel.getHeight() - 30) / 2, 100, 30);

                // Repaint to apply the changes
                menuPanel.repaint();
            }
        });
    }

    public void showMenu() {
        menuPanel.setVisible(true);
    }

    public void hideMenu() {
        menuPanel.setVisible(false);
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw semi-transparent background
        g2d.setColor(new Color(128, 128, 128, 192)); // Grayish with some transparency
        g2d.fillRect(0, game.getHeight() / 3, game.getWidth(), game.getHeight() / 3); // 1/3 of the height centered vertically
    }

    private void openMapEditor() {
        mapEditor.openEditor(); // Call the method in the MapEditor class to open the editor
    }
}