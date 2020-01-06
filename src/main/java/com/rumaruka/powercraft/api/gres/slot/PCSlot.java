package com.rumaruka.powercraft.api.gres.slot;

import com.rumaruka.powercraft.api.inventory.IInventoryBackgroundPC;
import com.rumaruka.powercraft.api.inventory.IInventoryPC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PCSlot extends Slot {

    private ItemStack backgroundStack;
    private boolean renderGrayWhenEmpty;


    public PCSlot(IInventory inventory, int slotIndex) {

        super(inventory, slotIndex, 0, 0);
        this.slotNumber = -1;
    }


    @Override
    public boolean isItemValid(ItemStack itemStack) {

        return this.inventory.isItemValidForSlot(getSlotIndex(), itemStack);
    }


    @Override
    public int getSlotStackLimit() {

        if (this.inventory instanceof IInventoryPC) {
            return ((IInventoryPC) this.inventory).getSlotStackLimit(getSlotIndex());
        }
        return super.getSlotStackLimit();
    }


    @Override
    public boolean canTakeStack(EntityPlayer entityPlayer) {

        if (this.inventory instanceof IInventoryPC) {
            return ((IInventoryPC) this.inventory).canTakeStack(getSlotIndex(), entityPlayer);
        }
        return super.canTakeStack(entityPlayer);
    }

    public boolean canDragIntoSlot() {

        if (this.inventory instanceof IInventoryPC) {
            return ((IInventoryPC) this.inventory).canBeDragged(getSlotIndex());
        }
        return true;
    }

    public void setBackgroundStack(ItemStack backgroundStack) {

        this.backgroundStack = backgroundStack;
    }


    public ItemStack getBackgroundStack() {
        if(this.inventory instanceof IInventoryBackgroundPC){
            return ((IInventoryBackgroundPC)this.inventory).getBackgroundStack(getSlotIndex());
        }
        return this.backgroundStack;
    }


    public void setRenderGrayWhenEmpty(boolean renderGrayWhenEmpty) {

        this.renderGrayWhenEmpty = renderGrayWhenEmpty;
    }


    public boolean renderGrayWhenEmpty() {
        if(this.inventory instanceof IInventoryBackgroundPC){
            return ((IInventoryBackgroundPC)this.inventory).renderGrayWhenEmpty(getSlotIndex());
        }
        return this.renderGrayWhenEmpty;
    }

    public int[] getAppliedSides() {
        if(this.inventory instanceof IInventoryPC){
            return ((IInventoryPC)this.inventory).getAppliedSides(getSlotIndex());
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int v) {
        ItemStack itemStack = super.decrStackSize(v);
        return itemStack.getMaxStackSize()==0?null:itemStack;
    }
}
