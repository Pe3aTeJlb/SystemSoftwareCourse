
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexer {

    private  ArrayList<Terminal> TERMINALS = new ArrayList<Terminal>(
            Arrays.asList(

            //new Terminal("VAR", "^[a-zA-Z_]{1}[a-zA-Z_0-9]{0,}$"),
                    new Terminal("VAR", "^[a-zA-Z_]{1}\\w*$"),
            new Terminal("NUMBER", "0|[1-9][0-9]*"),

            new Terminal("ASSIGN_OP", "="),
            new Terminal("LOGICAL_OP", "==|!=|>|<"),
            new Terminal("OP", "[+-/*]|\\+\\+|\\-\\-"),

            new Terminal("FOR_KW", "for", 1),

            new Terminal("IF_KW", "if", 1),
            new Terminal("ELSE_KW", "else", 1),

            new Terminal("WHILE_KW", "while", 1),
            new Terminal("DO_KW", "do", 1),

            new Terminal("L_BR", "\\("),
            new Terminal("R_BR", "\\)"),

            new Terminal("L_S_BR", "\\{"),
            new Terminal("R_S_BR", "\\}"),

            new Terminal("SC",";"),
            new Terminal("VAR_TYPE", "int|str|float", 1),

            new Terminal("WS", "\\s+")

            )
    );

    private ArrayList<Lexeme> lexemes = new ArrayList<>();


    /*
 private String var = "^[a-zA-Z_]{1}[a-zA-Z_0-9]{0,}$";
    private String op = "[\\+ \\| \\- \\| \\* \\| \\/]";
    private String num = "[0|[1-9][0-9]*]";
    private String assing_op = "[=]";
    private String logical_op = "[&& | \\|| | % | == | ~ | > | < | >= | <=]";
    private String if_KW = "if";
    private String do_WK = "do";
    private String while_KW = "while";
    private String else_KW = "else";
    private String val = var + "|"+ num;

    private String else_body;
    private String if_body;
    private String logical_expression = val + "\\("+logical_op+val+"\\)*";
    private String if_condition = "("+logical_expression+")";
    private String if_head = if_KW + if_condition;
    private String if_expr = if_head + if_body + "("+else_KW+else_body+")?";

    private String while_body;
    private String while_head = while_KW + "("+logical_expression+")";
    private String while_expr = while_head + while_body;


    private String assign_expr = var + assing_op + val + "("+op+val+")*";
    private String expr = assign_expr + "|"+ if_expr + "|" + while_expr;
    private String lang = expr + "+";
     */

    public Lexer(){

    }

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
                if (!lexeme.getTerminal().getName().equals("WS")){ lexemes.add(lexeme);}
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
