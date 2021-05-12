import java.util.*;

public class LambdaLab {
    public static void main(String[] args) {
        boolean run = true;

        Scanner user = new Scanner(System.in);


        String[] first = split("System.out.print(true)");
        System.out.println(Arrays.toString(first));

        //idk why this isn't working
        String[] second = split("\\sheep.(sheep goats)");
        System.out.println(Arrays.toString(second));

        while (run) {
            String input = user.nextLine();

            if (input.equals("stop")) {
                break;
            }

            if (input.equals("")) {
                continue;
            }

            else {
                //trying to split to tokens
                split (input);
            }
        }

    }

//\sheep.(sheep goats)
 public static String[] split (String input) {

     String[] ops = input.split("\\s*[a-zA-Z]+\\s*");
     String[] notops = input.split("\\s*[^a-zA-Z]+\\s*");
     String[] res = new String[ops.length + notops.length - 1];
     for (int i = 0; i < res.length; i++)
         res[i] = i % 2 == 0 ? notops[i / 2] : ops[i / 2 + 1];

     return res;

 }
}