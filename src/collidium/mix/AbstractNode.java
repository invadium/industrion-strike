package collidium.mix;

public abstract class AbstractNode implements Node {

    protected Node __;

    protected boolean running = true;

    protected boolean visible = true;

    @Override
    public void init() {}

    @Override
    public Node __() {
        return this.__;
    }

    @Override
    public void setParent(Node node) {
        this.__ = node;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

}
