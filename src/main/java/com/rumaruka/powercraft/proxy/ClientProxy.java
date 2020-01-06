package com.rumaruka.powercraft.proxy;

import com.rumaruka.powercraft.init.PCBlocks;
import com.rumaruka.powercraft.init.PCItems;

public class ClientProxy extends CommonProxy {


    @Override
    public void renderObjects() {
        PCBlocks.renderBlock();
        PCItems.renderItem();
    }
}
