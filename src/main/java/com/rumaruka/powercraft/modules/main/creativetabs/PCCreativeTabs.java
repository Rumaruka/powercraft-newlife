package com.rumaruka.powercraft.modules.main.creativetabs;

import com.rumaruka.powercraft.init.PCItems;
import com.rumaruka.powercraft.ref.RefMods;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PCCreativeTabs extends CreativeTabs {

    public static PCCreativeTabs PC_CREATIVETABS = new PCCreativeTabs();

    public PCCreativeTabs( ) {
        super(RefMods.MODID);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(PCItems.activator);
    }
}
