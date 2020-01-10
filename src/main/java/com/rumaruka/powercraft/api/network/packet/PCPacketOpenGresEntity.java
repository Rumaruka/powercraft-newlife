package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PCPacketOpenGresEntity extends PCPacketServerToClient {
    private int entityID;
    private int windowId;
    private long session;
    private NBTTagCompound nbtTagCompound;

    public PCPacketOpenGresEntity(){

    }

    public PCPacketOpenGresEntity(IEntityPC entity, int windowId, long session, NBTTagCompound nbtTagCompound){
        this.entityID = entity.getEntityId();
        this.windowId = windowId;
        this.session = session;
        this.nbtTagCompound = nbtTagCompound;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        IEntityPC entity = PCUtils.getEntity(world, this.entityID, IEntityPC.class);
        if(entity!=null){
            entity.setSession(this.session);
            PCGres.openClientGui(player, entity, this.windowId, this.nbtTagCompound);
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.entityID = buf.readInt();
        this.session = buf.readLong();
        this.nbtTagCompound = readNBTFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.entityID);
        buf.writeLong(this.session);
        writeNBTToBuf( this.nbtTagCompound,buf);
    }
}
