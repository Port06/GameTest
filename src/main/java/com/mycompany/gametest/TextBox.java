package com.mycompany.gametest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TextBox {
    // Text and visibility settings
    public String text;
    private boolean visible;
    
    // Position and dimensions for rendering
    private int x, y, width, height;
    
    // Appearance settings
    private Color backgroundColor;
    private Color textColor;
    private Font font;
    private BufferedImage offscreen;
    
    // Queue-related fields for managing multiple text boxes
    private Queue<TextBox> textBoxQueue;
    private TextBox currentTextBox;
    private ScheduledExecutorService textBoxScheduler;
    
    // Reference to the game for calling repaint (or other game-wide operations)
    private Game game;

    /**
     * Constructor for the main TextBox manager instance.
     * This instance is responsible for creating and displaying additional text boxes.
     */
    public TextBox(int x, int y, int width, int height, Game game) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = "";
        this.visible = false;
        this.backgroundColor = new Color(0, 0, 0, 128); // Semi-transparent black
        this.textColor = Color.WHITE;
        this.font = new Font("Arial", Font.PLAIN, 16);
        this.offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.game = game;
        
        // Initialize the queue that will hold additional text boxes.
        this.textBoxQueue = new LinkedList<>();  // Initialize the queue here
    }

    /**
     * Displays this text box with the given text.
     */
    public void showText(String text) {
        this.text = text;
        this.visible = true;
    }

    /**
     * Hides this text box.
     */
    public void hideText() {
        this.visible = false;
    }
    
    /**
     * Creates a new TextBox with the provided parameters, adds it to the queue,
     * and triggers the display of the next text box if none is currently active.
     *
     * @param text   The text to display.
     * @param x      The x position of the new text box.
     * @param y      The y position of the new text box.
     * @param width  The width of the new text box.
     * @param height The height of the new text box.
     */
    public void addTextBox(String text, int x, int y, int width, int height) {
        TextBox newTextBox = new TextBox(x, y, width, height, game);  // Create a new TextBox instance
        newTextBox.showText(text);
        textBoxQueue.add(newTextBox);
        if (currentTextBox == null) {
            showNextTextBox();
        }
    }
    
    /**
     * Displays the next text box from the queue.
     * This method polls the queue and schedules the next text box display
     * after a calculated display time.
     */
    public void showNextTextBox() {
        if (!textBoxQueue.isEmpty()) {
            currentTextBox = textBoxQueue.poll();
            // Make sure the current text box is marked as visible.
            currentTextBox.showText(currentTextBox.text);
            
            // Repaint the game so that the new text box is rendered.
            game.repaint();

            // Calculate display time based on the queue size.
            int queueSize = textBoxQueue.size();
            long displayTime = Math.max(250, 2000 - (queueSize * 300)); // Minimum display time is 250ms

            // Schedule the display of the next text box.
            textBoxScheduler = Executors.newSingleThreadScheduledExecutor();
            textBoxScheduler.schedule(this::showNextTextBox, displayTime, TimeUnit.MILLISECONDS);
        } else {
            // No more text boxes in the queue.
            currentTextBox = null;
            if (textBoxScheduler != null && !textBoxScheduler.isShutdown()) {
                textBoxScheduler.shutdownNow();
            }
        }
    }

    /**
     * Draws the text box if it is visible.
     *
     * @param g The Graphics context to use for drawing.
     */
    public void drawTextBox(Graphics g) {
        // Only draw if this text box is visible.
        if (!visible) {
            return;
        }

        Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the semi-transparent background.
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, width, height, 20, 20);

        // Draw the text.
        g2d.setFont(font);
        g2d.setColor(textColor);
        int padding = 10;
        drawString(g2d, text, padding, padding, width - 2 * padding);

        // Render the offscreen image onto the provided Graphics context.
        g.drawImage(offscreen, x, y, null);
        g2d.dispose();
    }

    /**
     * Helper method to draw a multi-line string within a given width.
     *
     * @param g2d      The Graphics2D context.
     * @param text     The text to draw.
     * @param x        The x starting position.
     * @param y        The y starting position.
     * @param maxWidth The maximum width allowed for the text.
     */
    private void drawString(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int curX = x;
        int curY = y + 5; // Slight vertical offset

        for (String word : text.split(" ")) {
            int wordWidth = fm.stringWidth(word + " ");
            if (curX + wordWidth >= x + maxWidth) {
                curX = x;
                curY += lineHeight;
            }
            g2d.drawString(word, curX, curY);
            curX += wordWidth;
        }
    }
    
    public void drawActiveTextBox(Graphics g) {
        if (currentTextBox != null && currentTextBox.visible) {
            currentTextBox.drawTextBox(g);
        }
    }

    // Setters for appearance properties.
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public boolean isVisible() {
        return visible;
    }
}