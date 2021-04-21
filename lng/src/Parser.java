import java.util.ArrayList;

public class Parser {

    private ArrayList<Lexeme> lexemes;

    private Node root;

    public Parser(){

    }

    public void createAST(ArrayList<Lexeme> lex) throws Exception {

        lexemes = new ArrayList<>(lex);
        root = lang();

    }

    private Node lang() throws Exception {

        Node node = new Node("lang");
        node.addChild(expr());

        while (lexemes.size() > 0 && currToken().matches("VAR|IF_KW|WHILE_KW|DLL_FUNC_KW|HASHMAP_FUNC_KW|COMMON_FUNC_KW")) {
            node.addChild(expr());
        }

        //if(!lexemes.isEmpty()){throw new Exception("Error in lang");}

        return node;

    }

    private Node expr() throws Exception {

        Node node = new Node("expr");

        switch (currToken()) {
            case ("VAR") -> node.addChild(assign_expr());
            case ("IF_KW") -> node.addChild(if_expr());
            case ("DLL_FUNC_KW"), ("HASHMAP_FUNC_KW"), ("COMMON_FUNC_KW") -> node.addChild(function_call());
            case ("WHILE_KW") -> node.addChild(while_expr());
            default -> throw new Exception("Error in expr");
        }

        return node;

    }

    private Node assign_expr() throws Exception {

        Node node = new Node("assign_expr");

        match("VAR", node);
        match("ASSIGN_OP", node);

        node.addChild(value_expr());

        return node;

    }

    private Node value_expr() throws Exception {

        Node node = new Node("value_expr");

        switch (currToken()) {

            case ("NUMBER"), ("VAR") -> node.addChild(value());

            case ("NEW_KW") -> node.addChild(assign_struct());

            case ("COMMON_FUNC_KW") -> node.addChild(function_call());

            case ("L_BR") -> {
                match("L_BR", node);
                node.addChild(value_expr());
                match("R_BR", node);
            }

            default -> throw new Exception("Error in value_expr");

        }

        while (currToken().matches("OP")) {

            match("OP", node);
            node.addChild(value_expr());

        }

        return node;

    }

    private Node value() throws Exception {

        Node node = new Node("value");

        switch (currToken()) {
            case ("NUMBER") -> match("NUMBER", node);
            case ("VAR") -> match("VAR", node);
            default -> throw new Exception("Error in value");
        }

        return node;

    }

    private Node assign_struct() throws Exception{

        Node node = new Node("assign_struct");

        match("NEW_KW", node);

        node.addChild(data_struct());

        return node;

    }

    private Node data_struct() throws Exception{

        Node node = new Node("data_struct");

        switch (currToken()) {
            case ("DLL_KW") -> match("DLL_KW", node);
            case ("HASHMAP_KW") -> match("HASHMAP_KW", node);
            default -> throw new Exception("Error in value");
        }

        return node;

    }

    private Node if_expr() throws Exception {

        Node node = new Node("if_expr");

        node.addChild(if_head());
        node.addChild(if_else_body());

        if (currToken().matches("ELSE_KW")) {
            node.addChild(else_expr());
        }

        return node;

    }

    private Node if_head() throws Exception {

        Node node = new Node("if_head");

        match("IF_KW", node);
        node.addChild(if_condition());

        return node;

    }

    private Node else_expr() throws Exception {

        Node node = new Node("else_expr");

        match("ELSE_KW", node);
        node.addChild(if_else_body());

        return node;

    }

    private Node if_condition() throws Exception {

        Node node = new Node("if_condition");

        match("L_BR", node);
        node.addChild(logical_expr());
        match("R_BR", node);

        return node;

    }

    private Node logical_expr() throws Exception {

        Node node = new Node("logical_expr");

        switch (currToken()){

            case ("NUMBER"), ("VAR") -> {

                node.addChild(value_expr());

                while (currToken().matches("LOGICAL_OP")) {
                    match("LOGICAL_OP", node);
                    node.addChild(value_expr());
                }

            }

            case ("COMMON_FUNC_KW") -> node.addChild(function_call());

        }


        return node;

    }

    private Node if_else_body() throws Exception{

        Node node = new Node("ifelse_body");

        match("L_S_BR", node);

        node.addChild(expr());

        while (currToken().matches("VAR|IF_KW|WHILE_KW|DLL_FUNC_KW|HASHMAP_FUNC_KW|COMMON_FUNC_KW")) node.addChild(expr());

        match("R_S_BR", node);

        return node;

    }

    private Node while_expr() throws Exception{

        Node node = new Node("while_expr");

        node.addChild(while_head());
        node.addChild(while_body());

        return node;

    }

    private Node while_head() throws Exception{

        Node node = new Node("while_head");

        match("WHILE_KW", node);
        node.addChild(while_condition());

        return node;

    }

    private Node while_condition() throws Exception{

        Node node = new Node("while_condition");

        match("L_BR", node);
        node.addChild(logical_expr());
        match("R_BR", node);

        return node;

    }

    private Node while_body() throws Exception{

        Node node = new Node("while_body");

        match("L_S_BR", node);

        node.addChild(expr());

        while (currToken().matches("VAR|IF_KW|WHILE_KW")) node.addChild(expr());

        match("R_S_BR", node);

        return node;

    }

    private Node function_call() throws Exception{

        Node node = new Node("function_call");

        switch (currToken()) {
            case ("DLL_FUNC_KW") -> match("DLL_FUNC_KW", node);
            case ("HASHMAP_FUNC_KW") -> match("HASHMAP_FUNC_KW", node);
            case ("COMMON_FUNC_KW") -> match("COMMON_FUNC_KW", node);
            default -> throw new Exception("Error in value");
        }

        node.addChild(arguments());

        return node;
    }

    private Node arguments() throws Exception{

        Node node = new Node("arguments");

        match("L_BR", node);

        while (!currToken().matches("R_BR")) {
            node.addChild(value());
            if(currToken().equals("CM"))dropComma();
        }

        match("R_BR", node);

        return node;

    }



    //Tools

    private String currToken() {

        if (!lexemes.isEmpty()){
            return lexemes.get(0).getTerminal().getName();
        }else {return "";}

    }

    private void dropComma(){

        lexemes.remove(0);

    }

    private void match(String terminal, Node currNode) {

        String t = currToken();

        assert (t.equals(terminal)) : "Current Token != " + terminal;

        currNode.addLexeme(lexemes.get(0));

        lexemes.remove(0);

    }

    public Node getRoot(){
        return root;
    }

    public void print(){
        System.out.println("\n"+"Abstract Syntax Tree"+"\n");
        print(root, 0);
    }

    private void print(Node root, int level){

        String tab = "";
        for (int i = 0; i< level; i++){tab += "    ";}

        System.out.println(tab+root.getName());


        for (Lexeme l: root.getLexemes()) {
            System.out.println(tab+l.getValue());
        }

        for (Node n: root.getChild()) {
            print(n, level+1);
        }

    }

}
