package collidium.mix;

public interface Node {

    public Node __();

    public void setParent(Node node);

    public void init();


    public boolean isRunning();

    public void setRunning(boolean running);

    public void evo(float dt);


    public boolean isVisible();

    public void setVisible(boolean visible);

    public void draw(Context ctx);

}