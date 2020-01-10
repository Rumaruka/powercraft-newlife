package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PCPacketOpenGresHandler extends PCPacketServerToClient {

    private String guiOpenHandlerName;
    private int windowId;
    private NBTTagCompound nbtTagCompound;

    public PCPacketOpenGresHandler(){

    }

    public PCPacketOpenGresHandler(String guiOpenHandlerName, int windowId, NBTTagCompound nbtTagCompound) {
        this.guiOpenHandlerName = guiOpenHandlerName;
        this.windowId = windowId;
        this.nbtTagCompound = nbtTagCompound;
    }


    @Override
    @SideOnly(Side.CLIENT)
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCGres.openClientGui(player, this.guiOpenHandlerName, this.windowId, this.nbtTagCompound);
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.guiOpenHandlerName = readStringFromBuf(buf);
        this.nbtTagCompound = readNBTFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        writeStringToBuf(buf, this.guiOpenHandlerName);
        writeNBTToBuf(this.nbtTagCompound,buf);
    }
}
