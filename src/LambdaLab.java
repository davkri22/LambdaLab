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
        char[] chars = input.toCharArray();
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder varWord = new StringBuilder();
        for (int i = 0; i < chars.length; i++){
            if (Arrays.asList('\\', '.', '(', ')', ' ').contains(chars[i])){
                ret.add(Character.toString(chars[i]));
            }
            else{
                while(i < chars.length && !(Arrays.asList('\\', '.', '(', ')', ' ').contains(chars[i]))){
                    varWord.append(chars[i]);
                    i++;
                }
            }
            ret.add(varWord.toString());
            varWord = new StringBuilder();
        }
        return ret;
    }

    public static ArrayList<Expression> makeVars (ArrayList<String> tokens){
        ArrayList<Expression> ret = new ArrayList<>();
        for (String token: tokens) {
                ret.add(new Variable(token));
        }
        return ret;
    }
}
