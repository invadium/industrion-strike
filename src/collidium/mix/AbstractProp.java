package collidium.mix;

public abstract class AbstractProp extends AbstractNode {

    public AbstractProp() {
        this.running = false;
    }

    @Override
    public void evo(float dt) {}

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void setRunning(boolean running) {
        throw new RuntimeException("Can't set the 'running' property of a prop node!");
    }
}