package collidium.dna;

import collidium.mix.AbstractNode;
import collidium.mix.Context;
import collidium.mix.Env;
import collidium.mix.Mix;

import java.awt.Color;

public class Planet extends AbstractNode {

    private Color color;

    private float x, y, r;

    private Mix mix;

    private float dx = 1, dy = 1;

    private float speed = 200;

    public Planet(float x, float y, float r, Color color, Mix mix) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
        this.mix = mix;
    }

    @Override
    public void evo(float dt) {
        Env env = this.mix.env;

        this.x += this.dx * this.speed * dt;
        this.y += this.dy * this.speed * dt;

        if (this.x <= this.r && this.dx < 0) this.dx = 1;
        else if (this.x >= env.width - this.r && this.dx > 0) this.dx = -1;

        if (this.y <= this.r && this.dy < 0) this.dy = 1;
        else if (this.y >= env.height - this.r && this.dy > 0) this.dy = -1;
    }

    @Override
    public void draw(Context ctx) {
        Env env = ctx.$.env;

        //ctx.g.setColor(Color.getHSBColor(.5f, .5f, .4f));
        ctx.g.setColor(this.color);
        ctx.g.fillArc(
            (int)(this.x - this.r),
            (int)(this.y - this.r),
            ((int)this.r) << 1,
            ((int)this.r) << 1,
            0, 360);
        //ctx.g.fillRect((int)this.x, (int)this.y, 100, 100);
    }

}