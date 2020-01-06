package com.rumaruka.powercraft.api.inventory;

import com.google.common.base.Predicate;
import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCUtils;
import com.rumaruka.powercraft.api.PCVec3;
import com.rumaruka.powercraft.api.PCVec3I;

import com.rumaruka.powercraft.api.item.IItemPC;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IEntitySelectorFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PCInventoryUtils {

    public static void loadInventoryFromNBT(IInventory inventory, NBTTagCompound nbtTagCompound, String key) {

        if(inventory instanceof IInventorySetNoMark){
            IInventorySetNoMark isnm = (IInventorySetNoMark)inventory;
            NBTTagList list = nbtTagCompound.getTagList(key, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbtTagCompound2 = list.getCompoundTagAt(i);
                isnm.setInventorySlotContentsNoMark(nbtTagCompound2.getInteger("slot"), new ItemStack(nbtTagCompound2));
            }
        }else{
            NBTTagList list = nbtTagCompound.getTagList(key, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbtTagCompound2 = list.getCompoundTagAt(i);
                inventory.setInventorySlotContents(nbtTagCompound2.getInteger("slot"), new ItemStack(nbtTagCompound2));
            }
        }
    }


    public static void saveInventoryToNBT(IInventory inventory, NBTTagCompound nbtTagCompound, String key) {

        NBTTagList list = new NBTTagList();
        int size = inventory.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack.isEmpty()) {
                NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                nbtTagCompound2.setInteger("slot", i);
                itemStack.writeToNBT(nbtTagCompound2);
                list.appendTag(nbtTagCompound2);
            }
        }
        nbtTagCompound.setTag(key, list);
    }

    public static int getSlotStackLimit(IInventory inventory, int i){
        if(inventory instanceof IInventoryPC){
            return ((IInventoryPC)inventory).getSlotStackLimit(i);
        }
        return inventory.getInventoryStackLimit();
    }

    public static void onTick(IInventory inventory, World world) {
        int size = inventory.getSizeInventory();
        for(int i=0; i<size; i++){
            ItemStack itemStack = inventory.getStackInSlot(i);
            if(itemStack.isEmpty()){
                Item item = itemStack.getItem();
                if(item instanceof IItemPC){
                    ((IItemPC)item).onTick(itemStack, world, inventory, i);
                }
            }
        }
    }

    public static IInventory getInventoryFrom(Object obj) {
        if(obj instanceof IInventoryProvider){
            return ((IInventoryProvider)obj).getInventory();
        }else if(obj instanceof IInventory){
            return (IInventory)obj;
        }else if(obj instanceof EntityPlayer){
            return ((EntityPlayer)obj).inventory;
        }else if(obj instanceof EntityLiving){
            return new PCWrapperInventory(((EntityLiving)obj).getActiveItemStack());
        }
        return null;
    }




    public static IInventory getBlockInventoryAt(IBlockAccess world, float x, float y, float z) {
        IInventory inv = getInventoryFrom(PCUtils.getTileEntity(world, new BlockPos(x, y, z)));
        if(inv != null){
            Block block = (Block) PCUtils.getBlock(world,x, y, z);
            final Block[] chests = {Blocks.CHEST, Blocks.TRAPPED_CHEST};


        }
        return inv;
    }



    private static class EntitySelector implements IEntitySelectorFactory {

        private boolean livingEnabled;

        public EntitySelector(boolean livingEnabled) {
            this.livingEnabled = livingEnabled;
        }


        public boolean isEntityApplicable(Entity entity) {
            return (this.livingEnabled || !(entity instanceof EntityLivingBase)) && getInventoryFrom(entity)!=null;
        }

        @Nonnull
        @Override
        public List<Predicate<Entity>> createPredicates(Map<String, String> arguments, String mainSelector, ICommandSender sender, Vec3d position) {
            return null;
        }
    }



    public static int[] getInvIndexesForSide(IInventory inv, PCDirection side){
        if(side==null)
            return null;
        EnumFacing sideID = side.toForgeDirection();
        if(inv instanceof ISidedInventory){
            return ((ISidedInventory) inv).getSlotsForFace(sideID);
        }
        return null;
    }

    public static int[] makeIndexList(int start, int end){
        int[] indexes = new int[end-start];
        for(int i=0; i<indexes.length; i++){
            indexes[i] = i+start;
        }
        return indexes;
    }

    public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack){
        return getFirstEmptySlot(inv, itemstack, (int[])null);
    }

    public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, PCDirection side){
        return getFirstEmptySlot(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, int[] indexes){
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if (inv.getStackInSlot(i).isEmpty() && inv.isItemValidForSlot(i, itemstack) && getSlotStackLimit(inv, i)>0) {
                    return i;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                if (inv.getStackInSlot(i) .isEmpty()  && inv.isItemValidForSlot(i, itemstack) && getSlotStackLimit(inv, i)>0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack){
        return getSlotWithPlaceFor(inv, itemstack, (int[])null);
    }

    public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, PCDirection side){
        return getSlotWithPlaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, int[] indexes){
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(slot.isItemEqual(itemstack) && getMaxStackSize(slot, inv, i)>slot.getMaxStackSize()){
                        if(inv.isItemValidForSlot(i, itemstack))
                            return i;
                    }
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(slot.isItemEqual(itemstack) && getMaxStackSize(slot, inv, i)>slot.getMaxStackSize()){
                        if(inv.isItemValidForSlot(i, itemstack))
                            return i;
                    }
                }
            }
        }
        return getFirstEmptySlot(inv, itemstack, indexes);
    }

    /**
     * @see PCInventoryUtils#storeItemStackToInventoryFrom(IInventory, ItemStack, int[])
     */
    public static int storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack){
        return storeItemStackToInventoryFrom(inv, itemstack, (int[])null);
    }

    /**
     * @see PCInventoryUtils#storeItemStackToInventoryFrom(IInventory, ItemStack, int[])
     */
    public static int storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, PCDirection side){
        return storeItemStackToInventoryFrom(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    /**
     * @param inv
     * @param itemstack
     * @param indexes
     * @return
     * <br>
     * <br>
     * 0 -> itemStack entirely stored<br>
     * 1 -> itemStack partially stored<br>
     * 2 -> no slot for itemStack<br>
     * <br>
     * previously returned true now means 0 (-> check on 0 for same result)<br>
     */
    public static int storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, int[] indexes){
        int output=2;
        while(itemstack.getMaxStackSize()>0){
            int slot = getSlotWithPlaceFor(inv, itemstack, indexes);
            if(slot<0)
                break;
            storeItemStackToSlot(inv, itemstack, slot);
            output=1;
        }
        if(itemstack.getMaxStackSize()==0) return 0;
        return output;
    }

    public static boolean storeItemStackToSlot(IInventory inv, ItemStack itemstack, int i){
        ItemStack slot = inv.getStackInSlot(i);
        if (slot.isEmpty()) {
            int store = getMaxStackSize(itemstack, inv, i);
            if(store>itemstack.getMaxStackSize()){
                store = itemstack.getMaxStackSize();
            }
            slot = itemstack.copy();
            slot.setCount(store);
            itemstack.setCount(--store)  ;
        }else{
            if(slot.isItemEqual(itemstack)){
                int store = getMaxStackSize(itemstack, inv, i);
                store -= slot.getMaxStackSize();
                if(store>0){
                    if(store>itemstack.getMaxStackSize()){
                        store = itemstack.getMaxStackSize();
                    }
                    itemstack.setCount(--store)  ;
                    slot.setCount(++store);
                }
            }
        }
        inv.setInventorySlotContents(i, slot);
        return itemstack.getMaxStackSize()==0;
    }

    public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack){
        return getInventorySpaceFor(inv, itemstack, (int[])null);
    }

    public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, PCDirection side){
        return getInventorySpaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, int[] indexes){
        int space=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if(itemstack.isEmpty()){
                    if (slot.isEmpty()) {
                        space += getSlotStackLimit(inv, i);
                    }
                }else{
                    int slotStackLimit = getMaxStackSize(itemstack, inv, i);
                    if (slot.isEmpty()) {
                        if(slot.isItemEqual(itemstack) && slotStackLimit>slot.getMaxStackSize()){
                            if(inv.isItemValidForSlot(i, itemstack)){
                                space += slotStackLimit-slot.getMaxStackSize();
                            }
                        }
                    }else{
                        space += slotStackLimit;
                    }
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if(itemstack==null){
                    if (slot == null) {
                        space += getSlotStackLimit(inv, i);
                    }
                }else{
                    int slotStackLimit = getMaxStackSize(itemstack, inv, i);
                    if (slot != null) {
                        if(slot.isItemEqual(itemstack) && slotStackLimit>slot.getMaxStackSize()){
                            if(inv.isItemValidForSlot(i, itemstack)){
                                space += slotStackLimit-slot.getMaxStackSize();
                            }
                        }
                    }else{
                        space += slotStackLimit;
                    }
                }
            }
        }
        return space;
    }

    public static int getInventoryCountOf(IInventory inv, ItemStack itemstack){
        return getInventoryCountOf(inv, itemstack, (int[])null);
    }

    public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, PCDirection side){
        return getInventoryCountOf(inv, itemstack, getInvIndexesForSide(inv, side));
    }

    public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, int[] indexes){
        int count=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(itemstack==null){
                        count += slot.getMaxStackSize();
                    }else{
                        if(slot.isItemEqual(itemstack)){
                            count += slot.getMaxStackSize();
                        }
                    }
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    if(itemstack==null){
                        count += slot.getMaxStackSize();
                    }else{
                        if(slot.isItemEqual(itemstack)){
                            count += slot.getMaxStackSize();
                        }
                    }
                }
            }
        }
        return count;
    }

    public static int getInventoryFreeSlots(IInventory inv){
        return getInventoryFreeSlots(inv, (int[])null);
    }

    public static int getInventoryFreeSlots(IInventory inv, PCDirection side){
        return getInventoryFreeSlots(inv, getInvIndexesForSide(inv, side));
    }

    public static int getInventoryFreeSlots(IInventory inv, int[] indexes){
        int freeSlots=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot == null) {
                    freeSlots++;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot == null) {
                    freeSlots++;
                }
            }
        }
        return freeSlots;
    }

    public static int getInventoryFullSlots(IInventory inv){
        return getInventoryFullSlots(inv, (int[])null);
    }

    public static int getInventoryFullSlots(IInventory inv, PCDirection side){
        return getInventoryFullSlots(inv, getInvIndexesForSide(inv, side));
    }

    public static int getInventoryFullSlots(IInventory inv, int[] indexes){
        int fullSlots=0;
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    fullSlots++;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack slot = inv.getStackInSlot(i);
                if (slot != null) {
                    fullSlots++;
                }
            }
        }
        return fullSlots;
    }

    public static void moveStacks(IInventory from, IInventory to){
        moveStacks(from, (int[])null, to, (int[])null);
    }

    public static void moveStacks(IInventory from, PCDirection fromSide, IInventory to, PCDirection toSide) {
        moveStacks(from, getInvIndexesForSide(from, fromSide), to, toSide);
    }

    public static void moveStacks(IInventory from, int[] indexes, IInventory to, PCDirection toSide) {
        moveStacks(from, indexes, to, getInvIndexesForSide(to, toSide));
    }

    public static void moveStacks(IInventory from, PCDirection fromSide, IInventory to, int[] indexes) {
        moveStacks(from, getInvIndexesForSide(from, fromSide), to, indexes);
    }

    public static void moveStacks(IInventory from, int[] fromIndexes, IInventory to, int[] toIndexes) {
        if(fromIndexes==null){
            int size = from.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if (from.getStackInSlot(i) != null) {

                    storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);

                    if (from.getStackInSlot(i) != null && from.getStackInSlot(i).getMaxStackSize() <= 0) {
                        from.setInventorySlotContents(i, null);
                    }
                }
            }
        }else{
            for (int j = 0; j < fromIndexes.length; j++) {
                int i=fromIndexes[j];
                if (from.getStackInSlot(i) != null) {

                    storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);

                    if (from.getStackInSlot(i) != null && from.getStackInSlot(i).getMaxStackSize() <= 0) {
                        from.setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }

    public static ItemStack[] groupStacks(ItemStack[] input) {
        List<ItemStack> list = stacksToList(input);
        groupStacks(list);
        return stacksToArray(list);
    }

    public static void groupStacks(List<ItemStack> input) {
        if (input == null) {
            return;
        }

        for (ItemStack st1 : input) {
            if (st1 != null) {
                for (ItemStack st2 : input) {
                    if (st2 != null && st2.isItemEqual(st1)) {
                        int movedToFirst = Math.min(st2.getMaxStackSize(), st1.getMaxStackSize()
                                - st1.getMaxStackSize());

                        if (movedToFirst <= 0) {
                            break;
                        }
                        int i = st1.getMaxStackSize();
                        int i2 = st1.getMaxStackSize();
                        i += movedToFirst;
                        i2 -= movedToFirst;
                    }
                }
            }
        }

        ArrayList<ItemStack> copy = new ArrayList<ItemStack>(input);

        for (int i = copy.size() - 1; i >= 0; i--) {
            if (copy.get(i) == null || copy.get(i).getMaxStackSize() <= 0) {
                input.remove(i);
            }
        }
    }

    public static List<ItemStack> stacksToList(ItemStack[] stacks) {
        ArrayList<ItemStack> myList = new ArrayList<ItemStack>();
        Collections.addAll(myList, stacks);
        return myList;
    }

    public static ItemStack[] stacksToArray(List<ItemStack> stacks) {
        return stacks.toArray(new ItemStack[stacks.size()]);
    }

    public static void dropInventoryContent(IInventory inventory, World world, double x, double y, double z) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
            int size = inventory.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if(inventory instanceof IInventoryPC){
                    if(!((IInventoryPC)inventory).canDropStack(i))
                        continue;
                }
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (itemStack.isEmpty()) {
                    inventory.setInventorySlotContents(i, null);
                    PCUtils.spawnItem(world, x, y, z, itemStack);
                }
            }
        }
    }

    public static int useFuel(IInventory inv, World world, PCVec3 pos) {
        return useFuel(inv, (int[])null, world, pos);
    }

    public static int useFuel(IInventory inv, PCDirection side, World world, PCVec3 pos) {
        return useFuel(inv, getInvIndexesForSide(inv, side), world, pos);
    }

    public static int useFuel(IInventory inv, int[] indexes, World world, PCVec3 pos) {
        if(indexes==null){
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack is = inv.getStackInSlot(i);
                int fuel = PCUtils.getBurnTime(is);
                if (fuel > 0) {
                    inv.decrStackSize(i, 1);
                    ItemStack container = is.getItem().getContainerItem(is);
                    if (container != null) {
                        storeItemStackToInventoryFrom(inv, container, indexes);
                        if (container.getMaxStackSize() > 0) {
                            PCUtils.spawnItem(world, pos, container);
                        }
                    }
                    return fuel;
                }
            }
        }else{
            for (int j = 0; j < indexes.length; j++) {
                int i=indexes[j];
                ItemStack is = inv.getStackInSlot(i);
                int fuel = PCUtils.getBurnTime(is);
                if (fuel > 0) {
                    inv.decrStackSize(i, 1);
                    ItemStack container = is.getItem().getContainerItem(is);
                    if (container != null) {
                        storeItemStackToInventoryFrom(inv, container, indexes);
                        if (container.getMaxStackSize() > 0) {
                            PCUtils.spawnItem(world, pos, container);
                        }
                    }
                    return fuel;
                }
            }
        }
        return 0;
    }

    public static ItemStack decreaseStackSize(ItemStack[] inventoryContents, int slot, int amount){
        if (inventoryContents[slot] == null) return null;
        ItemStack itemstack;
        if (inventoryContents[slot].getMaxStackSize() <= amount) {
            itemstack = inventoryContents[slot];
            inventoryContents[slot] = null;
            return itemstack;
        }
        itemstack = inventoryContents[slot].splitStack(amount);
        if (inventoryContents[slot].getMaxStackSize() == 0) {
            inventoryContents[slot] = null;
        }
        return itemstack;
    }

    public static ItemStack getStackInSlot(ItemStack[] inventoryContents, int slot){
        if(inventoryContents==null || inventoryContents.length<=slot) return null;
        return inventoryContents[slot];
    }

    public static int getMaxStackSize(ItemStack itemStack, IInventory inv, int i){
        if(inv instanceof IInventorySizeOverriderPC){
            return ((IInventorySizeOverriderPC)inv).getMaxStackSize(itemStack, i);
        }
        int maxStack = itemStack.getMaxStackSize();
        int slotStack = getSlotStackLimit(inv, i);
        return maxStack>slotStack?slotStack:maxStack;
    }


    public static int getMaxStackSize(ItemStack itemStack, Slot slot) {
        if(slot.inventory instanceof IInventorySizeOverriderPC){
            return ((IInventorySizeOverriderPC)slot.inventory).getMaxStackSize(itemStack, slot.getSlotIndex());
        }
        int maxStack = itemStack.getMaxStackSize();
        int maxSlot = slot.getSlotStackLimit();
        return maxStack>maxSlot?maxSlot:maxStack;
    }

    public static boolean itemStacksEqual(ItemStack one, ItemStack two){
        if(one == null && two == null) return true;
        if(one == null || two == null) return false;
        if(!(one.getItem()==two.getItem())) return false;
        if(one.getItemDamage()==PCUtils.WILDCARD_VALUE || two.getItemDamage()==PCUtils.WILDCARD_VALUE)
            return true;
        if(!(one.getItemDamage()==two.getItemDamage())) return false;
        return ItemStack.areItemStackTagsEqual(one, two);
    }



}
