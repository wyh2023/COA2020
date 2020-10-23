package cpu.alu;

import transformer.Transformer;

import java.security.AlgorithmConstraints;
import java.util.Arrays;

public class NBCDU {

	// 模拟寄存器中的进位标志位
	private String CF = "0";

	// 模拟寄存器中的溢出标志位
	private String OF = "0";

	private ALU alu = new ALU();

	public static void main(String[] args){
	    NBCDU nbcdu = new NBCDU();
        Transformer transformer = new Transformer();
	    String a = transformer.decimalToNBCD("88");    //88
	    String b = transformer.decimalToNBCD("89");    //89
	    System.out.println(nbcdu.sub(a, b));
	    System.out.println(transformer.decimalToNBCD("1"));
    }

	/**
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return a + b
	 */
	String add(String a, String b) {
        // TODO
	    String[] aStr = cutStr(a);
	    String[] bStr = cutStr(b);

	    String aSign = aStr[0];
	    String bSign = bStr[0];
	    if(!aSign.equals(bSign)){
	        if(aSign.equals("1100")){
	            b = b.replace("1101", "1100");
	            return sub(b, a);
            } else {
	            a = a.replace("1101", "1100");
	            return sub(a, b);
            }
        }
	    String tmp;
	    StringBuilder ret = new StringBuilder();
	    int carry = 0;
        for(int i=7; i>0; i--){
            tmp = alu.add(aStr[i], bStr[i]);
            if(carry == 1){
                tmp = alu.add(tmp, "0001");
            }
            if((aStr[i].charAt(0) == '1' || bStr[i].charAt(0) == '1')&&(tmp.charAt(0) == '0')
                    || valOfNBCD(tmp)>9){   // 1001 + 1000 = 0001 ? check overflow!
                carry = 1;
                tmp = alu.add(tmp, "0110");
            } else {
                carry = 0;
            }
            ret.insert(0, tmp);
        }
        ret.insert(0, aSign);
		return ret.toString();
	}

	/***
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return b - a
	 */
	String sub(String a, String b) {
		// TODO
        String[] aStr = cutStr(a);
        String[] bStr = cutStr(b);

        String aSign = aStr[0];
        String bSign = bStr[0];
        if(!aSign.equals(bSign)){
            if(aSign.equals("1100")){
                a = a.replace("1100", "1101");
                return add(a, b);
            } else {
                a = a.replace("1101", "1100");
                return add(a, b);
            }
        }

        String tmpStr;
        String[] ans = new String[8];
        StringBuilder nTmp = new StringBuilder();
        int i=7;
        int count = 0;
        int carry = 0;
        for(; i>0; i--){
            if(aStr[i].equals("0000") && bStr[i].equals("0000")) break;
            tmpStr = alu.sub(aStr[i],"1001");
            if(i == 7) tmpStr = alu.add(tmpStr, "0001");   //借位
            String tmp = tmpStr;
            tmpStr = alu.add(tmpStr, bStr[i]);
            if(carry == 1){
                tmpStr = alu.add(tmpStr, "0001");
            }
            if((tmp.charAt(0) == '1' || bStr[i].charAt(0) == '1')&&(tmpStr.charAt(0) == '0') ||
                    valOfNBCD(tmpStr) > 9){      //carry和overflow还没判断呢！
                carry = 1;
                tmpStr = alu.add(tmpStr, "0110");
            } else {
                carry = 0;
            }
            count++;
            ans[i] = tmpStr;
        }
        while (i>0){
            ans[i] = "0000";
            i--;
        }
        int flg = 0;
        if(carry == 0){
            for(int j=7; j>7-count; j--){
                if(ans[j].equals("0000")) continue;
                if(flg == 0){
                    ans[j] = alu.sub(ans[j], "1010");
                    flg = 1;
                } else {
                    ans[j] = alu.sub(ans[j], "1001");
                }
            }
        }
        for(int j=7; j>0; j--){
            nTmp.insert(0, ans[j]);
        }
        String ret = nTmp.toString();
        if (ret.equals("0000000000000000000000000000")) { //之前判断"0"有问题；
            return "1100" + ret;
        }
        if(aSign.equals("1100")){
            return ((carry==1)? "1100":"1101") + ret;
        } else {
            return ((carry==1)? "1101":"1100") + ret;
        }
	}

	static String[] cutStr(String str){
	    String[] strArr = new String[8];
	    for(int i=0; i<8; i++){
	        strArr[i] = str.substring(4*i, 4*(i + 1));
        }
	    return strArr;
    }

    static int valOfNBCD(String a){
	    char[] aLst = a.toCharArray();
	    int ret = 0;
	    for(int i=0; i<4; i++){
	        ret <<= 1;
	        ret += (aLst[i]=='1')? 1:0;
        }
	    return ret;
    }

}
