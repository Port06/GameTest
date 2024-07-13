package com.mycompany.gametest;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextBox {
    public String text;
    private boolean visible;
    private int x, y, width, height;
    private Color backgroundColor;
    private Color textColor;
    private Font font;
    private BufferedImage offscreen;

    public TextBox(int x, int y, int width, int height) {
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
    }

    public void showText(String text) {
        this.text = text;
        this.visible = true;
    }

    public void hideText() {
        this.visible = false;
    }

    public void drawTextBox(Graphics g) {
        if (!visible) {
            return;
        }

        Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, width, height, 20, 20);

        // Draw text
        g2d.setFont(font);
        g2d.setColor(textColor);
        int padding = 10;
        drawString(g2d, text, padding, padding, width - 2 * padding);

        g.drawImage(offscreen, x, y, null);
        g2d.dispose();
    }

    private void drawString(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int curX = x;
        int curY = y + 5; // Offset the text by 5 pixels downwards

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