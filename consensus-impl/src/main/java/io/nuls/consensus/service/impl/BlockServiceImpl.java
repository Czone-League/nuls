package io.nuls.consensus.service.impl;

import io.nuls.consensus.service.intf.BlockService;
import io.nuls.core.chain.entity.Block;

/**
 * @author Niels
 * @date 2017/12/11
 */
public class BlockServiceImpl implements BlockService {
    private static final BlockServiceImpl INSTANCE = new BlockServiceImpl();

    private BlockServiceImpl() {
    }

    public static BlockServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Block getGengsisBlockFromDb() {
        // todo auto-generated method stub(niels)
        return null;
    }

    @Override
    public long getLocalHeight() {
        // todo auto-generated method stub(niels)
        return 0;
    }

    @Override
    public byte[] getLocalHighestHash() {
        // todo auto-generated method stub(niels)
        return new byte[0];
    }

    @Override
    public long getBestHeight() {
        // todo auto-generated method stub(niels)
        return 0;
    }

    @Override
    public byte[] getBestHash() {
        // todo auto-generated method stub(niels)
        return new byte[0];
    }

    @Override
    public Block getLocalHighestBlock() {
        // todo auto-generated method stub(niels)
        return null;
    }

    @Override
    public Block getBestBlock() {
        // todo auto-generated method stub(niels)
        return null;
    }

    @Override
    public Block getBlockByHash(String hash) {
        // todo auto-generated method stub(niels)
        return null;
    }

    @Override
    public Block getBlockByHeight(long height) {
        // todo auto-generated method stub(niels)
        return null;
    }


    @Override
    public void save(Block block) {
        // todo auto-generated method stub(niels)

    }

    @Override
    public void clearLocalBlocks() {
        // todo auto-generated method stub(niels)

    }

    @Override
    public void rollback(long height) {
        // todo auto-generated method stub(niels)
        //删除关联数据及区块数据
    }
}
