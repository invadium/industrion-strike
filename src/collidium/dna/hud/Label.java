package collidium.dna.hud;

import java.awt.Color;
import java.awt.Font;

import collidium.mix.AbstractNode;
import collidium.mix.Context;

public class Label extends AbstractNode {

    private String text;

    private Color color = Color.WHITE;

    private Font font = new Font("Arial", Font.BOLD, 32);

    private float x, y;

    public Label(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.running = false;
    }

    @Override
    public void evo(float dt) {}

    @Override
    public void draw(Context ctx) {
        ctx.g.setColor(this.color);
        ctx.g.setFont(this.font);
        ctx.g.drawString(text, this.x, this.y);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}