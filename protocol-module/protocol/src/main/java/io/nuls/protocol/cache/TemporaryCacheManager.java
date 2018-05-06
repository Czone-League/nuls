/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.nuls.protocol.cache;

import io.nuls.cache.CacheMap;
import io.nuls.kernel.model.Transaction;
import io.nuls.protocol.model.SmallBlock;

/**
 * Used for sharing temporary data between multiple hander.
 *
 * @author Niels
 * @date 2017/12/12
 */
public class TemporaryCacheManager {
    private static final TemporaryCacheManager INSTANCE = new TemporaryCacheManager();

    private CacheMap<String, SmallBlock> smallBlockCacheMap = new CacheMap<>("temp-small-block-cache", 16, 1000, 0);
    private CacheMap<String, Transaction> txCacheMap = new CacheMap<>("temp-tx-cache", 64, 3600, 0);

    private TemporaryCacheManager() {
    }

    public static TemporaryCacheManager getInstance() {
        return INSTANCE;
    }

    public void cacheSmallBlock(SmallBlock newBlock) {
        smallBlockCacheMap.put(newBlock.getHeader().getHash().getDigestHex(), newBlock);
    }

    public SmallBlock getSmallBlock(String hash) {
        if (null == smallBlockCacheMap) {
            return null;
        }
        return smallBlockCacheMap.get(hash);
    }

    public void cacheTx(Transaction tx) {
        txCacheMap.put(tx.getHash().getDigestHex(), tx);
    }

    public Transaction getTx(String hash) {
        if (null == txCacheMap) {
            return null;
        }
        return txCacheMap.get(hash);
    }

    public void remove(String hash) {
        if (null == smallBlockCacheMap) {
            return;
        }
        smallBlockCacheMap.remove(hash);
    }


    public void clear() {
        this.smallBlockCacheMap.clear();
    }

    public void destroy() {
        this.smallBlockCacheMap.destroy();
    }

}
