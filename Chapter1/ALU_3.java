package cpu.alu;

import transformer.Transformer;
import util.BinaryIntegers;
import util.IEEE754Float;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 乘除
 */
public class ALU {

	// 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    public static void main(String[] args){
        ALU alu = new ALU();
        Transformer transformer = new Transformer();
        String a = transformer.intToBinary("-9");
        String b = transformer.intToBinary("-3");
        //System.out.println(transformer.binaryToInt("11111111111111111110001010101010"));
        //System.out.println(transformer.binaryToInt("00000000000000000000000101011011"));
        System.out.println(alu.div(a, b));
        System.out.println(-7510%347);
        System.out.print("0");
        System.out.print(transformer.intToBinary("3"));
        System.out.print(transformer.intToBinary("0"));
    }

	/**
	 * 返回两个二进制整数的乘积(结果低位截取后32位)
	 * @param src 32-bits
	 * @param dest 32-bits
	 * @return 32-bits
	 */
	public String mul (String src, String dest){
		//TODO
        dest = dest + "0";
        String product = "00000000000000000000000000000000";

        for (int i=0; i<32; i++){
            char Y = dest.charAt(32);
            char Yi = dest.charAt(31);
            if(Y - Yi == 1){
                product = add(src, product);
            } else if(Y - Yi == -1) {
                product = sub(src, product);
            }
            dest = product.charAt(31) + dest.substring(0, 32);
            product = product.charAt(0) + product.substring(0, 31);
        }
        String ret = product + dest.substring(0, 32);

	    return ret.substring(32, 64);
    }

    /**
     * 返回两个二进制整数的除法结果 operand1 ÷ operand2
     * @param operand1 32-bits
     * @param operand2 32-bits
     * @return 65-bits overflow + quotient + remainder
     */
    public String div(String operand1, String operand2) {
    	//TODO
        if(operand2.equals(BinaryIntegers.ZERO)){
            if(operand1.equals(BinaryIntegers.ZERO)){
                return "NaN";
            } else {
                throw new ArithmeticException();
            }
        }

        String overflow = "0";
        if(operand1.equals(IEEE754Float.N_ZERO) && operand2.equals(BinaryIntegers.NegativeOne)){
            overflow = "1";
        }

        char aSign = operand1.charAt(0);
        char bSign = operand2.charAt(0);

        if(bSign == '1') operand2 = Neg(operand2);

        String[] ans;

        if(aSign=='0'){
            ans = posDiv(operand1, operand2);
            if(bSign=='1'){
                ans[1] = Neg(ans[1]);
            }
        } else{
            ans = negDiv(operand1, operand2);
            if(bSign=='0'){
                ans[1] = Neg(ans[1]);
            }
        }
        return overflow + ans[1] + ans[0];
    }

    String[] posDiv(String quotient, String division){
        String[] ans = new String[2];
        String remainder = "00000000000000000000000000000000";
        for(int i=0; i<32; i++){
            remainder = remainder.substring(1) + quotient.charAt(0);
            if(sub(division, remainder).charAt(0) != remainder.charAt(0)){
                quotient = quotient.substring(1) + "0";
            } else {
                quotient = quotient.substring(1) + "1";
                remainder = sub(division, remainder);
            }
        }
        ans[0] = remainder;
        ans[1] = quotient;
        return ans;
    }

    String[] negDiv(String quotient, String division){
        String[] ans = new String[2];
        String remainder = "11111111111111111111111111111111";
        for(int i=0; i<quotient.length(); i++){
            remainder = remainder.substring(1) + quotient.charAt(0);
            remainder = add(remainder, division);
            if(CF.equals("1") && !remainder.equals("00000000000000000000000000000000")){
                remainder = sub(division, remainder);
                quotient = quotient.substring(1) + "0";
            } else {
                quotient = quotient.substring(1) + "1";
            }
        }
        if(add(remainder, division).equals(BinaryIntegers.ZERO)){
            remainder = BinaryIntegers.ZERO;
            quotient = add("00000000000000000000000000000001", quotient);
        }

        ans[0] = remainder;
        ans[1] = quotient;
        return ans;
    }

    String Neg(String src){
        char[] binaryStr = src.toCharArray();
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
        return String.valueOf(binaryStr);
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
        this.CF = (carry == 1)? "1":"0";
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
}
