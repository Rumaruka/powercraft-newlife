package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.entity.IEntityPC;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PCPacketPasswordReply2 extends PCPacketServerToClient {
    private int entityID;
    private String password;

    public PCPacketPasswordReply2(){

    }

    public PCPacketPasswordReply2(IEntityPC entity, String password){
        this.entityID = entity.getEntityId();
        this.password = password;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        IEntityPC entity = PCUtils.getEntity(world, this.entityID, IEntityPC.class);
        if(entity!=null){
            if(!entity.guiOpenPasswordReply(player, this.password)){
                return new PCPacketWrongPassword2(entity);
            }
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.password = readStringFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.entityID);
        writeStringToBuf(buf, this.password);
    }
}
