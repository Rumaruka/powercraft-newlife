package com.rumaruka.powercraft.modules.main.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

public class BlockCrystals extends Block {


    public BlockCrystals( ) {
        super(Material.ROCK);
        setSoundType(SoundType.GLASS);

    }
}
