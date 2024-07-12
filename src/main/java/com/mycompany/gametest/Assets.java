package com.mycompany.gametest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class Assets {
    private static final int TILE_SIZE = 16;
    public static Map<CellState, Map<Integer, BufferedImage>> textures = new HashMap<>();
    public static BufferedImage emptyTexture;  // Assuming a single texture for EMPTY
    public static BufferedImage playerTexture; // Assuming a single texture for PLAYER

    public static int getTileSize() {
        return TILE_SIZE;
    }

    public static void init() {
        // Initialize the textures map for each CellState
        for (CellState state : CellState.values()) {
            textures.put(state, new HashMap<>());
        }

        loadTexturesFromDirectory("/grass", CellState.EMPTY);
        loadTexturesFromDirectory("/rocks", CellState.WALL);
        loadTexturesFromDirectory("/player", CellState.PLAYER);
        loadTexturesFromDirectory("/trees", CellState.WALL);
        loadTexturesFromDirectory("/portal", CellState.BOARDSWITCH);
    }

    private static void loadTexturesFromDirectory(String directoryPath, CellState state) {
        try {
            File directory = new File(Assets.class.getResource(directoryPath).getPath());
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    BufferedImage texture = ImageIO.read(file);
                    
                    String fileName = file.getName();
                    int textureId;
                    try {
                        // Assuming format: textureId_rest_of_filename.png
                        textureId = Integer.parseInt(fileName.split("_")[0]); 
                    } catch (NumberFormatException e) {
                        // Skip this file if texture ID parsing fails
                        System.err.println("Skipping file with invalid format: " + fileName);
                        continue;
                    }
                    
                    if (textures.get(state).containsKey(textureId)) {
                        System.err.println("Duplicate texture ID found for state " + state + " and texture ID " + textureId + ": " + fileName);
                    } else {
                        textures.get(state).put(textureId, texture);
                        System.out.println("Loaded texture with ID: " + textureId + " for state: " + state);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getTexture(CellState state, int textureId) {
        return textures.get(state).get(textureId);
    }
}