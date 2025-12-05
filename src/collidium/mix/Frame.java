package collidium.mix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frame implements Node {

    private Node __;

    public List<Node> ls = new ArrayList<>();

    public Map<String, Node> dir = new HashMap<>();

    public Frame() {}

    @Override
    public void init() {
    }

    public void attach(Node node) {
        this.ls.add(node);
        if (node instanceof Named) {
            dir.put(((Named)node).getName(), node);
        }
        node.setParent(this);
        node.init();
    }

    @Override
    public void evo(double dt) {
        ls.forEach(node -> node.evo(dt));
    }

    @Override
    public void draw(Context ctx) {
        ls.forEach(node -> node.draw(ctx));
    }

    @Override
    public Node __() {
        return this.__;
    }

    @Override
    public void setParent(Node node) {
        this.__ = node;
    }
}