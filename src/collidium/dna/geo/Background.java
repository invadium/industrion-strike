package collidium.dna.geo;

import java.awt.Color;

import collidium.mix.AbstractProp;
import collidium.mix.Context;
import collidium.mix.Env;

public class Background extends AbstractProp{

    private Color color = Color.BLACK;

    public Background() {}

    public Background(Color color) {
        this.color = color;
    }

    @Override
    public void draw(Context ctx) {
        Env env = ctx.$.env;

        // fill the background
        ctx.g.setColor(this.color);
        ctx.g.fillRect(0, 0, env.width, env.height);
    }

}
