
public class Main {

    public static void main(String[] args) throws Exception {

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        ShuntingYard shuntingYard = new ShuntingYard();


        if(args.length==0) {
            lexer.readFile("D://p.txt");
        }else {
            lexer.readFile(args[0]);
        }
        lexer.print();

        parser.createAST(lexer.getLexemes());
        parser.print();

        System.out.println("\n\n"+"Restore expression"+"\n");

        shuntingYard.constructExpression(parser.getRoot(), "");

    }
}
