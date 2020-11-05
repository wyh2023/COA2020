package cpu.alu;

import transformer.Transformer;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 取模、逻辑/算术/循环左右移
 */
public class ALU {

    public static void main(String[] args){
        ALU alu = new ALU();
        Transformer transformer = new Transformer();
        String a = transformer.intToBinary("2147483394");
        String b = transformer.intToBinary("4");
        int c = Integer.parseInt(transformer.binaryToInt("11111111111111111111111111111001"));
        System.out.println(alu.imod("11111111111111111111111111111001", "11111111111111111111111111110011"));
        System.out.println(-4 % 3);
    }

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    private Transformer transformer = new Transformer();

    //signed integer mod
    String imod(String src, String dest) {
        // TODO
        if(src.charAt(0) == '1') src = Neg(src);
        if(dest.charAt(0)=='0')
            return posDiv(dest, src);
        else
            return negDiv(dest, src);
    }

    String posDiv(String quotient, String division){
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
        return remainder;
    }

    String negDiv(String quotient, String division){
        String remainder = "11111111111111111111111111111111";
        for(int i=0; i<quotient.length(); i++){
            remainder = remainder.substring(1) + quotient.charAt(0);
            remainder = add(remainder, division);
            if(CF.equals("1")){
                remainder = sub(division, remainder);
                quotient = quotient.substring(1) + "0";
            } else {
                quotient = quotient.substring(1) + "1";
            }
        }
        return remainder;
    }

    String shl(String src, String dest) {
        // TODO
        System.out.println("shl!");
        return leftShift(src, dest);
    }

    String shr(String src, String dest) {
        // TODO
        StringBuilder ansBuilder = new StringBuilder();
        ansBuilder.append(dest);

        System.out.println("shr!");
        String srcInt = transformer.binaryToInt(src);
        int shift = Integer.parseInt(srcInt);
        shift %= 32;
        for(int i=0; i<shift; i++){
            ansBuilder.insert(0, "0");
        }
        return ansBuilder.substring(0, 32);
    }

    String sal(String src, String dest) {
        // TODO
        System.out.println("sal!");
        return leftShift(src, dest);
    }

    String sar(String src, String dest) {
        // TODO
        StringBuilder ansBuilder = new StringBuilder();
        ansBuilder.append(dest);

        System.out.println("sar!");
        String srcInt = transformer.binaryToInt(src);
        int shift = Integer.parseInt(srcInt);
        shift %= 32;
        for(int i=0; i<shift; i++){
            String ins = ansBuilder.substring(0, 1);
            ansBuilder.insert(0, ins);
        }
        return ansBuilder.substring(0, 32);
    }

    String ror(String src, String dest) {
        // TODO
        System.out.println("rol!");
        String srcInt = transformer.binaryToInt(src);
        int shift = Integer.parseInt(srcInt);
        shift %= 32;
        int end = 32 - shift;
        return  dest.substring(end, 32) + dest.substring(0, end);
    }

    String rol(String src, String dest) {
        // TODO
        System.out.println("ror!");
        String srcInt = transformer.binaryToInt(src);
        int shift = Integer.parseInt(srcInt);
        shift %= 32;
        return dest.substring(shift) + dest.substring(0, shift);
    }

    String leftShift(String src, String dest){
        StringBuilder ansBuilder = new StringBuilder();
        ansBuilder.append(dest);

        String srcInt = transformer.binaryToInt(src);
        int shift = Integer.parseInt(srcInt);
        shift %= 32;
        for(int i=0; i<shift; i++){
            ansBuilder.append(0);
        }
        return ansBuilder.substring(shift, shift+32);
    }

    //add two integer
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

    //sub two integer
    // dest - src
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
}
