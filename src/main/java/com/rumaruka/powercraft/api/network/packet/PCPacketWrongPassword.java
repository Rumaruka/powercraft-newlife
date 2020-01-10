package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PCPacketWrongPassword extends PCPacketServerToClient {
    private int x;
    private int y;
    private int z;

    public PCPacketWrongPassword(){

    }

    public PCPacketWrongPassword(PCTileEntityAPI te){
        this.x = te.getPos().getX();
        this.y = te.getPos().getY();
        this.z = te.getPos().getZ();
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCTileEntityAPI te = PCUtils.getTileEntity(world, this.x, this.y, this.z, PCTileEntityAPI.class);
        if(te!=null){
            te.wrongPasswordInput();
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {

    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {

    }
}
