package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;

import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketServerToClient;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import com.rumaruka.powercraft.init.PCTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PCPacketTileEntitySync extends PCPacketServerToClient {

    private int x;
    private int y;
    private int z;
    private NBTTagCompound compound;

    public PCPacketTileEntitySync(){

    }
    public PCPacketTileEntitySync(PCTileEntityAPI tileEntity, NBTTagCompound compound){
        this.x=tileEntity.getPos().getX();
        this.y=tileEntity.getPos().getY();
        this.z=tileEntity.getPos().getZ();

        this.compound=compound;
    }

    @Override
    protected PCPacket doAndReply(NetHandlerPlayClient playClient, World world, EntityPlayer player) {

        PCTileEntityAPI te =  PCUtils.getTileEntity(world,this.x,this.y,this.z,PCTileEntityAPI.class);

        if(te!=null){
            te.applySync(this.compound);
        }




        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.compound = readNBTFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        writeNBTToBuf(this.compound,buf);
    }
}
