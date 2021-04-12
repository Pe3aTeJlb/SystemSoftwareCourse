import java.util.Stack;

import java.util.*;

public class ShuntingYard {

    private Stack<String> stack = new Stack<>();

    private HashMap<String, Integer> map = new HashMap<>();

    private static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {

        MAIN_MATH_OPERATIONS = new HashMap<String, Integer>();

        MAIN_MATH_OPERATIONS.put("=", 0);
        MAIN_MATH_OPERATIONS.put("+", 1);
        MAIN_MATH_OPERATIONS.put("-", 1);
        MAIN_MATH_OPERATIONS.put("*", 2);
        MAIN_MATH_OPERATIONS.put("/", 2);

    }

    public void constructExpression(Node root){

        //exp
        for (Node n: root.getChild()) {
            String buff = exploreTree(n);
            System.out.println(buff);
        }

    }

    private String exploreTree(Node root){

        String buff = "";

        for (Lexeme l: root.getLexemes()) {
           // System.out.println(l.getValue());
            buff += l.getValue();
        }


        for(int i = 0; i < root.getChild().size(); i++){

            if(i == 1){

                buff = exploreTree(root.getReversedChild().get(i))+buff;
            }
            else {

                buff += exploreTree(root.getReversedChild().get(i));
            }

        }

        //for (Node n: root.getChild()) {
        //    buff += exploreTree(n);
        //}

        return buff;

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


        List<String> out = new ArrayList<String>();

        Stack<String> stack = new Stack<String>();


        expression = expression.replace(" ", "");

        Set<String> operationSymbols = new HashSet<>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);


        int index = 0;
        boolean findNext = true;

        while (findNext) {
            int nextOperationIndex = expression.length();
            String nextOperation = "";
            // Поиск следующего оператора или скобки.
            for (String operation : operationSymbols) {
                int i = expression.indexOf(operation, index);
                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }
            }
            // Оператор не найден.
            if (nextOperationIndex == expression.length()) {
                findNext = false;
            } else {
                // Если оператору или скобке предшествует операнд, добавляем его в выходную строку.
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }
                // Обработка операторов и скобок.
                // Открывающая скобка.
                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                }
                // Закрывающая скобка.
                else if (nextOperation.equals(rightBracket)) {
                    while (!stack.peek().equals(leftBracket)) {
                        out.add(stack.pop());
                        if (stack.empty()) {
                            throw new IllegalArgumentException("Unmatched brackets");
                        }
                    }
                    stack.pop();
                }
                // Операция.
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

        // Добавление в выходную строку операндов после последнего операнда.
        if (index != expression.length()) {
            out.add(expression.substring(index));
        }

        // Пробразование выходного списка к выходной строке.
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


    private void calculateExpression(String expression) {

        String rpn = sortingStation(expression, MAIN_MATH_OPERATIONS);

        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");

        Stack<String> stack = new Stack<String>();

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
                }

            }

        }

        if (stack.size() != 1)
            throw new IllegalArgumentException("Expression syntax error.");

        //return stack.pop();

    }

    private String add(String op1, String op2){
        return null;
    }

    private String sub(String op1, String op2){
        return null;
    }

    private String multiply(String op1, String op2){
        return null;
    }

    private String divide(String op1, String op2){
        return null;
    }

    private void mapVar(String var, int value){

        if(map.containsKey(var)){
            map.remove(var);
        }
        map.put(var,value);

    }

}