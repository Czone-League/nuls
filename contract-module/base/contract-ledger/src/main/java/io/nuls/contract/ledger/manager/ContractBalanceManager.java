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

package io.nuls.contract.ledger.manager;

import io.nuls.account.ledger.constant.AccountLedgerErrorCode;
import io.nuls.account.model.Account;
import io.nuls.account.model.Address;
import io.nuls.account.model.Balance;
import io.nuls.account.service.AccountService;
import io.nuls.contract.constant.ContractErrorCode;
import io.nuls.contract.ledger.module.ContractBalance;
import io.nuls.contract.ledger.util.ContractLedgerUtil;
import io.nuls.contract.storage.service.ContractAddressStorageService;
import io.nuls.contract.storage.service.ContractUtxoStorageService;
import io.nuls.core.tools.crypto.Base58;
import io.nuls.core.tools.log.Log;
import io.nuls.core.tools.param.AssertUtil;
import io.nuls.db.model.Entry;
import io.nuls.db.service.BatchOperation;
import io.nuls.kernel.exception.NulsException;
import io.nuls.kernel.lite.annotation.Autowired;
import io.nuls.kernel.lite.annotation.Component;
import io.nuls.kernel.model.Coin;
import io.nuls.kernel.model.Na;
import io.nuls.kernel.model.Result;
import io.nuls.kernel.utils.AddressTool;
import sun.reflect.misc.ConstructorUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @desription:
 * @author: PierreLuo
 * @date: 2018/6/7
 */
@Component
public class ContractBalanceManager {

    @Autowired
    private ContractUtxoStorageService contractUtxoStorageService;

    @Autowired
    private ContractAddressStorageService contractAddressStorageService;

    @Autowired
    private AccountService accountService;

    private Map<String, ContractBalance> balanceMap = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    /**
     * 初始化缓存本地所有合约账户的余额信息
     */
    public void initContractBalance() {
        balanceMap.clear();
        List<Coin> coinList = new ArrayList<>();
        List<Entry<byte[], byte[]>> rawList = contractUtxoStorageService.loadAllCoinList();
        Coin coin;
        String strAddress;
        ContractBalance balance;
        byte[] fromOwner;
        for (Entry<byte[], byte[]> coinEntry : rawList) {
            coin = new Coin();
            try {
                coin.parse(coinEntry.getValue());
                strAddress = asString(coin.getOwner());
            } catch (NulsException e) {
                Log.error("parse contract coin error form db", e);
                continue;
            }
            balance = balanceMap.get(strAddress);
            if(balance == null) {
                balance = new ContractBalance();
                balanceMap.put(strAddress, balance);
            }
            if (coin.usable()) {
                balance.addUsable(coin.getNa());
            } else {
                balance.addLocked(coin.getNa());
            }
        }
    }

    private String asString(byte[] bytes) {
        AssertUtil.canNotEmpty(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public Result<List<Entry<byte[], byte[]>>> removeBalance(byte[] removeAddress) {
        lock.lock();
        try {
            if(!ContractLedgerUtil.isContractAddress(removeAddress)) {
                return Result.getFailed(ContractErrorCode.CONTRACT_ADDRESS_NOT_EXIST);
            }

            //TODO pierre 终止合约，销毁合约地址，删除合约地址UTXO
            List<Entry<byte[], byte[]>> rawList = contractUtxoStorageService.loadAllCoinList();
            Coin coin;
            byte[] address;
            String strAddress;
            List<Entry<byte[], byte[]>> deleteList = new ArrayList<>();
            BatchOperation batchOperation = contractUtxoStorageService.createBatchOperation();
            for (Entry<byte[], byte[]> coinEntry : rawList) {
                coin = new Coin();
                try {
                    coin.parse(coinEntry.getValue());
                } catch (NulsException e) {
                    Log.error("parse contract coin error form db", e);
                    continue;
                }
                address = coin.getOwner();
                if(!Arrays.equals(removeAddress, address)) {
                    continue;
                }
                batchOperation.delete(coinEntry.getKey());
                deleteList.add(coinEntry);
            }
            Result result = batchOperation.executeBatch();
            if(result.isSuccess()) {
                balanceMap.remove(asString(removeAddress));
            }
            return Result.getSuccess().setData(deleteList);
        } finally {
            lock.unlock();
        }
    }
    /**
     * 获取账户余额
     *
     * @param address
     * @return
     */
    public Result<ContractBalance> getBalance(byte[] address) {
        lock.lock();
        try {
            if (address == null || address.length != AddressTool.HASH_LENGTH) {
                return Result.getFailed(ContractErrorCode.PARAMETER_ERROR);
            }

            if (!ContractLedgerUtil.isContractAddress(address)) {
                return Result.getFailed(ContractErrorCode.CONTRACT_ADDRESS_NOT_EXIST);
            }

            String addressKey = asString(address);
            ContractBalance balance = balanceMap.get(addressKey);
            if (balance == null) {
                balance = new ContractBalance();
                balanceMap.put(addressKey, balance);
            }
            return Result.getSuccess().setData(balance);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 刷新余额
     *
     * @param address
     */
    public void refreshBalance(List<Entry<byte[], byte[]>> addUtxoList, List<Entry<byte[], byte[]>> deleteUtxoList) {
        lock.lock();
        try {
            Coin coin;
            ContractBalance balance;
            String strAddress;
            if(addUtxoList != null) {
                for (Entry<byte[], byte[]> addUtxo : addUtxoList) {
                    coin = new Coin();
                    try {
                        coin.parse(addUtxo.getValue());
                    } catch (NulsException e) {
                        Log.error("parse contract coin error form db", e);
                        continue;
                    }
                    strAddress = asString(coin.getOwner());
                    balance = balanceMap.get(strAddress);
                    if(balance == null) {
                        balance = new ContractBalance();
                        balanceMap.put(strAddress, balance);
                    }
                    if (coin.usable()) {
                        balance.addUsable(coin.getNa());
                    } else {
                        //TODO pierre 合约地址是否存在锁定金额
                        balance.addLocked(coin.getNa());
                    }
                }
            }

            if(deleteUtxoList != null) {
                for (Entry<byte[], byte[]> deleteUtxo : deleteUtxoList) {
                    coin = new Coin();
                    try {
                        coin.parse(deleteUtxo.getValue());
                    } catch (NulsException e) {
                        Log.error("parse contract coin error form db", e);
                        continue;
                    }
                    strAddress = asString(coin.getOwner());
                    balance = balanceMap.get(strAddress);
                    if(balance == null) {
                        balance = new ContractBalance();
                        balanceMap.put(strAddress, balance);
                    }
                    if (coin.usable()) {
                        balance.minusUsable(coin.getNa());
                    } else {
                        //TODO pierre 合约地址是否存在锁定金额
                        balance.minusLocked(coin.getNa());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public List<Coin> getCoinListByAddress(byte[] address) {
        List<Coin> coinList = new ArrayList<>();
        List<Entry<byte[], byte[]>> rawList = contractUtxoStorageService.loadAllCoinList();
        for (Entry<byte[], byte[]> coinEntry : rawList) {
            Coin coin = new Coin();
            try {
                coin.parse(coinEntry.getValue());
            } catch (NulsException e) {
                Log.info("parse coin form db error");
                continue;
            }
            if (Arrays.equals(coin.getOwner(), address)) {
                coin.setOwner(coinEntry.getKey());
                coinList.add(coin);
            }
        }
        return coinList;
    }
}
