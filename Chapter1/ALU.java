package cpu.alu;

import transformer.Transformer;

import java.util.Arrays;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减与逻辑运算
 */
public class ALU {

    public static void main(String[] args){
        ALU alu = new ALU();
        Transformer transformer = new Transformer();
        String srcStr = transformer.intToBinary("1228999");
        String destStr = transformer.intToBinary("-1000000");
        //System.out.println(alu.sub(destStr, srcStr));
        System.out.println(alu.add("1100", "1100"));
    }

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    //add two integer
    String add(String src, String dest) {
        // TODO
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
        return String.valueOf(destArr);
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        // TODO
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

    String and(String src, String dest) {
        // TODO
        char[] srcArr = src.toCharArray();
        char[] destArr = dest.toCharArray();
        int len = srcArr.length;
        for(int i=0; i<len; i++){
            if(srcArr[i]=='0'){
                destArr[i] = '0';
            }
        }
        return String.valueOf(destArr);
    }

    String or(String src, String dest) {
        // TODO
        char[] srcArr = src.toCharArray();
        char[] destArr = dest.toCharArray();
        int len = srcArr.length;
        for(int i=0; i<len; i++){
            if(srcArr[i]=='1' && destArr[i]=='0'){
                destArr[i] = '1';
            }
        }
        return String.valueOf(destArr);
    }

    String xor(String src, String dest) {
        // TODO
        char[] srcArr = src.toCharArray();
        char[] destArr = dest.toCharArray();
        int len = srcArr.length;
        for(int i=0; i<len; i++){
            destArr[i] = srcArr[i]==destArr[i]? '0' : '1';
        }
        return String.valueOf(destArr);
    }

}
