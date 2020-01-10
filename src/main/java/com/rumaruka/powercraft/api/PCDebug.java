package com.rumaruka.powercraft.api;

import net.minecraft.client.renderer.entity.RenderManager;

public class PCDebug {
    public static final boolean DEBUG = false;

    /**
     * if on the string will be printed, else not
     * @param s the string to be printed when you are in debug
     */
    public static void println(String s){
        if(DEBUG)
            System.out.println(s);
    }

    public static void setup(){
        if(DEBUG){

        }
    }

}
