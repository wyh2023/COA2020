package transformer;


import java.util.Arrays;

//
public class Transformer {

    /**
     * Integer to binaryString
     *
     * @param numStr to be converted
     * @return result
     */
    public String intToBinary(String numStr) {
        //TODO:
        int num = Integer.parseInt(numStr);
        int sign = 0;
        if(num < 0){
            sign = 1;
            num = -num;
        }
        char[] binaryStr = new char[32];
        for(int i=31; i>=0; i--){
            if(num % 2 == 0){
                binaryStr[i] = '0';
            } else {
                binaryStr[i] = '1';
            }
            num /= 2;
        }
        //deal with negative numbers
        if(sign == 1){
            int index = 31;
            int flg = 0;
            for(; index>=0; index--){
                if (flg == 1){
                    binaryStr[index] = (binaryStr[index]=='1')? '0':'1';
                }
                if(binaryStr[index]=='1'){
                    flg = 1;
                }
            }
        }
        return String.valueOf(binaryStr);
    }

    /**
     * BinaryString to Integer
     *
     * @param binStr : Binary string in 2's complement
     * @return :result
     */
    public String binaryToInt(String binStr) {
        //TODO:
        char[] bits = binStr.toCharArray();
        char sign = bits[0];
        if(sign == '1'){
            int index = 31;
            int flg = 0;
            for(; index>=0; index--){
                if (flg == 1){
                    bits[index] = (bits[index]=='1')? '0':'1';
                }
                if(bits[index]=='1'){
                    flg = 1;
                }
            }
        }
        int ans = 0;
        int num = 1;
        for(int i=31; i>=0; i--){
            if(bits[i] == '1'){
                ans += num;
            }
            num <<= 1;
        }
        if(sign == '1') ans = -ans;
        return String.valueOf(ans);
    }

    /**
     * Float true value to binaryString
     * @param floatStr : The string of the float true value
     * */
    public String floatToBinary(String floatStr) {
        //TODO:
        String sign = "0";
        String expStr;
        String fracStr;

        int exponent = 127;

        float num = Float.parseFloat(floatStr);

        if(num < 0){
            sign = "1";
            num = -num;
        }

        while (num-2 > 0){
            num /= 2;
            exponent++;
            if(exponent == 255) break;
        }
        while (num-1 < 0){
            num *= 2;
            exponent--;
            if(exponent < 1) break;
        }

        double f = num - 1;
        if(exponent < 1 && num != 1){
            expStr = "00000000";
        } else if (exponent == 255){
            expStr = "11111111";
        } else {
            int expTmp = exponent;
            StringBuilder expBuilder = new StringBuilder();
            for(int i=0; i<8; i++){
                if(expTmp % 2 == 1){
                    expBuilder.insert(0, "1");
                } else {
                    expBuilder.insert(0, "0");
                }
                expTmp /= 2;
            }
            expStr = expBuilder.toString();
        }

        StringBuilder fracBuilder = new StringBuilder();
        if(exponent == 255){
            fracBuilder.append("00000000000000000000000");
        } else {
            if(exponent == 0){
                f = num / 2;
            }
            while (f > 0){
                f *= 2;
                if(f >= 1){
                    fracBuilder.append("1");
                    f -= 1;
                } else {
                    fracBuilder.append("0");
                }
            }
            int pending = 23-fracBuilder.length();
            for(int i=0; i<pending; i++){
                fracBuilder.append("0");
            }
        }
        fracStr = fracBuilder.toString();
        //System.out.println(sign + expStr + fracStr);
        //System.out.println(exponent);
        //ystem.out.println(f);
        String ans = sign + expStr + fracStr;
        if(ans.equals("01111111100000000000000000000000")){
            ans = "+Inf";
        }
        if(ans.equals("11111111100000000000000000000000")){
            ans = "-Inf";
        }
        return ans;
    }

    /**
     * Binary code to its float true value
     * */
    public String binaryToFloat(String binStr) {
        //TODO:
        String sign = binStr.substring(0, 1);
        String exponent = binStr.substring(1, 9);
        String fraction = binStr.substring(9, 32);
        String ans = "";
        if(exponent.equals("00000000") && fraction.equals("00000000000000000000000")){
            return "0.0";
        }

        int exp = str2int(exponent, 8);
        double frac = 0.0;
        if( exp > 0 && exp < 255 ){
            frac += str2flt(fraction);
            frac += 1;
            ans = Double.toString((frac * Math.pow(2, exp-127)));
        }

        if( exp == 255 && fraction.equals("00000000000000000000000")){
            ans = "Inf";
            if(sign.equals("0")){
                ans = "+" + ans;
            }
        }

        if( exp == 255 && !fraction.equals("00000000000000000000000")){
            ans = "NaN";
        }

        if( exp == 0 ){
            frac += str2flt(fraction);
            ans = Double.toString((frac * Math.pow(2, exp-126)));
        }

        if(sign.equals("1")){
            ans = "-" + ans;
        }
        return ans;
    }

    /**
     * The decimal number to its NBCD code
     * */
    public String decimalToNBCD(String decimal) {
        //TODO:
        char[] numLst = decimal.toCharArray();
        StringBuilder ansBuilder = new StringBuilder();
        int i = 0;
        if(numLst[0] == '-'){
            ansBuilder.append("1101");
            i = 1;
        } else {
            ansBuilder.append("1100");
        }
        for(int j=0; j<7-numLst.length+i;j++){
            ansBuilder.append("0000");
        }
        String BCD = "";
        for(; i<numLst.length; i++){
            switch (numLst[i]){
                case '0':
                    BCD = "0000"; break;
                case '1':
                    BCD = "0001"; break;
                case '2':
                    BCD = "0010"; break;
                case '3':
                    BCD = "0011"; break;
                case '4':
                    BCD = "0100"; break;
                case '5':
                    BCD = "0101"; break;
                case '6':
                    BCD = "0110"; break;
                case '7':
                    BCD = "0111"; break;
                case '8':
                    BCD = "1000"; break;
                case '9':
                    BCD = "1001"; break;
            }
            ansBuilder.append(BCD);
        }
        return ansBuilder.toString();
    }

    /**
     * NBCD code to its decimal number
     * */
    public String NBCDToDecimal(String NBCDStr) {
        //TODO:
        StringBuilder strBuilder = new StringBuilder();
        String BCD = "";
        for(int i=0; i<NBCDStr.length()-3; i+=4){
            String swt = NBCDStr.substring(i, i+4);
            switch (swt){
                case "0000":
                    BCD = "0"; break;
                case "0001":
                    BCD = "1"; break;
                case "0010":
                    BCD = "2"; break;
                case "0011":
                    BCD = "3"; break;
                case "0100":
                    BCD = "4"; break;
                case "0101":
                    BCD = "5"; break;
                case "0110":
                    BCD = "6"; break;
                case "0111":
                    BCD = "7"; break;
                case "1000":
                    BCD = "8"; break;
                case "1001":
                    BCD = "9"; break;
                case "1100":
                    BCD = ""; break;
                case "1101":
                    BCD = "-"; break;
            }
            strBuilder.append(BCD);
        }
        while (strBuilder.indexOf("0")==0 && strBuilder.length()>1){
            strBuilder.delete(0, 1);
        }
        while (strBuilder.indexOf("-")==0 && strBuilder.indexOf("0")==1 && strBuilder.length()>1){
            strBuilder.delete(1, 2);
        }
        return strBuilder.toString();
    }

    public static int str2int(String str, int len){
        char[] strlst = str.toCharArray();
        int num = 1;
        int ans = 0;
        for(int i=0; i<len; i++){
            if(strlst[len-1-i] == '1')
                ans += num;
            num <<= 1;
        }
        return ans;
    }

    public static double str2flt(String str){
        char[] strlst = str.toCharArray();
        double num = 0.5;
        double ans = 0.0;
        for(int i=0; i<23; i++){
            if(strlst[i]=='1')
                ans += num;
            num *= 0.5;
        }
        return ans;
    }
}
