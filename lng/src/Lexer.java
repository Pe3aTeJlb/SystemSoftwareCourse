
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private  List<Terminal> TERMINALS = List.of(

            new Terminal("VAR", "^[a-zA-Z_]{1}\\w*$"),
            new Terminal("NUMBER", "0|-?[1-9][0-9]*"),

            new Terminal("ASSIGN_OP", "="),
            new Terminal("LOGICAL_OP", "==|!=|>|<"),
            new Terminal("OP", "[+-/*]",1),

            new Terminal("IF_KW", "if", 1),
            new Terminal("ELSE_KW", "else", 1),

            new Terminal("WHILE_KW", "while", 1),
            new Terminal("DO_KW", "do", 1),

            new Terminal("NEW_KW", "(new)",1),

            new Terminal("DLL_KW", "DoubleLinkedList",1),
            new Terminal("DLL_FUNC_KW", "(addFront)|(addEnd)|(addBefore)|(addAfter)" +
                    "|(forward)|(backward)|(toFront)|(toEnd)",1),

            new Terminal("HASHMAP_KW", "HashMap",1),
            new Terminal("HASHMAP_FUNC_KW", "(put)|(containsKey)",1),

            new Terminal("COMMON_FUNC_KW", "(get)|(remove)|(size)|(clear)|(isEmpty)|(print)",1),

            new Terminal("L_BR", "\\("),
            new Terminal("R_BR", "\\)"),

            new Terminal("L_S_BR", "\\{"),
            new Terminal("R_S_BR", "\\}"),

            new Terminal("CM", ";"),
            new Terminal("WS", "\\s+")


    );

    private ArrayList<Lexeme> lexemes;

    public void readFile(String filePath) {

        StringBuilder fileData = new StringBuilder();

        lexemes = new ArrayList<>();

        try {

            FileReader reader = new FileReader(filePath);

            char[] buf = new char[1024];
            int numRead=0;

            while ((numRead=reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }

            reader.close();
            fileData.append("$");

            while (fileData.charAt(0) != '$') {

                Lexeme lexeme = extractNextLexeme(fileData);

                if (!lexeme.getTerminal().getName().equals("WS")){
                    lexemes.add(lexeme);
                }

                fileData.delete(0, lexeme.getValue().length());

            }

        }
        catch(IOException e) {
            System.out.println("Cant read File " + e.getMessage());
        }

    }

    private Lexeme extractNextLexeme(StringBuilder input) {

        StringBuilder buffer = new StringBuilder();
        buffer.append(input.charAt(0));

        if (anyTerminalMatches(buffer)) {

            while (anyTerminalMatches(buffer) && buffer.length() != input.length()) {
                buffer.append(input.charAt(buffer.length()));
            }

            buffer.deleteCharAt(buffer.length() - 1);

            List<Terminal> terminals = lookupTerminals(buffer);

            return new Lexeme(getPrioritizedTerminal(terminals), buffer.toString());
        } else {
            throw new RuntimeException("Unexpected symbol " + buffer);
        }

    }

    private Terminal getPrioritizedTerminal(List<Terminal> terminals) {

        Terminal prioritizedTerminal = terminals.get(0);

        for (Terminal terminal : terminals) {

            if (terminal.getPriority() > prioritizedTerminal.getPriority()) {
                prioritizedTerminal = terminal;
            }

        }

        return prioritizedTerminal;

    }

    private boolean anyTerminalMatches(StringBuilder buffer) {
        return lookupTerminals(buffer).size() != 0;
    }

    private List<Terminal> lookupTerminals(StringBuilder buffer) {

        List<Terminal> terminals = new ArrayList<>();

        for (Terminal terminal : TERMINALS) {

            if (terminal.matches(buffer)) {
                terminals.add(terminal);
            }

        }

        return terminals;
    }

    private StringBuilder lookupInput(String[] args) {

        if (args.length == 0) {
            throw new IllegalArgumentException("Input string not found");
        }

        StringBuilder buff = new StringBuilder();

        for (String arg : args) {
            buff.append(arg).append(" ");
        }

        return buff;

    }

    public ArrayList<Lexeme> getLexemes(){
        return lexemes;
    }

    public void print() {

        for (Lexeme lexeme : lexemes) {
            System.out.printf("[%s, %s]%n",
                    lexeme.getTerminal().getName(),
                    lexeme.getValue());
        }

    }

}
