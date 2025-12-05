package collidium.mix;

public interface Node {

    public Node __();

    public void init();

    public void evo(double dt);

    public void draw(Context ctx);

    public void setParent(Node node);

}
