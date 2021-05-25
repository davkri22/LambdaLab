import java.util.*;

public class LambdaLab {

    static Hashtable<String, Expression > dict = new Hashtable<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("> ");
        String input = in.nextLine().replaceAll("\uFEFF", "");


        while(!input.equals("exit")){
            ArrayList<Expression> tokens;
            if (!input.equals("")) {

                if (input.contains(";")) {
                    input = input.substring(0, input.indexOf(";"));
                }

                if (input.contains("=")) {
                    String var = input.substring(0, input.indexOf("=")).replaceAll(" ", "");
                    tokens = makeVars(tokenize(input.substring(input.indexOf("=") + 1)));
                    removeParens(tokens);

                    if (dict.containsKey(var)) {
                        System.out.println("Key is already defined");
                    }
                    else if (tokens.get(0).toString().equals("run")) {
                        tokens.remove(0);
                        if (tokens.get(0).getClass() == Application.class) {
                            Application app = (Application) tokens.get(0);
                            tokens.set(0, runApp(app));
                        }
                        dict.put(var, tokens.get(0).deepCopy());
                        System.out.println("Added " + dict.get(var) + " as " + var);
                    }

                    else {
                        dict.put(var, tokens.get(0).deepCopy());
                        System.out.println("Added " + dict.get(var) + " as " + var);
                    }

                }
                else {
                    tokens = makeVars(tokenize(input));
                    if (tokens.size() == 0){
                        System.out.print("> ");
                        input = in.nextLine().replaceAll("\uFEFF", "");
                        continue;
                    }
                    removeParens(tokens);
                    if (tokens.get(0).toString().equals("run")){
                        tokens.remove(0);
                        if (tokens.get(0).getClass() == Application.class) {
                            Application app = (Application) tokens.get(0);
                            tokens.set(0, runApp(app));
                        }
                    }
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
        for (int i = 0; i < tokens.size(); i++) {
            if (!(dict.get(tokens.get(i)) == null)) {
                ret.set(i, (dict.get(tokens.get(i)).deepCopy()));
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
                i = 0;
            }
        }
        makeFunc(tokens);
    }

    public static void reduce(ArrayList<Expression> tokens){
        if (tokens.get(0).toString().equals("run")){
            while (tokens.size() > 2) {
                tokens.set(1, new Application(tokens.get(1), tokens.get(2)));
                tokens.remove(2);
            }
        }
        else {
            while (tokens.size() > 1) {
                tokens.set(0, new Application(tokens.get(0), tokens.get(1)));
                tokens.remove(1);
            }
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
                tokens.remove(i--);
            }
            if (i >= tokens.size() - 1){
                break;
            }
            else if ((tokens.get(i).toString().equals("\\") || tokens.get(i).toString().equals("λ")) &&
                    !tokens.get(i - 1).toString().equals("run")){
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

    public static Expression runApp(Expression exp){
        Expression ret = exp;
        if (exp.getClass() == Application.class) {
            if (((Application) exp).lExp.getClass() == Application.class)
                ((Application) exp).lExp = runApp(((Application) exp).lExp);
            if (((Application) exp).rExp.getClass() == Application.class)
                ((Application) exp).rExp = runApp(((Application) exp).rExp);
            if (((Application) exp).lExp.getClass() == Function.class)
                ret = ((Function) ((Application) exp).lExp).run(((Application) exp).rExp);
            if (ret.getClass() == Function.class && ((Function) ret).exp.getClass() == Application.class)
                ((Function) ret).exp = runApp(((Function) ret).exp);
        }
        if (ret.getClass() == Function.class) {
            ((Function)ret).exp = runApp(((Function)ret).exp);
        }
        return ret;
    }
}