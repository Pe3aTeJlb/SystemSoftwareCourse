import java.util.ArrayList;
import java.util.Collections;

public class Node {

    private String name;

    private ArrayList<Node> children = new ArrayList<>();

    private ArrayList<Lexeme> lexemes = new ArrayList<>();


    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    public void addChild(Node node) { children.add(node); }

    public ArrayList<Node> getChild() {
        return children;
    }

    public ArrayList<Node> getReversedChild() {
        ArrayList<Node> buff = new ArrayList<>(children);
        Collections.reverse(buff);
        return buff;
    }



    public void addLexeme(Lexeme lexeme) {
        lexemes.add(lexeme);
    }

    public ArrayList<Lexeme> getLexemes(){return lexemes;}


}
