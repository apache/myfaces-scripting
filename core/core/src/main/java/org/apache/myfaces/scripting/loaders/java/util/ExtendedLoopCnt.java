package org.apache.myfaces.scripting.loaders.java.util;

/**
 * helper class which allows to deal with loops which have to keep track
 * of two running loop variables one being the total counter
 * oune being a counter which is modulated
 */
public class ExtendedLoopCnt {
    int _cnt = 0;
    int _totalCnt = 0;
    int _delimiter = 0;

    public ExtendedLoopCnt(int cnt, int totalCnt, int cntDelimiter) {
        _cnt = cnt;
        _totalCnt = totalCnt;
        _delimiter = cntDelimiter;
    }

    public final int getCnt() {
        return _cnt;
    }

    public final int getTotalCnt() {
        return _totalCnt;
    }

    public final int incCnt() {
        _cnt = (_cnt + 1) % _delimiter;
        return _cnt;
    }

    public final int incTotalCnt() {
        _totalCnt++;
        return _totalCnt;
    }
}
