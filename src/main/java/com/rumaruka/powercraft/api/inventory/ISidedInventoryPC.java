package com.rumaruka.powercraft.api.inventory;

public interface ISidedInventoryPC extends IInventoryPC {

    void setSideGroup(int i,int k);
    int getGroupCount();
    int getSideGroup(int i);

}
