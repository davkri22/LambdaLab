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
        }
        System.out.println("Goodbye!");
    }
}
