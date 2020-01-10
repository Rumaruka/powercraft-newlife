package com.rumaruka.powercraft.api;


import com.rumaruka.powercraft.api.network.PCPacket;
import com.rumaruka.powercraft.api.network.PCPacketClientToServer;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PCCtrlPressed {
    private static Check client;

    private static List<WeakReference<EntityPlayer>> players = new ArrayList<WeakReference<EntityPlayer>>();

    public static boolean isPressingCtrl(EntityPlayer player){
        if(player.world.isRemote){
            return client.check();
        }
        Iterator<WeakReference<EntityPlayer>> i = players.iterator();
        while(i.hasNext()){
            EntityPlayer ep = i.next().get();
            if(ep==null){
                i.remove();
            }
            if(ep==player)
                return true;
        }
        return false;
    }

    static void setPressingCtrl(EntityPlayer player, boolean state){
        if(state){
            if(!isPressingCtrl(player)){
                players.add(new WeakReference<EntityPlayer>(player));
            }
        }else{
            Iterator<WeakReference<EntityPlayer>> i = players.iterator();
            while(i.hasNext()){
                EntityPlayer ep = i.next().get();
                if(ep==null){
                    i.remove();
                }else if(ep==player){
                    i.remove();
                    break;
                }
            }
        }
    }

    public static final class Packet extends PCPacketClientToServer {

        private int key;

        Packet(int key){
            this.key = key;
        }

        public Packet(){

        }

        @Override
        protected void fromByteBuffer(ByteBuf buf) {
            this.key = buf.readInt();
        }

        @Override
        protected void toByteBuffer(ByteBuf buf) {
            buf.writeInt(this.key);
        }



        @Override
        protected PCPacket doAndReple(NetHandlerPlayServer playServer, World world, EntityPlayerMP playerMP) {
            setPressingCtrl(playerMP, this.key!=0);
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private static final class KeyHandler extends PCKeyboard.PCKeyHandler {

        public KeyHandler(String sKey, int key, String desk) {
            super(sKey, key, desk);
        }

        @Override
        public void onTick() {
            //
        }

        @Override
        public void onPressed() {
            System.out.println("Pressed");
            PCPacketHandler.sendToServer(new Packet(1));
        }

        @Override
        public void onRelease() {
            System.out.println("Release");
            PCPacketHandler.sendToServer(new Packet(0));
        }

    }

    private static class Check{

        Check() {

        }

        @SuppressWarnings("static-method")
        public boolean check() {
            return false;
        }

    }

    @SideOnly(Side.CLIENT)
    private static class CheckClient extends Check{

        static KeyHandler keyHandler;

        CheckClient() {

        }

        @Override
        public boolean check() {
            return keyHandler.isPressed();
        }

    }

    @SideOnly(Side.CLIENT)
    static void register() {
        PCSecurity.allowedCaller("PC_CtrlPressed.register()", PCClientUtils.class);
        CheckClient.keyHandler = new KeyHandler("ctrl", Keyboard.KEY_LCONTROL, "PowerCraft special button");
        client = new CheckClient();
    }
}
