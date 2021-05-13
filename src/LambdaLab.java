import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class LambdaLab {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print(">");
        String input = in.nextLine();

        while(!input.equals("exit")){
            if (input.contains(";")){
                input = input.substring(0, input.indexOf(";"));
            }
            ArrayList<Expression> tokens = makeVars(tokenize(input));
            while (tokens.size() > 1) {
                tokens.set(0, new Application(tokens.get(0), tokens.get(1)));
                tokens.remove(1);
            }
            System.out.println(tokens.get(0));
            System.out.print(">");
            input = in.nextLine();
        }
        System.out.println("Goodbye!");
    }

    public static ArrayList<String> tokenize(String input){
        char[] temp = input.toCharArray();
        ArrayList<String> tokens = new ArrayList<>();
        for (char c: temp) {
            tokens.add(Character.toString(c));
        }
        for (int i = 0; i < tokens.size(); i++){
            if (!Arrays.asList("\\", ".", "(", ")", "λ").contains(tokens.get(i))){
                makeWord(tokens, i);
            }
        }
        return tokens;
    }

    public static void makeWord(ArrayList<String> tokens, int i){
        if (i < tokens.size() - 1 && !Arrays.asList("\\", ".", "(", ")", "λ", " ").contains(tokens.get(i + 1))){
            tokens.add(i, tokens.get(i) + tokens.get(i + 1));
            tokens.remove(i + 1);
            tokens.remove(i + 1);
            makeWord(tokens, i);
        }
    }

    public static ArrayList<Expression> makeVars (ArrayList<String> tokens){
        ArrayList<Expression> ret = new ArrayList<>();
        for (String token: tokens) {
                ret.add(new Variable(token));
        }
        return ret;
    }
}
