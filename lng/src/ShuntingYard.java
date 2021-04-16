import java.util.Stack;
import java.util.*;

public class ShuntingYard {

    private HashMap<String, Object> map = new HashMap<>();

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

    }

    public void constructRPN(Node root){

        String ass = "";

        //upper expression layer
        for (Node n: root.getChild()) {
            ass += checkTypeButDontCalculate(n) + "    ";
        }
        System.out.println(ass);

    }

    private void checkType(Node exp){

        if(exp.getChild().get(0).getName().equals("assign_expr")){

            String buff = "";

            for (Lexeme l: exp.getChild().get(0).getLexemes()) {
                buff += l.getValue();
            }

            buff += restoreAssign(getDefinedNode(exp.getChild().get(0), "value_expr"));
            System.out.println(buff);

            calculateExpression(buff);

        }else if(exp.getChild().get(0).getName().equals("if_expr")){

            String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

            System.out.println(buff);

            boolean trueBranch = calculateCondition(buff);

            if(trueBranch){

                System.out.println("true branch");

                for (Node n: exp.getChild().get(0).getChild().get(1).getChild()) {
                    checkType(n);
                }

            }else {

                System.out.println("false branch");
                if(exp.getChild().get(0).getChild().size() > 2){
                    for (Node n : exp.getChild().get(0).getChild().get(2).getChild()) {
                        checkType(n);
                    }
                }

            }

        }else if(exp.getChild().get(0).getName().equals("while_expr")){

            String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

            System.out.println(buff);

            boolean trueBranch = calculateCondition(buff);

            if(trueBranch){

                System.out.println("true branch");

                for (Node n: exp.getChild().get(0).getChild().get(1).getChild()) {
                    checkType(n);
                }

                checkType(exp);

            }else {

                System.out.println("false branch");

                return;

            }

        }else if(exp.getChild().get(0).getName().equals("function_call")){

            String buff = " "+exp.getChild().get(0).getLexemes().get(0).getValue();


            for (Node node: getDefinedNode(exp.getChild().get(0), "arguments").getChild()) {
                buff = " "+  node.getLexemes().get(0).getValue() + buff;
            }

            System.out.println(buff);

            calculateFunction(buff);

        }

    }

    private String checkTypeButDontCalculate(Node exp){

        String globalBuffer = "";

        if(exp.getChild().get(0).getName().equals("assign_expr")){

            String buff = "";

            for (Lexeme l: exp.getChild().get(0).getLexemes()) {
                buff += l.getValue();
            }

            buff += restoreAssign(getDefinedNode(exp.getChild().get(0), "value_expr"));
            System.out.println(buff);

            return justReturnExpression(buff);

        }else if(exp.getChild().get(0).getName().equals("if_expr")){

            String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

            System.out.println(buff);

            globalBuffer += justReturnExpression(buff);


            for (Node n: exp.getChild().get(0).getChild().get(1).getChild()) {
                globalBuffer += "    " + checkTypeButDontCalculate(n);
            }

            System.out.println("false branch");
            if(exp.getChild().get(0).getChild().size() > 2){
                for (Node n : exp.getChild().get(0).getChild().get(2).getChild().get(0).getChild()) {
                    globalBuffer += "    " + checkTypeButDontCalculate(n);
                }
            }



        }else if(exp.getChild().get(0).getName().equals("while_expr")){

            String buff = restoreCondition(getDefinedNode(exp.getChild().get(0), "logical_expr"));

            System.out.println(buff);

            globalBuffer += justReturnExpression(buff);

            for (Node n: exp.getChild().get(0).getChild().get(1).getChild()) {
                globalBuffer += "    "+checkTypeButDontCalculate(n);
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

            System.out.println("lol");
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
            if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {

                stack.push(token);

            } else {

                Object op2 = stack.pop(); // dll
                Object op1 = stack.empty() ? "0" : stack.pop();

                switch (token){

                    case "*": stack.push(multiply(op1,op2));
                    case "/": stack.push(divide(op1,op2));
                    case "+": stack.push(add(op1,op2));
                    case "-": stack.push(sub(op1,op2));

                    case "=": mapVar(op1, op2);
                    case "new": {stack.push(op1); stack.push(NewDataStruct(op1));}

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
            if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {

                System.out.println(token);
                stack.push(token);

            } else {

                System.out.println(stack.toString());

                Object op1 = stack.pop();
                Object op2 = stack.empty() ? "0" : stack.pop();
                Object op3 = stack.empty() ? "0" : stack.pop();

                switch (token){

                    case "addFront": addFront(op1, op2);
                    //case "addEnd": addFront(op1, op2);
                    //case "forward": addFront(op1, op2);
                    //case "backward": addFront(op1, op2);
                    //case "toFront": addFront(op1, op2);
                   // case "toEnd": addFront(op1, op2);

                    //case "get": addFront(op1, op2);
                    //case "remove": addFront(op1, op2);
                    //case "size": addFront(op1, op2);
                   // case "isEmpty": addFront(op1, op2);
                   // case "clear": addFront(op1, op2);
                    case "print": print(op1);
                   // case "addFront": addFront(op1, op2);

                    //case "containsKey": addFront(op1, op2);

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
            if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {
                stack.push(token);
            } else {

                String op2 = stack.pop();
                String op1 = stack.empty() ? "0" : stack.pop();

                if(token.equals(">")){
                    return Greater(op1, op2);
                }else if(token.equals("<")){
                    return Less(op1, op2);
                }else if(token.equals("==")){
                    return Equals(op1, op2);
                }else if(token.equals("!=")){
                    return NotEquals(op1, op2);
                }

            }

        }

        return false;

    }

    public String justReturnExpression(String exp){

        return sortingStation(exp, MAIN_MATH_OPERATIONS);

    }





    private String sortingStation(String expression, Map<String, Integer> operations) {
        return sortingStation(expression, operations, "(", ")");
    }

    private String sortingStation(String expression, Map<String, Integer> operations,
                                  String leftBracket, String rightBracket) {

        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");


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

        if(map.containsKey(op1)){
            a = Integer.parseInt((String) map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return Integer.toString(a + b);

    }

    private Object sub(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return Integer.toString(a - b);

    }

    private Object multiply(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return Integer.toString(a * b);

    }

    private Object divide(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return Integer.toString(a / b);

    }



    //Logical operations

    private boolean Greater(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String)op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return a > b;

    }

    private boolean Less(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return a < b;

    }

    private boolean Equals(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return a == b;

    }

    private boolean NotEquals(Object op1, Object op2){

        int a, b;

        if(map.containsKey(op1)){
            a = Integer.parseInt((String)map.get(op1));
        }else{ a = Integer.parseInt((String) op1);}

        if(map.containsKey(op1)){
            b = Integer.parseInt((String)map.get(op2));
        }else{ b = Integer.parseInt((String) op2);}

        return a != b;

    }



    //Func operations

    //common
    private Object NewDataStruct(Object struct){

        Object temp = new Object();
        String str = (String) struct;

        if(str.equals("DoubleLinkedList")){
            return new DoubleLinkedList<Integer>();
        }else if(str.equals("HashMap")){
            temp =  new CustomHashMap<String,String>();
        }

        return temp;

    }


    private String get(String op1, String key){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            int buff = ((DoubleLinkedList<Integer>)obj).getCurrent();
            return Integer.toString(buff);
        }else{
            System.out.println("error");
            return null;
        }

    }

    private void remove(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList<Integer>)obj).removeCurrent();
        }else{
            System.out.println("error");
        }

    }

    private String size(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            int buff = ((DoubleLinkedList<Integer>)obj).size();
            return Integer.toString(buff);
        }else{
            System.out.println("error");
            return null;
        }

    }

    private boolean isEmpty(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            return ((DoubleLinkedList)obj).isEmpty();
        }else if(obj instanceof CustomHashMap) {
            return ((CustomHashMap)obj).isEmpty();
        }else{
            System.out.println("error");
            return false;
        }

    }

    private void clear(String op1 ){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
           // ((DoubleLinkedList<Integer>)obj).clear();
        }else{
            System.out.println("error");
        }

    }

    private void print(Object op1){

        System.out.println("print");

        Object obj = map.get(op1);

        System.out.println(((DoubleLinkedList)obj).toString());

        if(obj instanceof DoubleLinkedList){
        }else if(obj instanceof CustomHashMap){
            System.out.println(obj.toString());
        }

    }

    //dll

    private void addFront(Object op1, Object key){

        Object obj = map.get(op1);

        System.out.println(((DoubleLinkedList)obj).toString());

        if(obj.getClass() == DoubleLinkedList.class){
            ((DoubleLinkedList)obj).addFront(Integer.parseInt((String) key));
        }else{
            System.out.println("error during addFront");
        }

    }

    private void addEnd(String op1, String key){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList<Integer>)obj).addEnd(Integer.parseInt(key));
        }else{
            System.out.println("error");
        }

    }

    private void forward(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList<Integer>)obj).iterForward();
        }else{
            System.out.println("error");
        }

    }

    private void backward(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList<Integer>)obj).iterBackward();
        }else{
            System.out.println("error");
        }

    }

    private void toFront(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList<Integer>)obj).toFront();
        }else{
            System.out.println("error");
        }

    }

    private void toEnd(String op1){

        Object obj = map.get(op1);

        if(obj instanceof DoubleLinkedList){
            ((DoubleLinkedList<Integer>)obj).toEnd();
        }else{
            System.out.println("error");
        }

    }

    private void addBefore(){

    }
    private void addAfter(){


    }


    //hashmap

    private boolean containsKey(String op1, String key){
        return ((HashMap)map.get(op1)).containsKey(key);
    }



    //Other operations

    private void mapVar(Object var, Object value){

        if(map.containsKey((String) var)){
            map.remove(var);
        }
        map.put((String) var,value);

    }

    private void mapVar(String var, Object obj){

        if(map.containsKey(var)){
            map.remove(var);
        }
        map.put(var,obj);

    }

}