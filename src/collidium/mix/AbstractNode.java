package collidium.mix;

public abstract class AbstractNode implements Node {
    private Node __;

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
}
