package com.rumaruka.powercraft.init;

import com.rumaruka.powercraft.api.registers.ItemRegister;

import com.rumaruka.powercraft.modules.core.item.ItemActivator;
import com.rumaruka.powercraft.modules.core.item.ItemOreSniffer;
import com.rumaruka.powercraft.modules.main.item.ItemSiliconIngot;
import net.minecraft.item.Item;

import static com.rumaruka.powercraft.modules.main.creativetabs.PCCreativeTabs.PC_CREATIVETABS;

public class PCItems {




    public static Item silicon_ingot;
    public static Item activator;
    public static Item ore_sniffer;

    public static void init(){
    silicon_ingot = new ItemSiliconIngot().setUnlocalizedName("silicon_ingot").setCreativeTab(PC_CREATIVETABS);
        activator = new ItemActivator().setUnlocalizedName("activator").setCreativeTab(PC_CREATIVETABS);
        ore_sniffer = new ItemOreSniffer().setUnlocalizedName("ore_sniffer").setCreativeTab(PC_CREATIVETABS);

    }




    public static void registerINGAME(){
        ItemRegister.registerItem(silicon_ingot,silicon_ingot.getUnlocalizedName().substring(5));
        ItemRegister.registerItem(activator,activator.getUnlocalizedName().substring(5));
        ItemRegister.registerItem(ore_sniffer,ore_sniffer.getUnlocalizedName().substring(5));
    }



    public static void renderItem(){
        ItemRegister.renderItems(silicon_ingot);
        ItemRegister.renderItems(activator);
        ItemRegister.renderItems(ore_sniffer);

    }
}
