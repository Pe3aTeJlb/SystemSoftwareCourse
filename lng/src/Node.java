import java.util.ArrayList;

public class Node {

    private String name = null;
    private ArrayList<Node> children = new ArrayList<>();

    public Node(String name) {
        this.name = name;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public ArrayList<Node> getChild() {
        return children;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
