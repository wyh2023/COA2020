package memory.cacheWriteStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.MappingStrategy;

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-12 11:38
 */
public abstract class WriteStrategy {
    MappingStrategy mappingStrategy;
    /**
     * 将数据写入Cache，并且根据策略选择是否修改内存
     * @param rowNo 行号
     * @param input  数据
     * @return
     */
    public void write(int rowNo, char[] input) {
        //TODO
        Cache.getCache().setDirty(rowNo, true);
        Cache.getCache().setData(rowNo, input);
    }


    /**
     * 修改内存
     * @return
     */
    public void writeBack(int rowNo) {
        //TODO
        Cache cache = Cache.getCache();
        String pAddr = mappingStrategy.getPAddr(rowNo);
        Memory.getMemory().writeBack(pAddr, cache.getData(rowNo));
    }

    public void setMappingStrategy(MappingStrategy mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
    }

    public abstract Boolean isWriteBack();
}
