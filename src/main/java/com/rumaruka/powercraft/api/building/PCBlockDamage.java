package com.rumaruka.powercraft.api.building;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.PCField.Flag;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.network.packet.PCPacketBlockBreaking;
import com.rumaruka.powercraft.api.reflect.PCFields;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class PCBlockDamage extends PCWorldSaveData implements PCTickHandler.ITickHandler {
    private static final String NAME = "powercraft-blockdamage";

    private List<PCVec4I> updated = new ArrayList<PCVec4I>();
    private HashMap<PCVec4I, float[]> damages = new HashMap<PCVec4I, float[]>();
    private static PCBlockDamage INSTANCE;

    private static PCBlockDamage getInstance(){
        if(INSTANCE==null){
            INSTANCE = loadOrCreate(NAME, PCBlockDamage.class);
        }
        return INSTANCE;
    }

    public PCBlockDamage(String name){
        super(name);
        PCTickHandler.registerTickHandler(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        PCNBTTagHandler.loadMapFromNBT(nbtTagCompound, "damages", this.damages, PCVec4I.class, float[].class, Flag.SAVE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        PCNBTTagHandler.saveMapToNBT(nbtTagCompound, "damages", this.damages, Flag.SAVE);
        return nbtTagCompound;
    }

    @Override
    public void cleanup() {
        INSTANCE = null;
        PCTickHandler.removeTickHander(this);
    }

    // 0-10, on 10 destroy
    public static boolean damageBlock(World world, float x, float y, float z, float amount){
        if(world.isRemote)
            return false;
        PCVec4I v4 = new PCVec4I(x, y, z, world.provider.getDimension());
        getInstance();
        float[] damage = INSTANCE.damages.get(v4);
        int pd = -1;
        if(damage==null || damage.length!=2){
            damage = new float[2];


            INSTANCE.damages.put(v4, damage);
        }else{
            pd = (int)damage[0];
        }
        damage[0] += amount/damage[1];
        INSTANCE.markDirty();
        int npd = (int)damage[0];
        if(damage[0]>=10){
            INSTANCE.updated.remove(v4);
            INSTANCE.damages.remove(v4);
            INSTANCE.markDirty();

        }
        if(pd!=npd)
            PCPacketHandler.sendToAllAround(new PCPacketBlockBreaking(x, y, z, npd), (int) v4.w, x, y, z, 32);
        if(!INSTANCE.updated.contains(v4))
            INSTANCE.updated.add(v4);
        return false;
    }


    public void onStartTick(PCSide side) {
        if(side==PCSide.SERVER)
            this.updated.clear();
    }


    public void onEndTick(PCSide side) {
        if(side== PCSide.SERVER){
            Iterator<PCVec4I> i = this.damages.keySet().iterator();
            while(i.hasNext()){
                PCVec4I v4 = i.next();
                if(!this.updated.contains(v4)){
                    i.remove();
                    PCPacketHandler.sendToAllAround(new PCPacketBlockBreaking(v4.x, v4.y, v4.z, -1), (int) v4.w, v4.x, v4.y, v4.z, 32);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
   public static void setClientDamage(float x, float y, float z, float damage){
        Map<Number, DestroyBlockProgress> damagedBlocks = PCFields.Client.RenderGlobal_damagedBlocks.getValue(PCClientUtils.mc().renderGlobal);
        int cloudTickCounter = PCFields.Client.RenderGlobal_cloudTickCounter.getValue(PCClientUtils.mc().renderGlobal).intValue();
        PosDamage pd = new PosDamage(x, y, z);
        if(damage==-1){
            damagedBlocks.remove(pd);
        }else{
            DestroyBlockProgress destroyBlockProgress = damagedBlocks.get(pd);
            if(destroyBlockProgress==null){
                destroyBlockProgress = new DestroyBlockProgress(0, new BlockPos(x, y, z));
                damagedBlocks.put(pd, destroyBlockProgress);
            }
            destroyBlockProgress.setPartialBlockDamage((int) damage);
            destroyBlockProgress.setCloudUpdateTick(cloudTickCounter);
        }
    }

    @SideOnly(Side.CLIENT)
    private static class PosDamage extends Number{

        private static final long serialVersionUID = -3611831358033254957L;

        private float x;
        private float y;
        private float z;

        public PosDamage(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public double doubleValue() {
            return -1;
        }

        @Override
        public float floatValue() {
            return -1;
        }

        @Override
        public int intValue() {
            return -1;
        }

        @Override
        public long longValue() {
            return -1;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (int) (prime * result + this.x);
            result = (int) (prime * result + this.y);
            result = (int) (prime * result + this.z);
            return result;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PosDamage other = (PosDamage) obj;
            if (this.x != other.x) return false;
            if (this.y != other.y) return false;
            if (this.z != other.z) return false;
            return true;
        }

    }
}
