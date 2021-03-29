import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Terminal {

    private String name;
    private String pattern;
    private int priority;


    public Terminal(String n, String pat, int pr){

        name = n;
        pattern = pat;
        priority = pr;

    }

    public String getName(){
        return name;
    }

    public String getPattern(){
        return pattern;
    }

    public int getPriority(){
        return priority;
    }

}
