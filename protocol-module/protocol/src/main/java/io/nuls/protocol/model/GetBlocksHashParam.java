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

package io.nuls.protocol.model;

import io.nuls.kernel.model.BaseNulsData;
import io.protostuff.Tag;

/**
 * 请求区块摘要的数据封装
 * Request block hashes data encapsulation.
 *
 * @author Niels
 * @date 2018/2/8
 */
public class GetBlocksHashParam extends BaseNulsData {

    /**
     * 请求的起始高度，即需要返回的第一个高度
     * The starting height of the request is the first height to be returned.
     */
    @Tag(1)
    private long start;

    /**
     * 请求的区块数量
     * the count of the blocks request
     */
    @Tag(2)
    private long size;

    public GetBlocksHashParam() {
    }

    /**
     * 请求的起始高度，即需要返回的第一个高度
     * The starting height of the request is the first height to be returned.
     */
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    /**
     * 请求的区块数量
     * the count of the blocks request
     */
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
