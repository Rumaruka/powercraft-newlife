package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PCPacketOpenGresTileEntity extends PCPacketServerToClient {
    private int x, y, z;
    private int windowId;
    private long session;
    private NBTTagCompound nbtTagCompound;

    public PCPacketOpenGresTileEntity(){

    }

    public PCPacketOpenGresTileEntity(PCTileEntityAPI tileEntity, int windowId, long session, NBTTagCompound nbtTagCompound){
        this.x = tileEntity.getPos().getX();
        this.y = tileEntity.getPos().getY();
        this.z = tileEntity.getPos().getZ();
        this.windowId = windowId;
        this.session = session;
        this.nbtTagCompound = nbtTagCompound;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCTileEntityAPI te = PCUtils.getTileEntity(world, this.x, this.y, this.z, PCTileEntityAPI.class);
        if(te!=null){
            te.setSession(this.session);
            PCGres.openClientGui(player, te, this.windowId, this.nbtTagCompound);
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.session = buf.readLong();
        this.nbtTagCompound = readNBTFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeLong(this.session);
        writeNBTToBuf( this.nbtTagCompound,buf);
    }
}
