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
package io.nuls.protocol.service;

import io.nuls.kernel.exception.NulsException;
import io.nuls.kernel.model.Block;
import io.nuls.kernel.model.BlockHeader;
import io.nuls.kernel.model.NulsDigestData;
import io.nuls.kernel.model.Result;
import io.nuls.network.entity.Node;

/**
 * @author Niels
 * @date 2017/11/10
 */
public interface BlockService {

    Block getGengsisBlock();

    Block getBestBlock();

    BlockHeader getBestBlockHeader();

    BlockHeader getBlockHeader(long height) throws NulsException;

    BlockHeader getBlockHeader(NulsDigestData hash) throws NulsException;

    Block getBlock(NulsDigestData hash);

    Block getBlock(long height);

    boolean saveBlock(Block block) throws NulsException;

    boolean rollbackBlock(Block block) throws NulsException;

    Result forwardBlock(Block block, Node excludeNode);

    Result broadcastBlock(Block block, Node excludeNode);
}
