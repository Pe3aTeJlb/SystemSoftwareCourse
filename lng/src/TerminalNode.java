import java.util.ArrayList;
import java.util.Collections;

public class TerminalNode extends Node{

    private ArrayList<Lexeme> children = new ArrayList<Lexeme>();

    public TerminalNode(String name) {
        super(name);
    }

    public void addLexeme(Lexeme lexeme) {
        children.add(lexeme);
    }

    public ArrayList<Lexeme> getLexemes(){return children;}

    public ArrayList<Lexeme> getReverseLexemes(){

        ArrayList<Lexeme> buff = new ArrayList<>(children);
        Collections.reverse(buff);
        return buff;
    }

}
