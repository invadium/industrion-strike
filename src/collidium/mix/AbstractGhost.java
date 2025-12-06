package collidium.mix;

public abstract class AbstractGhost extends AbstractNode {

    public AbstractGhost() {
        this.visible = false;
    }

    @Override
    public void draw(Context ctx) {}

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        throw new RuntimeException("Can't set the 'visible' property of a ghost node!");
    }
}
