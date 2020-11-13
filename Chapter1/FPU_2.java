package cpu.alu;

import transformer.Transformer;
import util.BinaryIntegers;
import util.IEEE754Float;

import java.util.regex.Pattern;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    private char CF = '0';

    public static void main(String[] args) {
    }
    /**
     * compute the float mul of a * b
     */
    String mul(String a, String b) {
        if(Pattern.matches(a, IEEE754Float.NaN) || Pattern.matches(b, IEEE754Float.NaN)){
            return "NaN";
        }

        String ansExp;
        String ansFrac;

        char signA = a.charAt(0);
        char signB = b.charAt(0);

        String ansSign = (signA == signB)? "0":"1";

        if(a.equals(IEEE754Float.N_ZERO) || b.equals(IEEE754Float.N_ZERO)
                || a.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.P_ZERO)){
            if(Pattern.matches(a, IEEE754Float.NaN) || Pattern.matches(b, IEEE754Float.NaN)){
                return IEEE754Float.NaN;
            }
            if(a.startsWith("11111111", 1) || b.startsWith("11111111", 1)){
                return IEEE754Float.NaN;
            }
            return ansSign + "0000000000000000000000000000000";
        }

        String expA = a.substring(1, 9);
        String expB = b.substring(1, 9);

        String bitA = (expA.equals("00000000"))? "0":"1";
        String bitB = (expB.equals("00000000"))? "0":"1";

        String fracA = a.substring(9);
        String fracB = b.substring(9);

        String countA = bitA + fracA + "0000"; //28
        String countB = bitB + fracB + "0000";

        String tmp = add(expA, expB);
        //CF 判断有没有溢出；
        char tmpCF = CF;
        ansExp = sub("01111111", tmp);
        //CF 判断有没有借位；

        if((tmpCF=='1' && CF=='1') || ansExp.equals("11111111")){ //一开始溢出了但是没有借位，说明
            if(signA==signB) return IEEE754Float.P_INF;
            else return IEEE754Float.N_INF;
        }

        if(tmpCF=='0' && CF=='0'){ //一开始没有溢出，但是有借位
            if(signA==signB) return IEEE754Float.P_ZERO;
            else return IEEE754Float.N_ZERO;
        }

        ansFrac = mulFrac(countA, countB).substring(1, 25);

        //Normalize
        while (ansFrac.charAt(0) == '0' && !ansExp.equals("00000000")){
            ansFrac = ansFrac.substring(1) + "0";
            ansExp = sub("00000001", ansExp);
        }
        ansFrac = ansFrac.substring(1, 24);

        return ansSign + ansExp + ansFrac;
    }

    /**
     * compute the float mul of a / b
     */
    String div(String a, String b) {

        String ansExp;
        String ansFrac;

        char signA = a.charAt(0);
        char signB = b.charAt(0);

        String ansSign = (signA == signB)? "0":"1";

        if(b.startsWith("0000000000000000000000000000000", 1)){
            if(a.equals(IEEE754Float.N_ZERO) || a.equals(IEEE754Float.P_ZERO)){
                return IEEE754Float.NaN;
            } else {
                throw new ArithmeticException();
            }
        }

        if(a.startsWith("0000000000000000000000000000000", 1)){
            return ansSign + "0000000000000000000000000000000";
        }

        String expA = a.substring(1, 9);
        String expB = b.substring(1, 9);

        String bitA = (expA.equals("00000000"))? "0":"1";
        String bitB = (expB.equals("00000000"))? "0":"1";

        String fracA = a.substring(9);
        String fracB = b.substring(9);

        String countA = bitA + fracA + "0000"; //28
        String countB = bitB + fracB + "0000";

        String tmp = sub(expB, expA);
        //CF 判断有没有溢出；
        char tmpCF = CF;
        ansExp = add("01111111", tmp);
        //CF 判断有没有借位；

        if(tmpCF == '0' && CF == '0'){
            return ansSign + "0000000000000000000000000000000";
        }

        if(tmpCF == '1' && CF == '1'){
            return ansSign + "1111111100000000000000000000000";
        }

        ansFrac = divFrac(countB, countA).substring(0, 24);

        //Normalize
        while (ansFrac.charAt(0) == '0' && !ansExp.equals("00000000")){
            ansFrac = ansFrac.substring(1) + "0";
            ansExp = sub("00000001", ansExp);
        }
        ansFrac = ansFrac.substring(1, 24);

        return ansSign + ansExp + ansFrac;
    }

    String add(String src, String dest) {
        int carry = 0;
        char[] srcArr = src.toCharArray();
        char[] destArr = dest.toCharArray();
        int len = srcArr.length;
        for(int i=len-1; i>=0; i--){
            if(srcArr[i]=='1' && destArr[i]=='1'){
                destArr[i] = (carry==1)? '1':'0';
                carry = 1;
            } else if (srcArr[i]=='1' || destArr[i]=='1'){
                if(carry==1){
                    destArr[i] = '0';
                } else {
                    destArr[i] = '1';
                    carry = 0;
                }
            } else if (carry == 1){
                destArr[i] = '1';
                carry = 0;
            }
        }
        this.CF = (carry == 1)? '1':'0';
        return String.valueOf(destArr);
    }

    String sub(String src, String dest) {
        int carry = 0;
        char[] srcArr = src.toCharArray();
        char[] destArr = dest.toCharArray();
        int len = srcArr.length;
        int flg = 0;
        for(int i=len-1; i>=0; i--){
            if(flg == 1){
                srcArr[i] = (srcArr[i]=='0')? '1':'0';
            }
            if(srcArr[i]!='0'){
                flg = 1;
            }
        }
        return add(String.valueOf(srcArr), dest);
    }

    public String mulFrac (String src, String dest){
        String Y = dest;
        String product = "0000000000000000000000000000";
        for(int i=0; i<28; i++){
            if(Y.charAt(27) == '1'){
                product = add(src, product);
            }
            product = "0" + product;
            Y = product.charAt(28) + Y.substring(0, 28);
            product = product.substring(0, 28);
        }
        return product;
    }

    public String divFrac (String src, String dest){
        String remainder = "0" + dest;
        String quotient = "00000000000000000000000000000";
        src = "0" + src;
        for(int i=0; i<29; i++){
            remainder = sub(src, remainder);
            char tmp = quotient.charAt(0);
            if(CF == '1'){
                quotient = quotient.substring(1) + "1";
            } else {
                remainder = add(src, remainder);
                quotient = quotient.substring(1) + "0";
            }
            remainder = remainder.substring(1) + tmp;
        }
        return quotient;
    }

}
