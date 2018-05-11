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

package io.nuls.kernel.model;

import io.protostuff.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ln on 2018/5/5.
 */
public class CoinData extends BaseNulsData {

    @Tag(1)
    private List<Coin> from;
    @Tag(2)
    private List<Coin> to;

    public CoinData() {
        from = new ArrayList<>();
        to = new ArrayList<>();
    }

    public List<Coin> getFrom() {
        return from;
    }

    public void setFrom(List<Coin> from) {
        this.from = from;
    }

    public List<Coin> getTo() {
        return to;
    }

    public void setTo(List<Coin> to) {
        this.to = to;
    }

    /**
     * 获取该交易的手续费
     * The handling charge for the transaction.
     *
     * @return tx fee
     */
    public Na getFee() {
        Na toNa = Na.ZERO;
        for (Coin coin : to) {
            toNa = toNa.add(coin.getNa());
        }
        Na fromNa = Na.ZERO;
        for (Coin coin : from) {
            fromNa = fromNa.add(coin.getNa());
        }
        return fromNa.subtract(toNa);
    }

    public void addTo(Coin coin) {
        to.add(coin);
    }

    public void addFrom(Coin coin) {
        from.add(coin);
    }

    public Set<byte[]> getAddresses() {
        return null;
    }
}