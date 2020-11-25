package memory.cacheReplacementStrategy;

import memory.Cache;
import memory.cacheWriteStrategy.WriteBackStrategy;
import memory.cacheWriteStrategy.WriteStrategy;

import java.util.Arrays;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] addrTag) {
        //TODO
        for(int i=start; i<end; i++){
            if (Cache.getCache().isMatch(i, addrTag))
                return i;
        }
        return -1;
    }

    @Override
    public int Replace(int start, int end, char[] addrTag, char[] input) {
        //TODO
        //find free
        int index = 0;
        boolean isFull = true;
        for(int i=start; i<end; i++){
            if(Cache.getCache().getTimeStamp(i) == -1L){
                index = i;
                isFull = false;
                break;
            }
        }
        for(int i=start; i<end; i++){
            if(!Cache.getCache().getValid(i)){
                index = i;
                isFull = false;
                break;
            }
        }
        //Sort
        if(isFull){
            long maxTime = Cache.getCache().getTimeStamp(start);
            index = start;
            for(int i=start; i<end; i++){
                if(maxTime < Cache.getCache().getTimeStamp(i)){
                    maxTime = Cache.getCache().getTimeStamp(i);
                    index = i;
                }
            }
        }
        for(int i=start; i<end; i++){
            long time = Cache.getCache().getTimeStamp(i);
            if(time >= 0L){
                Cache.getCache().incTimeStamp(i);
            } else {
                break;
            }
        }
        if(Cache.getCache().getTimeStamp(index)==-1L){
            Cache.getCache().incTimeStamp(index);
        }
        Cache.getCache().updateCacheLine(index, addrTag, input);
        return index;
    }


}
