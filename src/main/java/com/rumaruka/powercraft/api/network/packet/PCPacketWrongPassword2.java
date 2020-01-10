package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PCPacketWrongPassword2 extends PCPacketServerToClient {

    private int entityID;

    public PCPacketWrongPassword2(){

    }

    public PCPacketWrongPassword2(IEntityPC entity){
        this.entityID = entity.getEntityId();
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        IEntityPC entity = PCUtils.getEntity(world, this.entityID, IEntityPC.class);
        if(entity!=null){
            entity.wrongPasswordInput();
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.entityID = buf.readInt();
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.entityID);
    }
}
