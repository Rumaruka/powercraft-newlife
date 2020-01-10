package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.gres.PCGresBaseWithInventory;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.world.World;

public class PCPacketSetSlot extends PCPacketServerToClient {
    private int windowId;

    private int slot;

    private ItemStack itemStack;

    public PCPacketSetSlot(){

    }

    public PCPacketSetSlot(int windowId, int slot, ItemStack itemStack){
        this.windowId = windowId;
        this.slot = slot;
        this.itemStack = itemStack;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCGresBaseWithInventory.SETTING_OK = true;
        playClient.handleSetSlot(new SPacketSetSlot(this.windowId, this.slot, this.itemStack));
        PCGresBaseWithInventory.SETTING_OK = false;
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.slot = buf.readInt();
        this.itemStack = readItemStackFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.slot);
        writeItemStackToBuf(buf, this.itemStack);
    }
}
