import org.w3c.dom.Node;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Main2 {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String input = br.readLine();

        int N = input.length();

        int[][] arr =new int[100][2];

        for(int i=0; i<100; i++){
            arr[i][0] = i;
        }

        for(int i=0; i<N; i++){
            arr[input.charAt(i)-'A'][1] ++;
        }

        Arrays.sort(arr, (o1, o2)->{
            return o2[1]-o1[1];
        });
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while(arr[index][1] > 0){
            sb.append(arr[index][1]).append((char)(arr[index][0]+'A'));
            index++;
        }

        System.out.println(sb.toString());

    }


}
