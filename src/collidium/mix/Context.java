package collidium.mix;

import java.awt.Graphics2D;

public class Context {

    public final Mix $;

    public final Graphics2D g;

    public Context(Mix $, Graphics2D g) {
        this.$ = $;
        this.g = g;
    }

}