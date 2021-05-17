import java.util.*;

public class LambdaLab {

    static Hashtable<String, Expression > dict = new Hashtable<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("> ");
        String input = in.nextLine().replaceAll("\uFEFF", "");


        while(!input.equals("exit")){
            if (!input.equals("")) {

                if (input.contains(";")) {
                    input = input.substring(0, input.indexOf(";"));
                }

                if (input.contains("=")) {
                    String var = input.substring(0, input.indexOf("=")).replaceAll(" ", "");
                    ArrayList<Expression> tokens = makeVars(tokenize(input.substring(input.indexOf("=") + 1)));
                    removeParens(tokens);

                    if (dict.containsKey(var)) {
                        System.out.println("Key is already defined");
                    }
                    else {
                        dict.put(var, tokens.get(0));
                        System.out.println("Added " + var + " as " + dict.get(var));
                    }
                }
                else {
                    ArrayList<Expression> tokens = makeVars(tokenize(input));
                    removeParens(tokens);
                    if (tokens.size() > 0)
                        System.out.println(tokens.get(0));
                }
            }
            System.out.print("> ");
            input = in.nextLine().replaceAll("\uFEFF", "");
        }
        System.out.println("Goodbye!");
    }

    public static ArrayList<String> tokenize(String input) {
        ArrayList<String> tokens = new ArrayList<>();
        for (char c : input.toCharArray()) {
            tokens.add(Character.toString(c));
        }
        for (int i = 0; i < tokens.size(); i++) {
            if (!Arrays.asList("\\", ".", "(", ")", "λ", " ").contains(tokens.get(i))) {
                makeWord(tokens, i);
            }
        }
        for (int i = 0; i < tokens.size(); i++) {
            if (!(dict.get(tokens.get(i)) == null)) {
                tokens.set(i, (dict.get(tokens.get(i))).toString());
            }
        }

            return tokens;
    }


    public static void makeWord(ArrayList<String> tokens, int i){
        if (i < tokens.size() - 1 && !Arrays.asList("\\", ".", "(", ")", "λ", " ").contains(tokens.get(i + 1))){
            tokens.set(i, tokens.get(i) + tokens.get(i + 1));
            tokens.remove(i + 1);
            makeWord(tokens, i);
        }
    }

    public static ArrayList<Expression> makeVars (ArrayList<String> tokens){
        ArrayList<Expression> ret = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            while (i < tokens.size() && tokens.get(i).equals(" ")){
                tokens.remove(i);
            }
            if (i < tokens.size()) {
                ret.add(new Variable(tokens.get(i)));
            }
        }
        return ret;
    }

    public static void removeParens(ArrayList<Expression> tokens){
        ArrayList<Expression> parenExp = new ArrayList<>();
        Stack<Expression> parenStack = new Stack<>();
        Variable placeholder = new Variable("start");
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).toString().equals("(")){
                parenStack.push(tokens.get(i));
                tokens.remove(i++);
                tokens.add(i - 1, placeholder);
                while (true){
                    if (tokens.get(i).toString().equals(")")) {
                        parenStack.pop();
                        if (parenStack.size() == 0){
                            tokens.remove(i);
                            break;
                        }
                    }
                    else if (tokens.get(i).toString().equals("("))
                        parenStack.push(tokens.get(i));
                    parenExp.add(tokens.get(i));
                    tokens.remove(i);
                }
                removeParens(parenExp);
                for (int j = 0; j < parenExp.size(); j++) {
                    tokens.add(tokens.indexOf(placeholder), parenExp.get(0));
                    parenExp.remove(0);
                }
                tokens.remove(placeholder);
            }
        }
        makeFunc(tokens);
    }

    public static void reduce(ArrayList<Expression> tokens){
        while (tokens.size() > 1) {
            tokens.set(0, new Application(tokens.get(0), tokens.get(1)));
            tokens.remove(1);
        }
    }

    public static Expression makeFunc(ArrayList<Expression> tokens){
        Expression lambdaExp = null;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).toString().equals(".")) {
                while (i < tokens.size() - 1 && !tokens.get(i + 1).toString().equals("(") &&
                        !tokens.get(i + 1).toString().equals("\\") && !tokens.get(i + 1).toString().equals("λ")) {
                    if (lambdaExp == null)
                        lambdaExp = tokens.get(i + 1);
                    else
                        lambdaExp = new Application(lambdaExp, tokens.get(i + 1));
                    tokens.remove(i + 1);
                }
                tokens.remove(i);
            }
            if (i >= tokens.size() - 1){
                break;
            }
            if (tokens.get(i - 1).toString().equals("(")){
                ArrayList<Expression> nextLambda = new ArrayList<>();
                int end = 0;
                while (end < tokens.size() && !tokens.get(end).toString().equals(")")){
                    end++;
                }
                for (int j = i; j < end; j++) {
                    nextLambda.add(tokens.get(i));
                    tokens.remove(i);
                }
                tokens.set(i - 1, makeFunc(nextLambda));
                tokens.remove(i);
            }
            else if (tokens.get(i).toString().equals("\\") || tokens.get(i).toString().equals("λ")){
                ArrayList<Expression> nextLambda = new ArrayList<>();
                int target = tokens.size();
                for (int j = i; j < target; j++) {
                    nextLambda.add(tokens.get(i));
                    tokens.remove(i);
                }
                if (lambdaExp == null)
                    lambdaExp = makeFunc(nextLambda);
                else
                    lambdaExp = new Application(lambdaExp, makeFunc(nextLambda));
                break;
            }
        }
        for (int i = 0; i < tokens.size(); i++){
            if (tokens.get(i).toString().equals("\\") || tokens.get(i).toString().equals("λ")){
                tokens.set(i, new Function((Variable)tokens.get(i + 1), lambdaExp));
                tokens.remove(i + 1);
            }
        }
        reduce(tokens);
        if (tokens.size() == 0){
            return null;
        }
        return tokens.get(0);
    }
}