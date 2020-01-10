package com.rumaruka.powercraft.api.network;


import com.rumaruka.powercraft.api.PCCtrlPressed;
import com.rumaruka.powercraft.api.PCSide;
import com.rumaruka.powercraft.PowerCraft;

import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.network.packet.*;
import com.rumaruka.powercraft.api.reflect.PCReflect;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class PCPacketHandler extends SimpleChannelInboundHandler<PCPacket> {
    private static boolean done;
    private static EnumMap<Side, FMLEmbeddedChannel> channels;
    private static List<Class<? extends PCPacket>> packetList = new ArrayList<Class<? extends PCPacket>>();
    @SuppressWarnings("unchecked")
    private static Class<? extends PCPacket>[] packets = new Class[]{PCPacketPacketResolve.class};
    private static HashMap<Class<? extends PCPacket>, Integer> packetID = new HashMap<Class<? extends PCPacket>, Integer>();

    static{
        packetID.put(PCPacketPacketResolve.class, 0);
    }
    public static void register(){
        PCSecurity.allowedCaller("PC_PacketHandler.register()", PowerCraft.class);
        if(channels==null){
            channels = NetworkRegistry.INSTANCE.newChannel("PowerCraft", new Indexer());
            FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
            channel.pipeline().addAfter(channel.findChannelHandlerNameForType(Indexer.class), PCPacketHandler.class.getName(), new PCPacketHandler(PCSide.CLIENT));
            channel = channels.get(Side.SERVER);
            channel.pipeline().addAfter(channel.findChannelHandlerNameForType(Indexer.class), PCPacketHandler.class.getName(), new PCPacketHandler(PCSide.SERVER));
            FMLCommonHandler.instance().bus().register(new Listener());
            PCPacketHandler.registerPacket(PCPacketTileEntitySync.class);
            PCPacketHandler.registerPacket(PCPacketPasswordRequest.class);
            PCPacketHandler.registerPacket(PCPacketPasswordReply.class);
            PCPacketHandler.registerPacket(PCPacketWrongPassword.class);
            PCPacketHandler.registerPacket(PCPacketPasswordRequest2.class);
            PCPacketHandler.registerPacket(PCPacketPasswordReply2.class);
            PCPacketHandler.registerPacket(PCPacketWrongPassword2.class);
            PCPacketHandler.registerPacket(PCPacketTileEntityMessageCTS.class);
            PCPacketHandler.registerPacket(PCPacketTileEntityMessageSTC.class);
            PCPacketHandler.registerPacket(PCPacketTileEntityMessageIntCTS.class);
            PCPacketHandler.registerPacket(PCPacketEntityMessageCTS.class);
            PCPacketHandler.registerPacket(PCPacketEntityMessageSTC.class);
            PCPacketHandler.registerPacket(PCPacketEntitySync.class);
            PCPacketHandler.registerPacket(PCPacketSetSlot.class);
            PCPacketHandler.registerPacket(PCPacketWindowItems.class);
            PCPacketHandler.registerPacket(PCPacketClickWindow.class);
            PCPacketHandler.registerPacket(PCPacketBlockBreaking.class);
            PCPacketHandler.registerPacket(PCCtrlPressed.Packet.class);
        }
    }

    public static class Listener{

        Listener(){

        }

        @SuppressWarnings("static-method")
        @SubscribeEvent
        public void clientConnected(PlayerEvent.PlayerLoggedInEvent event){
            sendPacketResolveTo((EntityPlayerMP) event.player);
        }

    }

    @Sharable
    public static class Indexer extends MessageToMessageCodec<FMLProxyPacket, PCPacket> {

        Indexer(){

        }

        @Override
        protected void encode(ChannelHandlerContext ctx, PCPacket msg, List<Object> out) throws Exception {
            ByteBuf buffer = Unpooled.buffer();
            writePacketToByteBuf(msg, buffer);
            FMLProxyPacket packet = new FMLProxyPacket(new PacketBuffer(buffer.copy()), "PowerCraft");
            out.add(packet);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
            out.add(getPacketFromByteBuf(msg.payload()));
        }

    }

    public static Packet getPacketFrom(PCPacket packet){
        Packet pkt = channels.get(Side.SERVER).generatePacketFrom(packet);
        if(pkt==null){
        }
        return pkt;
    }

    public static void registerPacket(Class<? extends PCPacket> packet){
        if(done){
        }else{
            packetList.add(packet);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setupPackets(){
        PCSecurity.allowedCaller("PC_PacketHandler.setupPackets()", PowerCraft.class);
        done = true;
        packets = new Class[packetList.size()+1];
        packetID.clear();
        packets[0] = PCPacketPacketResolve.class;
        packetID.put(PCPacketPacketResolve.class, 0);
        for(int i=1; i<packets.length; i++){
            packets[i] = packetList.get(i-1);
            packetID.put(packets[i], i);
        }
    }

    static void sendPacketResolveTo(EntityPlayerMP player){
        String[] packetClasses = new String[packets.length-1];
        for(int i=0; i<packetClasses.length; i++){
            packetClasses[i] = packets[i+1].getName();
        }
        sendTo(new PCPacketPacketResolve(packetClasses), player);
    }

    static PCPacket getPacketFromByteBuf(ByteBuf buf) {
        int id = buf.readInt();
        PCPacket packet = PCReflect.newInstance(packets[id]);
        if(packet==null){
            return null;
        }
        packet.fromByteBuffer(buf);
        return packet;
    }

    static void writePacketToByteBuf(PCPacket packet, ByteBuf buf) {
        Integer id = packetID.get(packet.getClass());
        if(id==null){
            return;
        }
        buf.writeInt(id);
        packet.toByteBuffer(buf);
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    static void setPackets(String[] packetClasses) {

        packets = new Class[packetClasses.length+1];
        packetID.clear();
        packets[0] = PCPacketPacketResolve.class;
        packetID.put(PCPacketPacketResolve.class, 0);
        for(int i=1; i<packets.length; i++){
            try {
                packets[i] = (Class<? extends PCPacket>) Class.forName(packetClasses[i-1]);
                packetID.put(packets[i], i);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendToAll(PCPacket packet){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendTo(PCPacket packet, EntityPlayerMP player){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToAllAround(PCPacket packet, int dimension, double x, double y, double z, double range){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToAllAround(PCPacket packet, World world, double x, double y, double z, double range){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(PCUtils.getDimensionID(world), x, y, z, range));
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToDimension(PCPacket packet, int dimension){
        checkServer(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendToServer(PCPacket packet){
        checkClient(packet);
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private static void checkServer(PCPacket packet){

    }

    private static void checkClient(PCPacket packet){

    }

    public PCSide side;

    private PCPacketHandler(PCSide side){
        this.side = side;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PCPacket msg) throws Exception {
        INetHandler iNetHandler = channels.get(this.side.side).attr(NetworkRegistry.NET_HANDLER).get();
        PCPacket result = msg.doAndReply(this.side, iNetHandler);
        if (result != null){
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }
}
