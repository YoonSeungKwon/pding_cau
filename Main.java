import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        String input = br.readLine();

        int N = input.length();
        char temp = input.charAt(0);
        int count = 1;

        for(int i=1; i<N; i++){
            if(temp != input.charAt(i)){
                sb.append(count).append(temp);
                temp = input.charAt(i);
                count = 1;
            }else{
                count++;
            }
        }

        sb.append(count).append(temp);
        System.out.println(sb);


    }
}