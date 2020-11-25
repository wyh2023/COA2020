package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

import java.lang.reflect.Member;

public class SetAssociativeMapping extends MappingStrategy{


    public static void main(String[] args){
        SetAssociativeMapping setAssociativeMapping = new SetAssociativeMapping();
        System.out.println(setAssociativeMapping.countSets(2));
    }


    Transformer t = new Transformer();
    private int SETS=1; // 共256个组
    private int setSize=1024;   // 每个组4行


    /**
     * 该方法会被用于测试，请勿修改
     * @param SETS
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     * @param setSize
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    /**
     *
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */

    @Override
    public char[] getTag(int blockNO) {
        //TODO
        char[] ret = new char[22];
        String tag = t.intToBinary(String.valueOf(blockNO)).substring(10);
        for(int i=0; i<22; i++){
            if(i < 22-countSets(SETS)){
                ret[i] = tag.charAt(i);
            } else {
                ret[i] = '0';
            }
        }
        return ret;
    }

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        //TODO
        int setNO = blockNO % SETS;
        int start = setNO * setSize;
        int end = (setNO + 1)*setSize;
        char[] tag = getTag(blockNO);
        return replacementStrategy.isHit(start, end, tag);
    }

    @Override
    public int writeCache(int blockNO) {
        //TODO
        int setNO = blockNO % SETS;
        int start = setNO * setSize;
        int end = (setNO + 1)*setSize;
        char[] tag = getTag(blockNO);
        char[] memory =Memory.getMemory().read(t.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B);
        return replacementStrategy.Replace(start, end, tag, memory);
    }

    @Override
    public String getPAddr(int rowNo) {
        //TODO
        //
        int setNum = rowNo / setSize;
        String setStr = new Transformer().intToBinary(String.valueOf(setNum)).substring(32-countSets(SETS));
        char[] tag = Cache.getCache().getTag(rowNo);
        String tagStr = String.valueOf(tag).substring(0, 22-countSets(SETS));
        return tagStr + setStr + "0000000000";
    }

    public int countSets(int SETS){
        int ret = 0;
        SETS >>= 1;
        while(SETS != 0){
            ret++;
            SETS >>= 1;
        }
        return ret;
    }

}










