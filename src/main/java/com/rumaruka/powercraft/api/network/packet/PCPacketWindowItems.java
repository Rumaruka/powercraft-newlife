package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.gres.PCGresBaseWithInventory;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PCPacketWindowItems extends PCPacketServerToClient {
    private int windowId;

    private NonNullList<ItemStack> itemStacks;

    public PCPacketWindowItems(){

    }
    public PCPacketWindowItems(int windowId, NonNullList<ItemStack> itemStacks){
        this.windowId = windowId;
        this.itemStacks = itemStacks;
    }

    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCGresBaseWithInventory.SETTING_OK = true;
        playClient.handleWindowItems(new SPacketWindowItems(this.windowId, this.itemStacks));
        PCGresBaseWithInventory.SETTING_OK = false;
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        int size = buf.readInt();
        this.itemStacks = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
        for(int i=0; i<size; i++){
            this.itemStacks.add(readItemStackFromBuf(buf));
        }
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.itemStacks.size());
        for(ItemStack itemStack:this.itemStacks){
            writeItemStackToBuf(buf, itemStack);
        }
    }
}
