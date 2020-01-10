package com.rumaruka.powercraft.api;

import com.rumaruka.powercraft.api.reflect.PCSecurity;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PCResourceReloadListener {


    private static List<IResourceReloadListener> listeners = new ArrayList<IResourceReloadListener>();

    @SideOnly(Side.CLIENT)
    static void register(){
        PCSecurity.allowedCaller("PC_ResourceListener.register()", PCClientUtils.class);
        ((IReloadableResourceManager)PCClientUtils.mc().getResourceManager()).registerReloadListener(PCResourceListener.INSTANCE);
    }



    static void onResourceReload(){
        for(IResourceReloadListener listener:listeners){
            listener.onResourceReload();
        }
    }

    public static void registerResourceReloadListener(IResourceReloadListener listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    @SideOnly(Side.CLIENT)
    private static final class PCResourceListener implements IResourceManagerReloadListener{

        public static final PCResourceListener INSTANCE = new PCResourceListener();

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            onResourceManagerReload(resourceManager);
        }
    }


    public interface IResourceReloadListener{
        void onResourceReload();
    }
}
