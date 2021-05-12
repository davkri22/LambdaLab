import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class LambdaLab {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print(">");
        String input = in.nextLine();

        while(!input.equals("exit")){
            System.out.print(">");
            input = in.nextLine();
            if (input.contains(";")){
                input = input.substring(0, input.indexOf(";"));
            }
            ArrayList<String> tokens = tokenize(input);
        }
        System.out.println("Goodbye!");
    }

    public static ArrayList<String> tokenize(String input){
        char[] chars = input.toCharArray();
        ArrayList<String> ret = new ArrayList<>();
        String varWord = "";
        for (int i = 0; i < chars.length; i++){
            if (Arrays.asList('\\', '.', '(', ')', ' ').contains(chars[i])){
                ret.add(Character.toString(chars[i]));
            }
            else{
                while(!(Arrays.asList('\\', '.', '(', ')', ' ').contains(chars[i]))){
                    varWord += chars[i];
                    i++;
                }
            }
            ret.add(varWord);
        }
        return ret;
    }
}
