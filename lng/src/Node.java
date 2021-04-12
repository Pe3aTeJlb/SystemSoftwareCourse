import java.util.ArrayList;
import java.util.Collections;

public class Node {

    private String name = null;

    private ArrayList<Node> children = new ArrayList<>();

    private ArrayList<Lexeme> lexemes = new ArrayList<Lexeme>();

    public Node(String name) {
        this.name = name;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public ArrayList<Node> getChild() {
        return children;
    }

    public ArrayList<Node> getReversedChild() {
        ArrayList<Node> buff = new ArrayList<>(children);
        Collections.reverse(buff);
        return buff;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addLexeme(Lexeme lexeme) {
        lexemes.add(lexeme);
    }

    public ArrayList<Lexeme> getLexemes(){return lexemes;}

    public ArrayList<Lexeme> getReverseLexemes(){

        ArrayList<Lexeme> buff = new ArrayList<>(lexemes);
        Collections.reverse(buff);
        return buff;
    }


}
