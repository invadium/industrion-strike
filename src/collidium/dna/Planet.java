package collidium.dna;

import collidium.mix.AbstractNode;
import collidium.mix.Context;
import collidium.mix.Env;
import collidium.mix.Node;

import java.awt.Color;
import java.awt.Font;

public class Planet extends AbstractNode {

    private double x = 0, y = 0;

    @Override
    public void evo(double dt) {
        this.x += 20 * dt;
        this.y += 20 * dt;
    }

    @Override
    public void draw(Context ctx) {
        Env env = ctx.$.env;

        ctx.g2.setColor(Color.getHSBColor(.5f, .5f, .4f));
        ctx.g2.fillRect((int)this.x, (int)this.y, 100, 100);
        ctx.g2.setColor(Color.RED);
        ctx.g2.setFont(new Font("Arial", Font.BOLD, 32));
        ctx.g2.drawString("Time: " + Math.floor(env.time), env.width/2, env.height/2);
    }

}
