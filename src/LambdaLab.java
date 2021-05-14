import java.util.*;

public class LambdaLab {

    static Hashtable<String, Expression > dict = new Hashtable<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print(">");
        String input = in.nextLine().replaceAll("\uFEFF", "");



        while(!input.equals("exit")){
            if (!input.equals("")) {

                if(!(dict.get(input) == null)) {
                    System.out.println(dict.get(input));
                }

                else if (input.contains("=")) {
                    String var = input.substring(0, input.indexOf(" "));
                    ArrayList<Expression> tokens = makeVars(tokenize(input.substring(input.indexOf("= ") + 1)));
                    removeParens(tokens);
                    makeFunc(tokens);
                    reduce(tokens);

                    dict.put(var, tokens.get(0));

                    System.out.println ("Added " + var + " as " + dict.get(var));
                }
                else {
                    if (input.contains(";")) {
                        input = input.substring(0, input.indexOf(";"));
                    }
                    ArrayList<Expression> tokens = makeVars(tokenize(input));
                    removeParens(tokens);
                    makeFunc(tokens);
                    reduce(tokens);
                    if (tokens.size() > 0)
                        System.out.println(tokens.get(0));
                }
            }
            System.out.print(">");
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
        for (int i = 0; i < tokens.size(); i++){
            if (tokens.get(i).toString().equals("(")){
                if (tokens.get(i + 1).toString().equals("\\") || tokens.get(i + 1).toString().equals("λ")){
                    while (!tokens.get(i + 1).toString().equals(")")){
                        i++;
                    }
                    break;
                }
                if (!tokens.get(i + 1).toString().equals("(") && !tokens.get(i + 1).toString().equals(")") &&
                        !tokens.get(i + 2).toString().equals("(") && !tokens.get(i + 2).toString().equals(")")) {
                    tokens.set(i, new Application(tokens.get(i + 1), tokens.get(i + 2)));
                    tokens.remove(i + 1);
                    tokens.remove(i + 1);
                    while (!tokens.get(i + 1).toString().equals(")")) {
                        tokens.set(i, new Application(tokens.get(i), tokens.get(i + 1)));
                        tokens.remove(i + 1);
                    }
                }
                else if (!tokens.get(i + 1).toString().equals("(") && !tokens.get(i + 1).toString().equals(")")){
                    tokens.set(i, tokens.get(i + 1));
                    tokens.remove(i + 1);

                }
                else
                    tokens.remove(i--);
            }
            else if (tokens.get(i).toString().equals(")")){
                tokens.remove(i--);
            }
        }
    }

    public static void reduce(ArrayList<Expression> tokens){
        while (tokens.size() > 1) {
            tokens.set(0, new Application(tokens.get(0), tokens.get(1)));
            tokens.remove(1);
        }
    }

    public static Expression makeFunc(ArrayList<Expression> tokens){
        Expression lambdaExp = null;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).toString().equals(".")) {
                lambdaExp = tokens.get(i + 1);
                tokens.remove(i);
                while (i < tokens.size() - 1 && !tokens.get(i + 1).toString().equals(")") &&
                        !tokens.get(i + 1).toString().equals("\\") && !tokens.get(i + 1).toString().equals("λ")) {
                    lambdaExp = new Application(lambdaExp, tokens.get(i + 1));
                    tokens.remove(i + 1);
                }
            }
            if (i >= tokens.size() - 1){
                break;
            }
            if (tokens.get(i).toString().equals("(")){
                ArrayList<Expression> nextLambda = new ArrayList<>();
                int end = 0;
                while (end < tokens.size() && !tokens.get(end).toString().equals(")")){
                    end++;
                }
                for (int j = i + 1; j < end; j++) {
                    nextLambda.add(tokens.get(i + 1));
                    tokens.remove(i + 1);
                }
                tokens.set(i, makeFunc(nextLambda));
                tokens.remove(i + 1);
            }
            else if (tokens.get(i + 1).toString().equals("\\") || tokens.get(i + 1).toString().equals("λ")){
                ArrayList<Expression> nextLambda = new ArrayList<>();
                int target = tokens.size();
                for (int j = i + 1; j < target; j++) {
                    nextLambda.add(tokens.get(i + 1));
                    tokens.remove(i + 1);
                }
                lambdaExp = new Application(lambdaExp, makeFunc(nextLambda));
                break;
            }
        }
        for (int i = 0; i < tokens.size(); i++){
            if (tokens.get(i).toString().equals("\\") || tokens.get(i).toString().equals("λ")){
                tokens.set(i, new Function((Variable)tokens.get(i + 1), lambdaExp));
                tokens.remove(i + 1);
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