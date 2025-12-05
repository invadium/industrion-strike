package collidium.mix;

public class Mix implements Node {

    public Env env = new Env();

    public Frame lab = new Frame();

    public Mix() {}

    @Override
    public Node __() {
        return null;
    }

    @Override
    public void init() {
    }

    @Override
    public void evo(double dt) {
        this.lab.evo(dt);
    }

    @Override
    public void draw(Context ctx) {
        this.lab.draw(ctx);
    }

    @Override
    public void setParent(Node node) {
        throw new RuntimeException("Unsupported Operation! Can't assign a parent to the mix!");
    }
}
