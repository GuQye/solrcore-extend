import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by . on 2018/3/23.
 */
public class commonTest {
    public static void main(String[] args) {
        BigInteger max = BigInteger.valueOf(100);
        BigInteger min = BigInteger.valueOf(10);
        System.out.println(max.subtract(min).toString().length());
//        final DecimalFormat encoder = new DecimalFormat("0000000", new DecimalFormatSymbols(Locale.ROOT));
//        String a = encoder.format(123);
//        System.out.println(a);
    }
}
