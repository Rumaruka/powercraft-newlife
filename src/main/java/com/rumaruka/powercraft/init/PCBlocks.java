package com.rumaruka.powercraft.init;

import com.rumaruka.powercraft.api.registers.BlockRegister;
import com.rumaruka.powercraft.modules.PCConfig;
import com.rumaruka.powercraft.modules.checkpoint.BlockCheckPoint;
import com.rumaruka.powercraft.modules.main.block.BlockSiliconOre;
import com.rumaruka.powercraft.modules.main.creativetabs.PCCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;

import static com.rumaruka.powercraft.modules.main.creativetabs.PCCreativeTabs.PC_CREATIVETABS;

public class PCBlocks {





    //World
    public static Block silicon_ore;
    public static Block red_crystal;
    public static Block yellow_crystal;


    //Checkpoint

    public static Block checkpoint;


    public static void init(){

        silicon_ore = new BlockSiliconOre().setCreativeTab(PC_CREATIVETABS).setUnlocalizedName("silicon_ore");
        if(PCConfig.checkpointModule){
            checkpoint = new BlockCheckPoint().setCreativeTab(PC_CREATIVETABS).setUnlocalizedName("checkpoint");

        }
    }




    public static void registerINGAME(){
        BlockRegister.registerBlock(silicon_ore,silicon_ore.getUnlocalizedName().substring(5));
        if(PCConfig.checkpointModule){
            BlockRegister.registerBlock(checkpoint,checkpoint.getUnlocalizedName().substring(5));

        }
    }



    public static void renderBlock(){

        BlockRegister.registerRender(silicon_ore);
        if(PCConfig.checkpointModule){
            BlockRegister.registerRender(checkpoint);

        }

    }



}
