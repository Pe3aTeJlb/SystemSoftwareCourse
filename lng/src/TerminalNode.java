import java.util.ArrayList;

public class TerminalNode extends Node{

    ArrayList<Lexeme> children = new ArrayList<Lexeme>();

    public TerminalNode(String name) {
        super(name);
    }

    public void addChild(Lexeme lexeme) {
        children.add(lexeme);
    }

    public ArrayList<Lexeme> getChildren(){return children;}
}
