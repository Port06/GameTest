package com.mycompany.gametest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditor {

    private JFrame editorFrame;
    private JComboBox<String> boardSelector;
    private JPanel mapPanel; // Panel to display the map
    private String[] availableBoards = {"Board 1", "Board 2", "Board 3"}; // Example boards

    public void openEditor() {
        // Initialize the editor frame
        editorFrame = new JFrame("Map Editor");
        editorFrame.setSize(800, 600);
        editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editorFrame.setLayout(new BorderLayout());

        // Create and add the dropdown menu for board selection
        boardSelector = new JComboBox<>(availableBoards);
        boardSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic to switch boards
                String selectedBoard = (String) boardSelector.getSelectedItem();
                loadBoard(selectedBoard);
            }
        });

        // Create the map panel
        mapPanel = new JPanel();
        mapPanel.setBackground(Color.GRAY); // Placeholder color

        // Add components to the editor frame
        editorFrame.add(boardSelector, BorderLayout.NORTH);
        editorFrame.add(mapPanel, BorderLayout.CENTER);

        // Display the editor frame
        editorFrame.setVisible(true);

        // Load the initial board
        loadBoard(availableBoards[0]);
    }

    private void loadBoard(String boardName) {
        // Logic to load the selected board and display it in the map panel
        System.out.println("Loading board: " + boardName);
        // Placeholder logic to update the map panel
        mapPanel.removeAll();
        mapPanel.add(new JLabel("Displaying " + boardName));
        mapPanel.revalidate();
        mapPanel.repaint();
    }
}