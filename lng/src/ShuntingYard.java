import java.util.Stack;
import java.util.*;

public class ShuntingYard {

    private final HashMap<String, Object> map = new HashMap<>();

    private static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {

        MAIN_MATH_OPERATIONS = new HashMap<>();

        MAIN_MATH_OPERATIONS.put("=", 3);
        MAIN_MATH_OPERATIONS.put("<", 3);
        MAIN_MATH_OPERATIONS.put(">", 3);
        MAIN_MATH_OPERATIONS.put("==", 3);
        MAIN_MATH_OPERATIONS.put("!=", 3);

        MAIN_MATH_OPERATIONS.put("new", 2);

        MAIN_MATH_OPERATIONS.put("addFront", 2);
        MAIN_MATH_OPERATIONS.put("addEnd", 2);
        MAIN_MATH_OPERATIONS.put("addAfter", 2);
        MAIN_MATH_OPERATIONS.put("addBefore", 2);
        MAIN_MATH_OPERATIONS.put("forward", 2);
        MAIN_MATH_OPERATIONS.put("backward", 2);
        MAIN_MATH_OPERATIONS.put("toFront", 2);
        MAIN_MATH_OPERATIONS.put("toEnd", 2);

        MAIN_MATH_OPERATIONS.put("put", 2);
        MAIN_MATH_OPERATIONS.put("get", 2);
        MAIN_MATH_OPERATIONS.put("contains", 2);

        MAIN_MATH_OPERATIONS.put("remove", 2);

        MAIN_MATH_OPERATIONS.put("isEmpty", 2);

        MAIN_MATH_OPERATIONS.put("size", 2);

        MAIN_MATH_OPERATIONS.put("print", 2);



        MAIN_MATH_OPERATIONS.put("+", 2);
        MAIN_MATH_OPERATIONS.put("-", 2);
        MAIN_MATH_OPERATIONS.put("*", 1);
        MAIN_MATH_OPERATIONS.put("/", 1);

    }


    public void constructExpression(Node root){

        //upper expression layer
        for (Node n: root.getChild()) {
            checkType(n);
        }

        //System.out.println("\n\n"+"final RPN"+"\n");
        //constructRPN(root);

    }

    public void constructRPN(Node root){

        String buff = "";

        //upper expression layer
        for (Node n: root.getChild()) {
            buff += checkTypeButDontCalculate(n) + "    ";
        }
        System.out.println(buff);

    }

    private void checkType(Node exp){

        switch (exp.getChild().get(0).getName()) {

            case "assign_expr" -> {

                String buff = "";

                if(getDefinedNode(exp.getChild().get(0), "value_expr").getChild().get(0).getName() == "function_call"){

                    buff += exp.getChild().get(0).getLexemes().get(0).getValue();

                    buff += checkTypeButDontCalculate(getDefinedNode(exp.getChild().get(0), "value_expr"));

                    buff += " "+exp.getChild().get(0).getLexemes().get(1).getValue();
                    System.out.println(buff);

                    calculateFunction(buff);

                }else {

                    for (Lexeme l : exp.getChild().get(0).getLexemes()) {
                        buff += l.getValue();
                    }

                    buff += restoreAssign(getDefinedNode(exp.getChild().get(0), "value_expr"));
                    System.out.println(buff);

                    calculateExpression(buff);
                }

            }

            case "if_expr" -> {

                String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "function_call"));

                System.out.println(buff);

                boolean trueBranch = calculateCondition(buff);

                if (trueBranch) {

                    System.out.println("true branch");

                    for (Node n : exp.getChild().get(0).getChild().get(1).getChild()) {
                        checkType(n);
                    }

                } else {

                    System.out.println("false branch");
                    if (exp.getChild().get(0).getChild().size() > 2) {
                        for (Node n : exp.getChild().get(0).getChild().get(2).getChild()) {
                            System.out.println(n.getChild().get(0).getName());
                            checkType(n.getChild().get(0));
                        }
                    }

                }

            }

            case "while_expr" -> {

                String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

                System.out.println(buff);

                boolean trueBranch = calculateCondition(buff);

                if (trueBranch) {

                    System.out.println("true branch");

                    for (Node n : exp.getChild().get(0).getChild().get(1).getChild()) {
                        checkType(n);
                    }

                    checkType(exp);

                } else {

                    System.out.println("false branch");

                }

            }

            case "function_call" -> {

                String buff = " " + exp.getChild().get(0).getLexemes().get(0).getValue();

                for (Node node : getDefinedNode(exp.getChild().get(0), "arguments").getChild()) {
                    buff = " " + node.getLexemes().get(0).getValue() + buff;
                }

                System.out.println(buff);

                calculateFunction(buff);

            }

        }

    }

    private String checkTypeButDontCalculate(Node exp){

        String globalBuffer = "";

        switch (exp.getChild().get(0).getName()) {
            case ("assign_expr") -> {

                String buff = "";

                for (Lexeme l : exp.getChild().get(0).getLexemes()) {
                    buff += l.getValue();
                }

                buff += restoreAssign(getDefinedNode(exp.getChild().get(0), "value_expr"));
                System.out.println(buff);

                return justReturnExpression(buff);

            }
            case ("if_expr") -> {

                String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

                System.out.println(buff);

                globalBuffer += justReturnExpression(buff);


                for (Node n : exp.getChild().get(0).getChild().get(1).getChild()) {
                    globalBuffer += "    " + checkTypeButDontCalculate(n);
                }

                System.out.println("false branch");
                if (exp.getChild().get(0).getChild().size() > 2) {
                    for (Node n : exp.getChild().get(0).getChild().get(2).getChild().get(0).getChild()) {
                        globalBuffer += "    " + checkTypeButDontCalculate(n);
                    }
                }


                break;
            }
            case ("while_expr") -> {

                String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

                System.out.println(buff);

                globalBuffer += justReturnExpression(buff);

                for (Node n : exp.getChild().get(0).getChild().get(1).getChild()) {
                    globalBuffer += "    " + checkTypeButDontCalculate(n);
                }

                break;
            }
            case ("function_call") -> {

                String buff = " " + exp.getChild().get(0).getLexemes().get(0).getValue();


                for (Node node : getDefinedNode(exp.getChild().get(0), "arguments").getChild()) {
                    buff = " " + node.getLexemes().get(0).getValue() + buff;
                }

                System.out.println(buff);

                return buff;

            }
        }

        return globalBuffer;

    }




    private Node getDefinedNode(Node root, String nodeName){

            if(root.getChild().get(0).getName().equals(nodeName)){
                return root.getChild().get(0);
            }else{
                return getDefinedNode(root.getChild().get(0), nodeName);
            }

    }

    private String restoreAssign(Node root){

        String buff = "";

        if(root.getLexemes().size()>=2){

            System.out.println(root.getChild().size());

            buff += root.getLexemes().get(0).getValue();

            for (Node n: root.getChild()) {

                buff += restoreAssign(n);

                if(!n.getLexemes().isEmpty()){

                    buff += root.getLexemes().get(1).getValue();

                    if(root.getLexemes().size()>2){
                        buff += root.getLexemes().get(2).getValue();
                    }

                }

            }


        }else {

            for (Lexeme l : root.getLexemes()) {
                //System.out.println(l.getValue());
                buff += " " + l.getValue();
            }

            for (int i = 0; i < root.getChild().size(); i++) {

                if (i == 1) {
                    buff = restoreAssign(root.getReversedChild().get(i)) + " " + buff;
                } else {
                    buff += " " + restoreAssign(root.getReversedChild().get(i));
                }

            }

        }

        return buff;

    }

    private String restoreCondition(Node root){

        String buff = "";

        if(root.getChild().size() == 2){
            buff += " " + root.getLexemes().get(0).getValue();
            buff = restoreCondition(root.getChild().get(0)) + " " + buff + " " + restoreCondition(root.getChild().get(1));

        }else {

            for (Lexeme l: root.getLexemes()) {
                //System.out.println(l.getValue());
                buff += " " + l.getValue();
            }

            for (Node n: root.getChild()) {
                buff += restoreCondition(n);
            }

        }

        return buff;

    }



    //Variant old

    public void calculateExpression(String exp) {

        String rpn = sortingStation(exp, MAIN_MATH_OPERATIONS);

        System.out.println("RPN: "+rpn);

        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");

        Stack<Object> stack = new Stack<>();

        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();

            // Операнд.
            if (!MAIN_MATH_OPERATIONS.containsKey(token)) {

                stack.push(token);

            } else {

                Object op2 = stack.pop();
                Object op1 = stack.empty() ? "0" : stack.pop();

                //System.out.println(op1 + " " + op2);

                switch (token){

                    case ("*") -> stack.push(multiply(op1,op2));
                    case ("/") -> stack.push(divide(op1,op2));
                    case ("+") -> stack.push(add(op1,op2));
                    case ("-") -> stack.push(sub(op1,op2));

                    case ("=") -> mapVar(op1, op2);
                    case ("new") -> {stack.push(op1); stack.push(NewDataStruct(op2));}

                    case ("size") -> {stack.push(op1); stack.push(size(op2));}


                    case ("get") -> stack.push(get(op1,op2));

                    default -> System.out.println("shuinting error");
                }

            }

        }

        System.out.println(map.toString());
        System.out.println("\n");

    }

    public void calculateFunction(String rpn){

        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");

        Stack<Object> stack = new Stack<>();

        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();

            // Операнд.
            if (!MAIN_MATH_OPERATIONS.containsKey(token)) {

                //System.out.println(token);
                stack.push(token);

            } else {

                System.out.println(token);

                Object op1 = stack.pop();
                Object op2 = stack.empty() ? "0" : stack.pop();
                Object op3 = stack.empty() ? "0" : stack.pop();

                System.out.println(op1+" "+ op2 + " " + op3);

                switch (token){

                    case ("=") -> {mapVar(op1, op2);}

                    case ("addFront") -> addFront(op1, op2);
                    case ("addEnd") -> addEnd(op1, op2);

                    case ("addAfter") -> addAfter(op1, op2, op3);
                    case ("addBefore") -> addBefore(op1, op2, op3 );

                    case ("forward") -> forward(op1);
                    case ("backward") -> backward(op1);

                    case ("toFront") -> toFront(op1);
                    case ("toEnd") -> toEnd(op1);


                    case ("remove") -> remove(op1, op2);
                    case ("clear") -> clear(op1);
                    case ("print") -> print(op1);

                    case ("put") -> put(op1, op2, op3);
                    case ("get") -> {stack.push(get(op1, op2));stack.push(op3);}

                    default -> System.out.println("shunting error");
                }

            }

        }

        System.out.println(map.toString());
        System.out.println("\n");

    }

    public boolean calculateCondition(String exp){

        String rpn = sortingStation(exp, MAIN_MATH_OPERATIONS);

        System.out.println("RPN: "+rpn);

        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");

        Stack<String> stack = new Stack<>();

        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();
            // Операнд.
            if (!MAIN_MATH_OPERATIONS.containsKey(token)) {
                stack.push(token);
            } else {

                String op2 = stack.pop();
                String op1 = stack.empty() ? "0" : stack.pop();

                System.out.println(op1 + " "+ op2);

                switch (token){

                    case(">") -> {return Greater(op1, op2);}
                    case ("<") -> {return Less(op1, op2); }
                    case("==") -> {return Equals(op1, op2); }
                    case("!=") -> {return NotEquals(op1, op2);}

                    case("contains") -> {return containsKey(op1, op2);}
                    case("isEmpty") -> {return isEmpty(op2);}

                    default -> System.out.println("");

                }

            }

        }

        return false;

    }

    public String justReturnExpression(String exp){

        return sortingStation(exp, MAIN_MATH_OPERATIONS);

    }



    private String sortingStation(String expression, Map<String, Integer> operations) {

        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");

        String leftBracket = "(";
        String rightBracket = ")";
        List<String> out = new ArrayList<>();

        Stack<String> stack = new Stack<>();

        expression = expression.replace(" ", "");

        Set<String> operationSymbols = new HashSet<>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        int index = 0;
        boolean findNext = true;

        while (findNext) {

            int nextOperationIndex = expression.length();
            String nextOperation = "";

            for (String operation : operationSymbols) {

                int i = expression.indexOf(operation, index);

                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }

            }

            if (nextOperationIndex == expression.length()) {

                findNext = false;

            } else {

                // Если оператору или скобке предшествует операнд, добавляем его в выходную строку.
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }

                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                }
                else if (nextOperation.equals(rightBracket)) {

                    while (!stack.peek().equals(leftBracket)) {

                        out.add(stack.pop());

                        if (stack.empty()) {
                            throw new IllegalArgumentException("Unmatched brackets");
                        }

                    }

                    stack.pop();

                }
                else {

                    while (!stack.empty() && !stack.peek().equals(leftBracket) &&
                            (operations.get(nextOperation) >= operations.get(stack.peek()))) {

                        out.add(stack.pop());

                    }

                    stack.push(nextOperation);

                }


                index = nextOperationIndex + nextOperation.length();

            }
        }

        if (index != expression.length()) {
            out.add(expression.substring(index));
        }

        while (!stack.empty()) {
            out.add(stack.pop());
        }

        StringBuffer result = new StringBuffer();
        if (!out.isEmpty())
            result.append(out.remove(0));
        while (!out.isEmpty())
            result.append(" ").append(out.remove(0));

        return result.toString();

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

    private boolean Greater(String op1, String op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return a > b;

    }

    private boolean Less(String op1, String op2){

        int a, b;
        System.out.println((String) map.getOrDefault(op1, op1));
        a = Integer.parseInt(map.getOrDefault(op1, op1).toString());

        b = Integer.parseInt(map.getOrDefault(op2, op2).toString());

        return a < b;

    }

    private boolean Equals(String op1, String op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return a == b;

    }

    private boolean NotEquals(String op1, String op2){

        int a, b;

        a = Integer.parseInt((String) map.getOrDefault(op1, op1));

        b = Integer.parseInt((String) map.getOrDefault(op2, op2));

        return a != b;

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

    private boolean containsKey(Object op1, String key){
        return ((CustomHashMap)map.get(op1)).containsKey(key);
    }



    //Other operations

    private void mapVar(Object var, Object value){

        if(map.containsKey((String) var)){
            map.remove(var);
        }
        map.put((String) var,value);

    }

}