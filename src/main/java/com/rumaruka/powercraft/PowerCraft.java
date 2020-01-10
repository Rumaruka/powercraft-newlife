package com.rumaruka.powercraft;


import com.rumaruka.powercraft.api.PCTickHandler;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.PCWorldSaveData;
import com.rumaruka.powercraft.api.dimension.PCDimension;
import com.rumaruka.powercraft.api.dimension.PCDimensions;
import com.rumaruka.powercraft.api.energy.PCEnergyGrid;
import com.rumaruka.powercraft.api.gres.PCGres;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.init.PCBlocks;
import com.rumaruka.powercraft.init.PCItems;
import com.rumaruka.powercraft.proxy.CommonProxy;
import com.rumaruka.powercraft.ref.RefMods;
import com.rumaruka.powercraft.ref.RefProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;


import static com.rumaruka.powercraft.ref.RefMods.*;
import static com.rumaruka.powercraft.ref.RefProxy.CLIENT_PROXY;
import static com.rumaruka.powercraft.ref.RefProxy.COMMON_PROXY;


@Mod(modid = MODID, name = MODNAME, version = MODVERSION)
public class PowerCraft {


    @Instance(MODID)
    public static PowerCraft instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @EventHandler
    public void PreInit(FMLPreInitializationEvent e) {
         proxy.preInit(e);
        //PowerCraft Block Loading
        PCBlocks.init();
        PCBlocks.registerINGAME();
        //PowerCraft Item Loading
        PCItems.init();
        PCItems.registerINGAME();

        PCPacketHandler.register();
        PCTickHandler.register();
        PCGres.register();
        PCEnergyGrid.register();
        PCDimensions.construct();

    }
    @EventHandler
    public void Init(FMLInitializationEvent e) {
        proxy.init(e);
        proxy.renderObjects();
    }
    @EventHandler
    public void PostInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);

    }

    @EventHandler
    public void serverStart(FMLServerAboutToStartEvent e){

        PCPacketHandler.setupPackets();
    }
    @EventHandler
    public void serverStop(FMLServerStoppingEvent E){
        PCWorldSaveData.onServerStopping();
    }
}
