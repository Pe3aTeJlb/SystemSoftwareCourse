import java.util.Stack;

import java.util.*;

public class ShuntingYard {

    private Stack<String> stack = new Stack<>();

    private HashMap<String, String> map = new HashMap<>();

    private static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {

        MAIN_MATH_OPERATIONS = new HashMap<>();

        MAIN_MATH_OPERATIONS.put("=", 3);
        MAIN_MATH_OPERATIONS.put("<", 3);
        MAIN_MATH_OPERATIONS.put(">", 3);
        MAIN_MATH_OPERATIONS.put("==", 3);
        MAIN_MATH_OPERATIONS.put("!=", 3);

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

                for (Node n: exp.getChild().get(0).getChild().get(2).getChild()) {
                    checkType(n);
                }

            }

        }else if(exp.getChild().get(0).getName().equals("while_expr")){

        }

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



    public void calculateExpression(String exp) {

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

                if (token.equals("*")) {
                    stack.push(multiply(op1,op2));
                } else if (token.equals("/")) {
                    stack.push(divide(op1,op2));
                } else if (token.equals("+")) {
                    stack.push(add(op1,op2));
                } else if (token.equals("-")) {
                    stack.push(sub(op1,op2));
                }else if(token.equals("=")){
                    mapVar(op1, op2);
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

    private String sortingStation(ArrayList<Lexeme> lexemes, int treeLeaves, Map<String, Integer> operations) {

        if (lexemes.size() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");

        String expression = "";

        for(int i = 0; i < treeLeaves; i++){
            expression += lexemes.get(i).getValue() + " ";
        }
        System.out.println("Original expression: "+expression);

        for(int i = 0; i < treeLeaves; i++){
           lexemes.remove(0);
        }


        String leftBracket = "(";
        String rightBracket = ")";

        List<String> out = new ArrayList<>();

        Stack<String> stack = new Stack<>();

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

    private String add(String op1, String op2){

        //System.out.println("add");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) + Integer.parseInt(op2));
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) + Integer.parseInt(map.get(op2)));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) + Integer.parseInt(map.get(op2)));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) + Integer.parseInt(op2));
        }

        return null;

    }

    private String sub(String op1, String op2){

        //System.out.println("sub");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) - Integer.parseInt(op2));
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) - Integer.parseInt(map.get(op2)));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) - Integer.parseInt(map.get(op2)));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) - Integer.parseInt(op2));
        }

        return null;

    }

    private String multiply(String op1, String op2){

       // System.out.println("mul");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) * Integer.parseInt(op2));
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) * Integer.parseInt(map.get(op2)));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) * Integer.parseInt(map.get(op2)));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) * Integer.parseInt(op2));
        }

        return null;

    }

    private String divide(String op1, String op2){

        //System.out.println("div");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) / Integer.parseInt(op2));
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) / Integer.parseInt(map.get(op2)));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(map.get(op1)) / Integer.parseInt(map.get(op2)));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.toString(Integer.parseInt(op1) / Integer.parseInt(op2));
        }

        return null;

    }

    //Logical operations

    private boolean Greater(String op1, String op2){

        //System.out.println("div");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) > Integer.parseInt(op2);
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(op1) > Integer.parseInt(map.get(op2));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) > Integer.parseInt(map.get(op2));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(op1) > Integer.parseInt(op2);
        }else {return false;}

    }

    private boolean Less(String op1, String op2){

        //System.out.println("div");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) < Integer.parseInt(op2);
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(op1) < Integer.parseInt(map.get(op2));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) < Integer.parseInt(map.get(op2));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(op1) < Integer.parseInt(op2);
        }else {return false;}

    }

    private boolean Equals(String op1, String op2){

        //System.out.println("div");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) == Integer.parseInt(op2);
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(op1) == Integer.parseInt(map.get(op2));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) == Integer.parseInt(map.get(op2));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(op1) == Integer.parseInt(op2);
        }else {return false;}

    }

    private boolean NotEquals(String op1, String op2){

        //System.out.println("div");

        if(map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) != Integer.parseInt(op2);
        }else if(!map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(op1) != Integer.parseInt(map.get(op2));
        } else if(map.containsKey(op1) && map.containsKey(op2)){
            return Integer.parseInt(map.get(op1)) != Integer.parseInt(map.get(op2));
        } else if(!map.containsKey(op1) && !map.containsKey(op2)){
            return Integer.parseInt(op1) != Integer.parseInt(op2);
        }else {return false;}

    }

    //Other operations

    private void mapVar(String var, String value){

        if(map.containsKey(var)){
            map.remove(var);
        }
        map.put(var,value);

    }

}