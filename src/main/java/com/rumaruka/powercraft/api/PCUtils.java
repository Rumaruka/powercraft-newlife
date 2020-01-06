package com.rumaruka.powercraft.api;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;

import com.rumaruka.powercraft.PCSide;
import com.rumaruka.powercraft.api.inventory.PCInventoryUtils;
import com.rumaruka.powercraft.api.recipe.RecipeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


public class PCUtils {
    private static PCUtils INSTANCE;

    public static final int BLOCK_NOTIFY = 1, BLOCK_UPDATE = 2, BLOCK_ONLY_SERVERSIDE = 4;

    public static final int WILDCARD_VALUE = Short.MAX_VALUE;

    PCUtils() throws InstanceAlreadyExistsException {
        if (INSTANCE != null) {
            throw new InstanceAlreadyExistsException();
        }
        INSTANCE = this;
    }

    public static <T> T as(Object obj, Class<T> c) {
        if (obj != null && c.isAssignableFrom(obj.getClass())) {
            return c.cast(obj);
        }
        return null;
    }

    public static TileEntity getTileEntity(IBlockAccess world, int x, int y, int z) {
        return world.getTileEntity(new BlockPos(x, y, z));
    }

    public static <T> T getTileEntity(IBlockAccess world, float x, float y, float z, Class<T> c) {
        return as(world.getTileEntity(new BlockPos(x, y, z)), c);
    }

    public static TileEntity getTileEntity(IBlockAccess world, BlockPos pos) {
        return world.getTileEntity(new BlockPos(pos));
    }

    public static <T> T getTileEntity(IBlockAccess world, PCVec3I pos, Class<T> c) {
        return getTileEntity(world, pos.x, pos.y, pos.z, c);
    }

    public static IBlockState getBlock(IBlockAccess world, float x, float y, float z) {
        return world.getBlockState(new BlockPos(x, y, z));
    }

    public static <T> T getBlock(IBlockAccess world, float x, float y, float z, Class<T> c) {
        return as(world.getBlockState(new BlockPos(x, y, z)), c);
    }

    public static IBlockState getBlock(IBlockAccess world, BlockPos pos) {
        return getBlock(world, pos);
    }

    public static <T> T getBlock(IBlockAccess world, PCVec3I pos, Class<T> c) {
        return getBlock(world, pos.x, pos.y, pos.z, c);
    }














    public static Item getItem(ItemStack itemStack) {
        return itemStack.getItem();
    }

    public static <T> T getItem(ItemStack itemStack, Class<T> c) {
        return as(getItem(itemStack), c);
    }


    public static Item getItemForBlock(Block block) {
        return Item.getItemFromBlock(block);
    }








    public static int getRotation(Entity entity) {
        return PCMathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
    }

    public static int getRotationMetadata(int metadata, Entity entity) {
        return (getRotation(entity) << 2) | (metadata & 3);
    }





    public static void spawnItem(World world, double x, double y, double z, ItemStack itemStack) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && itemStack != null) {
            spawnItemChecked(world, x, y, z, itemStack);
        }
    }

    public static void spawnItem(World world, PCVec3 pos, ItemStack itemStack) {
        spawnItem(world, pos.x, pos.y, pos.z, itemStack);
    }

    public static void spawnItems(World world, double x, double y, double z, List<ItemStack> itemStacks) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && itemStacks != null) {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack != null) {
                    spawnItemChecked(world, x, y, z, itemStack);
                }
            }
        }
    }

    public static void spawnItems(World world, PCVec3 pos, List<ItemStack> itemStack) {
        spawnItems(world, pos.x, pos.y, pos.z, itemStack);
    }

    public static void spawnItems(World world, List<EntityItem> itemStacks) {

        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && itemStacks != null) {
            for (EntityItem itemStack : itemStacks) {
                if (itemStack != null) {
                    spawnItemChecked(world, itemStack.posX, itemStack.posY, itemStack.posZ, itemStack.getItem());
                }
            }
        }
    }

    private static void spawnItemChecked(World world, double x, double y, double z, ItemStack itemStack) {
        float f = 0.7F;
        double d0 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
        double d1 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
        double d2 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
        entityitem.setDefaultPickupDelay();
        world.spawnEntity(entityitem);
    }

    public static void spawnEntity(World world, Entity entity) {
        if (!world.isRemote) {
            world.spawnEntity(entity);
        }
    }


    public static int getRedstoneValue(World world, int x, int y, int z) {
        return world.getStrongPower(new BlockPos(x, y, z));
    }




    public static void notifyBlockOfNeighborChange(World world, int x, int y, int z,int x1, int y1, int z1, Block neightbor) {
        Block block = (Block) getBlock(world, x, y, z);
        if (block != null) {
            ((IBlockState) block).neighborChanged(world, new BlockPos(x, y, z), neightbor,new BlockPos(x1,y1,z1));
        }
    }



    public static String getUsername(EntityPlayer player) {
        return player.getGameProfile().getName();
    }



    public static PCSide getSide() {
        return INSTANCE.iGetSide();
    }

    @SuppressWarnings("static-method")
    PCSide iGetSide() {
        return PCSide.SERVER;
    }

    static void markThreadAsServer() {
        INSTANCE.iMarkThreadAsServer();
    }

    void iMarkThreadAsServer() {
        //
    }

    public static boolean isServer() {
        return getSide() == PCSide.SERVER;
    }

    public static boolean isClient() {
        return getSide() == PCSide.CLIENT;
    }



    public static PCDirection getEntityMovement2D(Entity entity) {
        double mx = entity.motionX;
        double mz = entity.motionZ;
        if (Math.abs(mx) > Math.abs(mz)) {
            if (mx > 0) {
                return PCDirection.EAST;
            }
            return PCDirection.WEST;
        }
        if (mz > 0) {
            return PCDirection.SOUTH;
        }
        if (mz == 0) {
            if (entity instanceof EntityLivingBase) {
                return PCDirection.fromRotationY(getRotation(entity)).getOpposite();
            }
        }
        return PCDirection.NORTH;
    }

    public static NBTTagCompound getNBTTagOf(Object obj) {
        NBTTagCompound tag;
        if (obj instanceof Entity) {
            tag = ((Entity) obj).getEntityData();
        } else if (obj instanceof ItemStack) {
            tag = ((ItemStack) obj).getTagCompound();
        } else {
            return null;
        }
        if (tag == null || !tag.hasKey("PowerCraft"))
            return null;
        return tag.getCompoundTag("PowerCraft");
    }

    public static NBTTagCompound getWritableNBTTagOf(Object obj) {
        NBTTagCompound tag;
        if (obj instanceof Entity) {
            tag = ((Entity) obj).getEntityData();
        } else if (obj instanceof ItemStack) {
            tag = ((ItemStack) obj).getTagCompound();
            if (tag == null) {
                ((ItemStack) obj).setTagCompound(tag = new NBTTagCompound());
            }
        } else {
            return null;
        }
        if (tag.hasKey("PowerCraft")) {
            return tag.getCompoundTag("PowerCraft");
        }
        NBTTagCompound pctag = new NBTTagCompound();
        tag.setTag("PowerCraft", pctag);
        return pctag;
    }

    public static EntityPlayer getClientPlayer() {
        return INSTANCE.iGetClientPlayer();
    }

    @SuppressWarnings("static-method")
    EntityPlayer iGetClientPlayer() {
        return null;
    }

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5(String s) {
        return new String(digest.digest(s.getBytes()));
    }

    @SuppressWarnings("unused")
    public static int getSideRotation(IBlockAccess world, int x, int y, int z, PCDirection side, int faceSide) {
        notImplementedYet("getSideRotation");
        // TODO Auto-generated method stub
        return 0;
    }

    public static int getBurnTime(ItemStack itemStack) {
        return TileEntityFurnace.getItemBurnTime(itemStack);
    }

    public static ItemStack getSmeltingResult(ItemStack itemStack) {
        if (itemStack == null)
            return null;
        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(itemStack);
        if (smelted == null)
            return null;
        return smelted.copy();
    }

    public static Entity getEntity(World world, int entityID) {
        return world.getEntityByID(entityID);
    }

    public static <T> T getEntity(World world, int entityID, Class<T> c) {
        return as(getEntity(world, entityID), c);
    }


    public static boolean isEntityFX(Entity entity) {
        return INSTANCE.iIsEntityFX(entity);
    }

    @SuppressWarnings({ "static-method", "unused" })
    boolean iIsEntityFX(Entity entity) {
        return false;
    }

    public static void deleteDirectoryOrFile(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                deleteDirectoryOrFile(c);
            }
        }
        file.delete();
    }

    public static int getDimensionID(World world) {
        return world.provider.getDimension();
    }




    public static void setArrayContentsToNull(Object[] array) {
        Arrays.fill(array, null);
    }

    public static boolean isBlockSideSolid(IBlockState state,IBlockAccess world, int x, int y, int z, PCDirection dir){
        Block block = (Block) PCUtils.getBlock(world, x, y, z);
        return block.isSideSolid(state,world, new BlockPos(x, y, z), dir.toForgeDirection());
    }



    private static List<String> messages = new ArrayList<String>();

    public static void notImplementedYet(String what){
        if(!messages.contains(what)){
            messages.add(what);
            PCLogger.severe("%s not implemented yet", what);
        }
    }




    public static int getColorFor(int index) {
        return ItemDye.DYE_COLORS[index];
    }

    public static int countBits(int mask) {
        int bits = 0;
        for(int i=0; i<32; i++){
            if((mask & 1<<i)!=0){
                bits++;
            }
        }
        return bits;
    }

    public static boolean isBlockReplaceable(World world, int x, int y, int z) {
        Block block = (Block) getBlock(world, x, y, z);
        if(block.isReplaceable(world, new BlockPos(x, y, z)))
            return true;
        return block==Blocks.SNOW_LAYER||block == Blocks.VINE || block == Blocks.TALLGRASS || block == Blocks.DEADBUSH;
    }

    public static PCVec3 getLookDir(EntityPlayer player) {
        float pitch = (float) (player.rotationPitch*Math.PI/180);
        double y = -PCMathHelper.sin(pitch);
        double o = PCMathHelper.cos(pitch);
        float yaw = (float) (player.rotationYaw*Math.PI/180);
        double x = PCMathHelper.sin(-yaw)*o;
        double z = PCMathHelper.cos(-yaw)*o;
        PCVec3 lookDir = new PCVec3(x, y, z);
        return lookDir;
    }




    @SuppressWarnings("unchecked")
    public static List<ItemStack> getItemStacksForOreItem(Object oreItem) {
        if(oreItem instanceof ItemStack){
            List<ItemStack> list = new ArrayList<ItemStack>();
            list.add((ItemStack) oreItem);
            return list;
        }else if(oreItem instanceof List){
            return new ArrayList<ItemStack>((List<ItemStack>) oreItem);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity>List<E> getEntitiesWithinAABB(World world, AxisAlignedBB aabb, Class<E> c) {
        return world.getEntitiesWithinAABB(c, aabb);
    }

    @SuppressWarnings("unchecked")
    public static List<Entity> getEntitiesWithinAABB(World world, AxisAlignedBB aabb) {
        return world.getEntitiesWithinAABB(Entity.class, aabb);
    }




}
