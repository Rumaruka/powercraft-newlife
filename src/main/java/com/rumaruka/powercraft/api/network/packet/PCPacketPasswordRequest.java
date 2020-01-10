package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PCPacketPasswordRequest extends PCPacketServerToClient {
    private int x;
    private int y;
    private int z;

    public PCPacketPasswordRequest(){

    }

    public PCPacketPasswordRequest(PCTileEntityAPI te){
        this.x = te.getPos().getX();
        this.y = te.getPos().getY();
        this.z = te.getPos().getZ();
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCTileEntityAPI te = PCUtils.getTileEntity(world, this.x, this.y, this.z, PCTileEntityAPI.class);
        if(te!=null){
            te.openPasswordGui();
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
}
