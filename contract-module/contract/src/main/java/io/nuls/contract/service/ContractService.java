/**
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
 */
package io.nuls.contract.service;

import io.nuls.contract.dto.ContractResult;
import io.nuls.contract.entity.txdata.CallContractData;
import io.nuls.contract.entity.txdata.CreateContractData;
import io.nuls.contract.entity.txdata.DeleteContractData;
import io.nuls.kernel.model.Result;

/**
 * @desription:
 * @author: PierreLuo
 * @date: 2018/5/5
 */
public interface ContractService {

    /**
     * @param number 当前块编号
     * @param prevStateRoot 上一区块状态根
     * @param create 创建智能合约的参数
     * @return
     */
    Result<ContractResult> createContract(long number, byte[] prevStateRoot, CreateContractData create);

    /**
     * @param number 当前块编号
     * @param prevStateRoot 上一区块状态根
     * @param call 调用智能合约的参数
     * @return
     */
    Result<ContractResult> callContract(long number, byte[] prevStateRoot, CallContractData call);

    /**
     * @param number 当前块编号
     * @param prevStateRoot 上一区块状态根
     * @param delete 删除智能合约的参数
     * @return
     */
    Result<ContractResult> deleteContract(long number, byte[] prevStateRoot, DeleteContractData delete);

    /**
     * @param address
     * @return
     */
    Result<Object> getContractInfo(String address);

    /**
     * @return
     */
    Result<Object> getVmStatus();

    Result<Boolean> isContractAddress(byte[] fromAddressBytes);
}
