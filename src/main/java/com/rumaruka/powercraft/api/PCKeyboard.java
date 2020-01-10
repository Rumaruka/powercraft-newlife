package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.PCClientUtils;
import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public final class PCKeyboard {
    static List<PCKeyHandler> handlers = new ArrayList<PCKeyHandler>();

    private static final PCKeyboard INSTANCE = new PCKeyboard();

    @SideOnly(Side.CLIENT)
    static void register(){
        PCSecurity.allowedCaller("PC_Keyboard.register()", PCClientUtils.class);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    private PCKeyboard(){

    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unused", "static-method" })
    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent inputEvent){
        int key = Keyboard.getEventKey();
        boolean state = Keyboard.getEventKeyState();
        for(PCKeyHandler handler:handlers){
            if(handler.getKeyCode()==key){
                handler.onEvent(state);
            }
        }
    }

    public static abstract class PCKeyHandler extends KeyBinding {

        private boolean state;

        public PCKeyHandler(String sKey, int key, String desk) {
            super(sKey, key, desk);
            handlers.add(this);
        }

        @SuppressWarnings("hiding")
        void onEvent(boolean state) {
            if(this.state && state){
                onTick();
            }else if(this.state && !state){
                onRelease();
            }else if(!this.state && state){
                onPressed();
            }
            this.state = state;
        }

        @Override
        public boolean isPressed() {
            return this.state;
        }

        public abstract void onTick();

        public abstract void onPressed();

        public abstract void onRelease();

    }
}
