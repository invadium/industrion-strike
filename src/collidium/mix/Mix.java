package collidium.mix;

import collidium.dna.geo.Background;

public class Mix extends AbstractNode {

    public Env env = new Env();

    public Node background = new Background();

    public Frame lab = new Frame();

    public Mix() {}

    @Override
    public Node __() {
        return null;
    }

    @Override
    public void init() {}

    @Override
    public void evo(float dt) {
        this.lab.evo(dt);
    }

    @Override
    public void draw(Context ctx) {
        if (this.background.isVisible()) this.background.draw(ctx);
        if (this.lab.isVisible()) this.lab.draw(ctx);
    }

    @Override
    public void setParent(Node node) {
        throw new RuntimeException("Unsupported Operation! Can't assign a parent to the mix!");
    }

}
