import java.util.*;

public class TuringMachine {

    private final HashMap<String, Object> map = new HashMap<>();

    private static final Map<String, Integer> operations;

    private static final Map<String, Integer> operandCount;

    private ArrayList<Lexeme> lexemes;

    static {

        operations = new HashMap<>();

        operations.put("=", 3);
        operations.put("<", 3);
        operations.put(">", 3);
        operations.put("==", 3);
        operations.put("!=", 3);

        operations.put("if", 3);
        operations.put("goto", 3);

        operations.put("addAfter", 2);
        operations.put("addBefore", 2);
        operations.put("put", 2);
        operations.put("contains", 2);
        operations.put("addFront", 2);
        operations.put("addEnd", 2);
        operations.put("remove", 2);
        operations.put("get", 2);
        operations.put("toEnd", 2);
        operations.put("toFront", 2);
        operations.put("backward", 2);
        operations.put("forward", 2);
        operations.put("new", 2);
        operations.put("size", 2);
        operations.put("clear", 2);
        operations.put("print", 2);
        operations.put("isEmpty", 2);

        operations.put("*", 2);
        operations.put("/", 2);
        operations.put("+", 1);
        operations.put("-", 1);

    }

    static {

        operandCount = new HashMap<>();

        operandCount.put("addAfter", 3);
        operandCount.put("addBefore", 3);
        operandCount.put("put", 3);

        operandCount.put("*", 2);
        operandCount.put("/", 2);
        operandCount.put("+", 2);
        operandCount.put("-", 2);
        operandCount.put("<", 2);
        operandCount.put(">", 2);
        operandCount.put("==", 2);
        operandCount.put("!=", 2);
        operandCount.put("=", 2);
        operandCount.put("contains", 2);
        operandCount.put("addFront", 2);
        operandCount.put("addEnd", 2);
        operandCount.put("remove", 2);
        operandCount.put("get", 2);
        operandCount.put("if", 2);

        operandCount.put("toEnd", 1);
        operandCount.put("toFront", 1);
        operandCount.put("backward", 1);
        operandCount.put("forward", 1);
        operandCount.put("new", 1);
        operandCount.put("goto", 1);
        operandCount.put("size", 1);
        operandCount.put("clear", 1);
        operandCount.put("print", 1);
        operandCount.put("isEmpty", 1);

    }

    private  List<Terminal> TERMINALS = List.of(

            new Terminal("DLL_FUNC_KW", "(addFront)|(addEnd)|(addBefore)|(addAfter)" +
                    "|(forward)|(backward)|(toFront)|(toEnd)",1),

            new Terminal("HASHMAP_FUNC_KW", "(put)|(containsKey)",1),

            new Terminal("COMMON_FUNC_KW", "(get)|(remove)|(size)|(clear)|(isEmpty)|(print)",1)

    );

    public TuringMachine(ArrayList<Lexeme> lex, Node root){

        lexemes = new ArrayList<>(lex);

        ArrayList<String> buffer = new ArrayList();

        for (Node n: root.getChild()) {
            buffer.addAll(calculateRPN(n));
        }

        String rpn = "";

        for (String s: buffer) {
            rpn += s + " ";
        }

        System.out.println("RPN: "+rpn);

        calculateExpression(buffer);

    }

    private ArrayList<String> calculateRPN(Node exp){

        ArrayList<String> buff = new ArrayList<>();

        ArrayList<Lexeme> buffLex = new ArrayList<>();

        switch (exp.getChild().get(0).getName()) {

            case ("assign_expr") -> {

                if(getDefinedNode(exp.getChild().get(0), "value_expr").getChild().get(0).getName() == "function_call"){

                    buff.add(exp.getChild().get(0).getLexemes().get(0).getValue());

                    buff.addAll(calculateRPN(getDefinedNode(exp.getChild().get(0), "value_expr")));

                    buff.add(exp.getChild().get(0).getLexemes().get(1).getValue());

                   // System.out.println("assign "+buff.toString());

                   return buff;

                }else {

                    for (Lexeme l : exp.getChild().get(0).getLexemes()) {
                        buffLex.add(l);
                    }

                    buffLex.addAll(restoreAssign(getDefinedNode(exp.getChild().get(0), "value_expr")));

                    for (Lexeme l : buffLex) {
                        //System.out.println(l.getValue());
                    }

                    return sortingStation(buffLex);

                }

            }

            case ("if_expr") -> {

                String _else = createPointer();
                String _endIf = createPointer();

                buffLex = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

                //for (Lexeme l: buffLex) {
                 //   System.out.println(l.getValue());
               // }

                //System.out.println("buffLex restored "+buffLex.toString());

                buff.addAll(sortingStation(buffLex));

               // System.out.println("buff sorted "+buff.toString());

                buff.add(_else);
                buff.add("if");

                for (Node n : exp.getChild().get(0).getChild().get(1).getChild()) {
                    buff.addAll(calculateRPN(n));
                }

                buff.add(_endIf);
                buff.add("goto");
                buff.add(_else);


                //System.out.println("false branch");
                if (exp.getChild().get(0).getChild().size() > 2) {
                    for (Node n : exp.getChild().get(0).getChild().get(2).getChild().get(0).getChild()) {
                        buff.addAll(calculateRPN(n));
                    }
                }

                buff.add(_endIf);

            }

            case ("while_expr") -> {

                String start = createPointer();
                String end = createPointer();

                buff.add(start);

                buffLex = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

                //System.out.println(buff);

                buff.addAll(sortingStation(buffLex));

                buff.add(end);
                buff.add("if");

                for (Node n : exp.getChild().get(0).getChild().get(1).getChild()) {
                    buff.addAll(calculateRPN(n));
                }

                buff.add(start);
                buff.add("goto");
                buff.add(end);

            }

            case ("function_call") -> {

                buff.add(exp.getChild().get(0).getLexemes().get(0).getValue());

                for (Node node : getDefinedNode(exp.getChild().get(0), "arguments").getChild()) {
                    buff.add(0,node.getLexemes().get(0).getValue());
                }

               // System.out.println(buff.toString());

                return buff;

            }

        }

        //System.out.println(buff.toString());

        return buff;
    }

    private ArrayList<Lexeme> restoreAssign(Node root){

        ArrayList<Lexeme> buff = new ArrayList<>();

        if(root.getLexemes().size()>=2){

            //System.out.println(root.getChild().size());

            buff.add(root.getLexemes().get(0));

            for (Node n: root.getChild()) {

                buff.addAll(restoreAssign(n));

                if(!n.getLexemes().isEmpty()){

                    buff.add(root.getLexemes().get(1));

                    if(root.getLexemes().size()>2){
                        buff.add(root.getLexemes().get(2));
                    }

                }

            }


        }else {

            for (Lexeme l : root.getLexemes()) {
                buff.add(l);
            }

            for (int i = 0; i < root.getChild().size(); i++) {

                if (i == 1) {
                    buff.addAll(0,restoreAssign(root.getReversedChild().get(i)));
                } else {
                    buff.addAll(restoreAssign(root.getReversedChild().get(i)));
                }

            }

        }

        return buff;

    }

    private ArrayList<Lexeme> restoreCondition(Node root){

        ArrayList<Lexeme> buff = new ArrayList<>();

        if(root.getChild().size() == 2){

            //buff += " " + root.getLexemes().get(0).getValue();
           // buff = restoreCondition(root.getChild().get(0)) + " " + buff + " " + restoreCondition(root.getChild().get(1));

            buff.add(root.getLexemes().get(0));
            buff.addAll(0,restoreCondition(root.getChild().get(0)));
            buff.addAll(restoreCondition(root.getChild().get(1)));

        }else {

            if(root.getLexemes().size()>=1)buff.add(root.getLexemes().get(0));

            //for (Lexeme l: root.getLexemes()) {
            //    buff.add(l);
           // }

            for (Node n: root.getChild()) {
                buff.addAll(restoreCondition(n));
            }

            if(root.getLexemes().size()==2)buff.add(root.getLexemes().get(1));

        }

        return buff;

    }

    private Node getDefinedNode(Node root, String nodeName){

        if(root.getChild().get(0).getName().equals(nodeName)){
            return root.getChild().get(0);
        }else{
            return getDefinedNode(root.getChild().get(0), nodeName);
        }

    }

    private ArrayList<String> sortingStation(ArrayList<Lexeme> lex) {

        if (lex == null || lex.size() == 0)
            throw new IllegalStateException("Lexemes isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");

        String leftBracket = "(";
        String rightBracket = ")";

        ArrayList<String> out = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        Set<String> operationSymbols = new HashSet<>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        for(int i =0; i < lex.size(); i++){

            String terminal = lex.get(i).getTerminal().getName();

            //System.out.println(terminal);
           // System.out.println(stack.toString());

            switch (terminal){

                case ("NUMBER"), ("VAR"), ("DLL_KW"), ("HASHMAP_KW") -> out.add(lex.get(i).getValue());

                case ("DLL_FUNC_KW"), ("HASHMAP_FUNC_KW"), ("COMMON_FUNC_KW") -> stack.push(lex.get(i).getValue());

                case ("CM") -> {

                    while(!stack.isEmpty() && stack.peek() != leftBracket){
                        out.add(stack.pop());
                    }

                }

                case ("ASSIGN_OP"), ("LOGICAL_OP"), ("OP"), ("NEW_KW") -> {

                    if(!stack.isEmpty() && operations.containsKey(stack.peek()) &&
                            operations.get(stack.peek()) <= operations.get(lex.get(i).getValue())
                    ){
                        out.add(stack.pop());
                    }

                    stack.push(lex.get(i).getValue());

                }

                case ("L_BR") -> stack.push(lex.get(i).getValue());

                case ("R_BR") -> {

                    while(!stack.isEmpty() && !stack.peek().equals(leftBracket)){

                       // System.out.println(stack.peek());

                        out.add(stack.pop());

                    }
                    stack.pop();

                    if(!stack.isEmpty() && isFunction(stack.peek())){
                        out.add(stack.pop());
                    }

                }

            }

        }

        while(!stack.isEmpty()){
            out.add(stack.pop());
        }

        return out;

    }


    private void calculateExpression(ArrayList<String> tokens) {

        System.out.println("\n\n\n"+"TuringMachine");

        Stack<Object> stack = new Stack<>();
        ListIterator<String> iter = tokens.listIterator();

        while (iter.hasNext()){

            String token = iter.next();
           // System.out.println(token);

            // Операнд.
            if (!operations.containsKey(token)) {

                stack.push(token);

            } else {

                System.out.println(stack.toString());
                System.out.println("oper "+ token);

                Object op1 = null;
                Object op2 = null;
                Object op3 = null;

                if(!stack.isEmpty()){
                    op1 = stack.pop();
                }

                if(operandCount.get(token) >= 2){
                    op2 = stack.empty() ? "0" : stack.pop();
                }

                if(operandCount.get(token) == 3){
                    op3 = stack.empty() ? "0" : stack.pop();
                }

                System.out.println(op1 + " " + op2 + " " + op3);

                switch (token) {

                    case ("if") -> {

                        if(op2 == "false"){
                            while(!iter.next().equals(op1)){}
                        }

                    }

                    case ("goto") -> {

                        int pointerIndex = tokens.indexOf(op1);

                        if(pointerIndex == iter.nextIndex() - 2){

                            pointerIndex = tokens.lastIndexOf(op1);

                        }

                        System.out.println("pIndex " +pointerIndex + " curr index " + iter.nextIndex() +"-1");

                        if(iter.previousIndex() < pointerIndex){

                            System.out.println("fwrd");
                            while(!iter.next().equals(op1)){ }

                        }else{

                            System.out.println("back");

                            while(iter.previousIndex() > pointerIndex){
                                System.out.println("back2");
                                iter.previous();
                            }

                        }

                    }

                    case ("*") -> stack.push(multiply(op2, op1));
                    case ("/") -> stack.push(divide(op2, op1));
                    case ("+") -> stack.push(add(op2, op1));
                    case ("-") -> stack.push(sub(op2, op1));

                    case (">") -> stack.push(Greater(op2, op1));
                    case ("<") -> stack.push(Less(op2, op1));
                    case ("==") -> stack.push(Equals(op2, op1));
                    case ("!=") -> stack.push(NotEquals(op2, op1));
                    case ("contains") -> stack.push(containsKey(op2, op1));
                    //push op1?
                    case ("isEmpty") -> stack.push(isEmpty(op1));


                    case ("=") -> mapVar(op2, op1);
                    case ("new") -> stack.push(NewDataStruct(op1));

                    case ("addFront") -> addFront(op1, op2);
                    case ("addEnd") -> addEnd(op1, op2);

                    case ("addAfter") -> addAfter(op1, op2, op3);
                    case ("addBefore") -> addBefore(op1, op2, op3);

                    case ("forward") -> forward(op1);
                    case ("backward") -> backward(op1);

                    case ("toFront") -> toFront(op1);
                    case ("toEnd") -> toEnd(op1);

                    case ("size") -> stack.push(size(op1));
                    case ("remove") -> remove(op1, op2);
                    case ("clear") -> clear(op1);
                    case ("print") -> print(op1);

                    case ("put") -> put(op1, op2, op3);
                    case ("get") -> stack.push(get(op1, op2));


                    default -> System.out.println("shuinting error");

                }
            }
        }


        System.out.println(map.toString());
        System.out.println("\n");

    }



    //Math operations

    private Object add(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Integer.toString(a + b);

    }

    private Object sub(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Integer.toString(a - b);

    }

    private Object multiply(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Integer.toString(a * b);

    }

    private Object divide(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Integer.toString(a / b);

    }



    //Logical operations

    private String Greater(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Boolean.toString(a > b);

    }

    private String Less(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt(map.getOrDefault(op1, op1).toString());

        b = Integer.parseInt(map.getOrDefault(op2, op2).toString());

        return Boolean.toString(a < b);

    }

    private String Equals(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Boolean.toString(a == b);

    }

    private String NotEquals(Object op1, Object op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return Boolean.toString(a != b);

    }



    //Func operations

    //common
    private Object NewDataStruct(Object struct){

        //System.out.println("lolxd "+struct);

        if(struct.equals("DoubleLinkedList")){
            return new DoubleLinkedList<Integer>();
        }else if(struct.equals("HashMap")){
            return new CustomHashMap<String,String>();
        }else {
            return null;
        }

    }


    private Object get(Object op1, Object key){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            int buff = ((DoubleLinkedList<Integer>)obj).getByIndex(Integer.parseInt((String)key));
            return Integer.toString(buff);
        }else if(obj instanceof CustomHashMap){
            return ((CustomHashMap)obj).get(key);
        } else{
            System.out.println("error");
            return null;
        }

    }

    private void remove(Object op1, Object op2){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList)obj).removeByIndex(Integer.parseInt((String)op2));
        }else if(obj instanceof CustomHashMap){
            ((CustomHashMap)obj).remove(op2);
        }else{
            System.out.println("error");
        }

    }

    private String size(Object op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            int buff = ((DoubleLinkedList<Integer>)obj).size();
            return Integer.toString(buff);
        }else{
            System.out.println("error");
            return null;
        }

    }

    private boolean isEmpty(Object op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            return ((DoubleLinkedList)obj).isEmpty();
        }else if(obj instanceof CustomHashMap) {
            return ((CustomHashMap)obj).isEmpty();
        }else{
            System.out.println("error during empty check");
            return false;
        }

    }

    private void clear(Object op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList)obj).clear();
        }else if(obj instanceof CustomHashMap){
            ((CustomHashMap)obj).clear();
        }
        else{
            System.out.println("error");
        }

    }

    private void print(Object op1){

        //System.out.println("print");

        Object obj = map.get(op1);

        //System.out.println(((DoubleLinkedList)obj).toString());

        if(obj instanceof DoubleLinkedList){
            System.out.println("DLL "+ obj.toString());
        }else if(obj instanceof CustomHashMap){
            System.out.println("Hashmap "+obj.toString());
        }

    }

    //dll

    private void addFront(Object op1, Object key){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        String value = (String) map.getOrDefault(key, key);

        if(obj != null){
            obj.addFront(Integer.parseInt(value));
        }else{
            System.out.println("error during addFront");
        }

    }

    private void addEnd(Object op1, Object key){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        String value = (String) map.getOrDefault(key, key);

        if(obj != null){
            obj.addEnd(Integer.parseInt(value));
        }else{
            System.out.println("error");
        }

    }

    private void forward(Object op1){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        if(obj != null){
            obj.next();
        }else{
            System.out.println("error");
        }

    }

    private void backward(Object op1){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        if(obj != null){
            obj.prev();
        }else{
            System.out.println("error");
        }

    }

    private void toFront(Object op1){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        if(obj != null){
            obj.toFront();
        }else{
            System.out.println("error");
        }

    }

    private void toEnd(Object op1){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        if(obj != null){
            obj.toEnd();
        }else{
            System.out.println("error");
        }

    }

    private void addBefore(Object op1, Object op2, Object op3){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        String value1 = (String) map.getOrDefault(op2, op2);
        String value2 = (String) map.getOrDefault(op3, op3);

        if(obj != null){
            obj.addBefore(Integer.parseInt(value1),Integer.parseInt(value2));
        }else{
            System.out.println("error");
        }

    }

    private void addAfter(Object op1, Object op2, Object op3){

        DoubleLinkedList obj = (DoubleLinkedList) map.get(op1);

        String value1 = (String) map.getOrDefault(op2, op2);
        String value2 = (String) map.getOrDefault(op3, op3);

        if(obj != null){
            obj.addAfter(Integer.parseInt(value1),Integer.parseInt(value2));
        }else{
            System.out.println("error");
        }

    }


    //hashmap

    private void put(Object op1, Object op2, Object op3){
        ((CustomHashMap)map.get(op1)).put(op2,op3);
    }

    private boolean containsKey(Object op1, Object key){
        return ((CustomHashMap)map.get(op1)).containsKey(key);
    }



    //Other operations

    private void mapVar(Object var, Object value){

        if(map.containsKey((String) var)){
            map.remove(var);
        }
        map.put((String) var,value);

    }


    //Tools

    public boolean isFunction(String expr){

        for (Terminal terminal : TERMINALS) {

            if (terminal.matches(expr)) {
                return true;
            }
        }
        return false;
    }

    int index = 0;
    public String createPointer(){
        index++;
        return "p"+index;
    }

}
