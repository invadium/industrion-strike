package collidium.mix;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Context {

    public final Mix $;

    public final Graphics g;

    public final Graphics2D g2;

    public Context(Mix $, Graphics g, Graphics2D g2) {
        this.$ = $;
        this.g = g;
        this.g2 = g2;
    }

}