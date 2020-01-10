package com.rumaruka.powercraft.api.network.packet;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketClientToServer;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class PCPacketPasswordReply extends PCPacketClientToServer {

    private int x;
    private int y;
    private int z;
    private String password;

    public PCPacketPasswordReply(){

    }

    public PCPacketPasswordReply(PCTileEntityAPI te, String password){
        this.x = te.getPos().getX();
        this.y = te.getPos().getY();
        this.z = te.getPos().getZ();
        this.password = password;
    }

    @Override
    protected PCPacket doAndReple(NetHandlerPlayServer playServer, World world, EntityPlayerMP playerMP) {
        PCTileEntityAPI te = PCUtils.getTileEntity(world, this.x, this.y, this.z, PCTileEntityAPI.class);
        if(te!=null){
            if(!te.guiOpenPasswordReply(playerMP, this.password)){
                return new PCPacketWrongPassword(te);
            }
        }
        return null;
    }

    @Override
    protected void fromByteBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.password = readStringFromBuf(buf);
    }

    @Override
    protected void toByteBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        writeStringToBuf(buf, this.password);
    }


}
