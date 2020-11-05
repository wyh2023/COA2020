package cpu.alu;

import transformer.Transformer;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    private ALU alu = new ALU();

    public static void main(String[] args){
        FPU fpu = new FPU();
        Transformer transformer = new Transformer();
        System.out.println(transformer.binaryToFloat("11000000010010001111010111000010"));
        System.out.println(transformer.binaryToFloat("01000001001101010000000000000000"));
        String a = transformer.floatToBinary("-3.14");
        System.out.println(a);
        String b = transformer.floatToBinary("11.3125");
        System.out.println(fpu.add(a, b));
        float test = (float) (11.3125-3.14);
        System.out.println(test);
        System.out.println(transformer.floatToBinary("8.1725"));
    }


            //00111110100000000000000000000000
    /**
     * compute the float add of (a + b)
     **/
    String add(String a,String b){
        // TODO
        if(a.startsWith("0000000000000000000000000000000", 1)) return b;
        if(b.startsWith("0000000000000000000000000000000", 1)) return a;

        String aExp = a.substring(1, 9);
        String bExp = b.substring(1, 9);

        char aSign = a.charAt(0);
        char bSign = b.charAt(0);

        String aFriction = a.substring(9);
        String bFriction = b.substring(9);

        int expA = unsignedVal(aExp);
        int expB = unsignedVal(bExp);
        aFriction = (expA == 0)? ("0"+aFriction):("1"+aFriction);
        bFriction = (expB == 0)? ("0"+bFriction):("1"+bFriction);
        aFriction = aFriction + "0000"; //guardBits!
        bFriction = bFriction + "0000"; //guardBits!

        int flg = 0;

        while (expA < expB){
            flg = 1;
            aExp = alu.add("00000001", aExp);
            expA++;
            aFriction = "0" + aFriction;
        }
        while (expB < expA){
            flg = 2;
            bExp = alu.add("00000001", bExp);
            expB++;
            bFriction = "0" + bFriction;
        }

        if(flg == 1){
            if(aFriction.startsWith("00000000000000000000000", 1)) return b;
        } else if(flg == 2){
            if(bFriction.startsWith("00000000000000000000000", 1)) return a;
        }

        aFriction = aFriction.substring(0, 28);
        bFriction = bFriction.substring(0, 28);

        //Sign them!
        aFriction = "00" + aFriction;
        bFriction = "00" + bFriction;
        StringBuilder ansBuilder = new StringBuilder();
        String ansFriction;
        if(aSign == bSign){
            ansFriction = alu.add(aFriction, bFriction);
        } else {
            ansFriction = alu.sub(bFriction, aFriction); //b+ a-
        }

        if(ansFriction.equals("000000000000000000000000000000")){
            return "00000000000000000000000000000000";
        }

        if(aSign == bSign){
            ansBuilder.append(aSign);
        } else {
            if(ansFriction.charAt(0)=='1'){
                //a + b
                if(aSign == '1'){
                    ansBuilder.append("0");
                } else {
                    ansBuilder.append("1");
                }
                ansFriction = Neg(ansFriction);
            } else {
                ansBuilder.append(aSign);
            }
        }

        /*
        if(unsignedVal(ansFriction.substring(26, 30))>7){
            ansFriction = alu.add(ansFriction, "000000000000000000000000001000");
        }
         */
        if(ansFriction.charAt(1)=='1'){
            if(aExp.equals("11111110")){
                ansBuilder.append("1111111100000000000000000000000");
                return ansBuilder.toString();
            }
            aExp = alu.add(aExp, "00000001");
            ansFriction = ansFriction.substring(1, 29);
        } else {
            ansFriction = ansFriction.substring(2, 30);
        }

        //normalize

        while (ansFriction.charAt(0) == '0'){
            aExp = alu.sub("00000001", aExp);
            ansFriction = ansFriction + "0";
            ansFriction = ansFriction.substring(1, 28);
        }
        ansFriction = ansFriction.substring(0, 24);
        ansBuilder.append(aExp);
        ansBuilder.append(ansFriction, 1, 24);

 		return ansBuilder.toString();
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a,String b){
        // TODO
        b = (b.charAt(0)=='0')?
                ("1".concat(b.substring(1))) : ("0".concat(b.substring(1)));
		return add(a, b);
    }

    int unsignedVal(String str){
        int ret = 0;
        int len = str.length();
        for(int i=len-1; i>=0; i--){
            if (str.charAt(i) == '1') {
                ret += (1<<(len-1-i));
            }
        }
        return ret;
    }

    String Neg(String src){
        char[] binaryStr = src.toCharArray();
        int index = src.length()-1;
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
