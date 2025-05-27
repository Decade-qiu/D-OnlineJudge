import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        long ans = 0;
        List<Integer> res = new ArrayList<>();
        for (int i = 0;i < 10000000;i++){
            ans += i;
            res.add(i);
        }
        System.out.println(ans+" test success!");
    }
}