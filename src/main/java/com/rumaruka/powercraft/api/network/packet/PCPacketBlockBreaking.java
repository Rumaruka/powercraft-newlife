package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.building.PCBlockDamage;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PCPacketBlockBreaking extends PCPacketServerToClient {

    private float x;
    private float y;
    private float z;
    private float damage;

    public PCPacketBlockBreaking(){

    }

    public PCPacketBlockBreaking(float x, float y, float z, float damage) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.damage = damage;
    }
    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {
        PCBlockDamage.setClientDamage(this.x, this.y, this.z, this.damage);
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.damage = buf.readFloat();
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.damage);
    }
}
