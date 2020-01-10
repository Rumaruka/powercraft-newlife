package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCFields;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

import javax.management.InstanceAlreadyExistsException;

public class PCClientUtils extends PCUtils{
    private static InheritableThreadLocal<Boolean> isClient = new InheritableThreadLocal<Boolean>();

    static{
        try {
            new PCClientUtils();
        } catch (InstanceAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Will be called from Forge
     */
    public PCClientUtils() throws InstanceAlreadyExistsException {

       // PCRenderer.getInstance();
        isClient.set(Boolean.TRUE);
      //  PCResourceReloadListener.register();
        PCKeyboard.register();
        PCCtrlPressed.register();

    }


    /**
     * get the Minecraft instance
     * @return the Minecraft instance
     */
    public static Minecraft mc() {

        return Minecraft.getMinecraft();
    }




    GameType iGetGameTypeFor(EntityPlayer player) {
        return PCFields.Client.PlayerControllerMP_currentGameType.getValue(mc().playerController);
    }

    /**
     * is this game running on client
     * @return always yes
     */
    @SuppressWarnings("hiding")
    @Override
    PCSide iGetSide(){
        Boolean isClient = PCClientUtils.isClient.get();
        if(isClient==null){
            return PCSide.CLIENT;
        }else if(isClient.booleanValue()){
            return PCSide.CLIENT;
        }
        return PCSide.SERVER;
    }



    @Override
    EntityPlayer iGetClientPlayer() {
        return mc().player;
    }



}
