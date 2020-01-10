package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PCPacketTileEntityMessageCTS extends PCPacketServerToClient {
    private int x;
    private int y;
    private int z;
    private NBTTagCompound nbtTagCompound;
    private long session;

    public PCPacketTileEntityMessageCTS(){

    }

    public PCPacketTileEntityMessageCTS(PCTileEntityAPI te, NBTTagCompound nbtTagCompound, long session){
        this.x = te.getPos().getX();
        this.y = te.getPos().getY();
        this.z = te.getPos().getZ();

        this.nbtTagCompound = nbtTagCompound;
        this.session = session;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCTileEntityAPI te = PCUtils.getTileEntity(world, this.x, this.y, this.z, PCTileEntityAPI.class);
        if(te!=null){
            te.onClientMessageCheck(player, this.nbtTagCompound, this.session, false);
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.nbtTagCompound = readNBTFromBuf(buf);
        this.session = buf.readLong();
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        writeNBTToBuf(this.nbtTagCompound,buf);
        buf.writeLong(this.session);
    }
}
