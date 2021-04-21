
public class Main {

    public static void main(String[] args) throws Exception {

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        ShuntingYard shuntingYard = new ShuntingYard();

        if(args.length==0) {
            lexer.readFile("./lng/src/dll_test.txt");
        }else {
            lexer.readFile(args[0]);
        }
        lexer.print();

        parser.createAST(lexer.getLexemes());
        parser.print();

        System.out.println("\n\n"+"Restore expression"+"\n");

       // shuntingYard.calculateExpression("1+((((1+3)*2)+(2+4))/2)-3");
        //shuntingYard.constructRPN(parser.getRoot());
        //shuntingYard.constructExpression(parser.getRoot());

        TuringMachine turingMachine = new TuringMachine(lexer.getLexemes(), parser.getRoot());

    }
}
