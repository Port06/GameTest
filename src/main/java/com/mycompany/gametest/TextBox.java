package com.mycompany.gametest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class TextBox {
    private int x, y, width, height;
    private String text;
    private Color backgroundColor;
    private Color textColor;
    private Font font;
    private boolean visible;

    public TextBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = new Color(0, 0, 0, 128); // Semi-transparent
        this.textColor = Color.WHITE;
        this.font = new Font("16x16", Font.PLAIN, 16); // Use your specific font
        this.visible = false;
    }

    public void draw(Graphics g) {
        if (visible) {
            g.setColor(backgroundColor);
            g.fillRect(x, y, width, height);
            g.setColor(textColor);
            g.setFont(font);
            g.drawString(text, x + 5, y + 15); // Adjust position as needed
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void show() {
        this.visible = true;
    }

    public void hide() {
        this.visible = false;
    }
}