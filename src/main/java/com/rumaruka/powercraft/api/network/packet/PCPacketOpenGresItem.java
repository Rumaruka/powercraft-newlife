package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PCPacketOpenGresItem extends PCPacketServerToClient {
    private int itemID;
    private int windowId;
    private NBTTagCompound nbtTagCompound;

    public PCPacketOpenGresItem(){

    }

    public PCPacketOpenGresItem(Item item, int windowId, NBTTagCompound nbtTagCompound) {
        this.itemID = Item.getIdFromItem(item);
        this.windowId = windowId;
        this.nbtTagCompound = nbtTagCompound;
    }
    @Override
    @SideOnly(Side.CLIENT)
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        Item item = Item.getItemById(this.itemID);
        PCGres.openClientGui(player, item, this.windowId, this.nbtTagCompound);
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.itemID = buf.readInt();
        this.nbtTagCompound = readNBTFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.itemID);
        writeNBTToBuf( this.nbtTagCompound,buf);
    }
}
