/*
 * *
 *  * MIT License
 *  *
 *  * Copyright (c) 2017-2018 nuls.io
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */
package io.nuls.consensus.poc.model;

import io.nuls.kernel.model.BaseNulsData;
import io.nuls.protocol.constant.ProtocolConstant;
import io.protostuff.Tag;

/**
 * @author Niels
 * @date 2017/12/25
 */
public class BlockRoundData extends BaseNulsData {

    @Tag(1)
    protected long roundIndex;
    @Tag(2)
    protected int consensusMemberCount;
    @Tag(3)
    protected long roundStartTime;
    @Tag(4)
    protected int packingIndexOfRound;

    public long getRoundEndTime() {
        return roundStartTime + consensusMemberCount * ProtocolConstant.BLOCK_TIME_INTERVAL_SECOND * 1000L;
    }

    public BlockRoundData() {
    }

    public BlockRoundData(byte[] extend) {
        this.parse(extend);
    }

    public int getConsensusMemberCount() {
        return consensusMemberCount;
    }

    public void setConsensusMemberCount(int consensusMemberCount) {
        this.consensusMemberCount = consensusMemberCount;
    }

    public long getRoundStartTime() {
        return roundStartTime;
    }

    public void setRoundStartTime(long roundStartTime) {
        this.roundStartTime = roundStartTime;
    }

    public int getPackingIndexOfRound() {
        return packingIndexOfRound;
    }

    public void setPackingIndexOfRound(int packingIndexOfRound) {
        this.packingIndexOfRound = packingIndexOfRound;
    }

    public long getRoundIndex() {
        return roundIndex;
    }

    public void setRoundIndex(long roundIndex) {
        this.roundIndex = roundIndex;
    }
}
