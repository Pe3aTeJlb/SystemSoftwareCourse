import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {

    Program p = this;
    
    private String file;
    private StringTokenizer tokenizer;

    private String var = "^[a-zA-Z_]{1}[a-zA-Z_0-9]{0,}$";
    private String op = "\\+ \\| \\- \\| \\* \\| \\/";
    private String num = "[0|[1-9][0-9]*]";
    private String assing_op = "=";
    private String logical_op = "&& | \\|| | % | == | ~ | > | < | >= | <=";
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


    public Program(String filePath){
        if_body = "\\{"+expr+"\\}";
        while_body = "\\{"+expr+"\\}";
        else_body ="\\{"+expr+"\\}";

        if_expr = if_head + if_body + "\\("+else_KW+else_body+"\\)?";
        while_expr = while_head + while_body;

        System.out.println(if_expr);
    }

    public void start(){

        try {
            Load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Tokenize();

        System.out.println(file);
        Pattern pattern = Pattern.compile(num);
        Matcher matcher = pattern.matcher("123123");

        System.out.println(file.substring(matcher.start(), matcher.end()));

    }

    private void Load() throws IOException {

        StringBuffer fileData = new StringBuffer();

        BufferedReader reader = new BufferedReader(
                new FileReader("D://p.txt"));

        char[] buf = new char[1024];
        int numRead=0;

        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();

        file = fileData.toString();

    }

    private void Tokenize(){

        tokenizer = new StringTokenizer(file);

        while (tokenizer.hasMoreTokens()){

            System.out.println(tokenizer.nextToken());

        }

    }

}
