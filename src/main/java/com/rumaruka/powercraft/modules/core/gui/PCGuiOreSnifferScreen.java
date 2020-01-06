package com.rumaruka.powercraft.modules.core.gui;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.gres.*;
import com.rumaruka.powercraft.api.gres.events.IGresEventListener;
import com.rumaruka.powercraft.api.gres.events.PCGresEvent;
import com.rumaruka.powercraft.api.gres.layot.PCGresLayoutVertical;
import com.rumaruka.powercraft.api.gres.slot.PCSlot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PCGuiOreSnifferScreen implements IGresGui, IGresEventListener {
    private PCGresSlider slider;
    private PCGresLabel distanceL;
    private PCGresInventory inv;
    private PCDirection vector;
    private PCVec3I[][] startpos;
    private World world;
    private PCVec3I start;
    private static final int RANGE = 16;



    private void rotateRight() {
        PCVec3I swap = this.startpos[0][0];
        this.startpos[0][0] = this.startpos[0][1];
        this.startpos[0][1] = this.startpos[0][2];
        this.startpos[0][2] = this.startpos[1][2];
        this.startpos[1][2] = this.startpos[2][2];
        this.startpos[2][2] = this.startpos[2][1];
        this.startpos[2][1] = this.startpos[2][0];
        this.startpos[2][0] = this.startpos[1][0];
        this.startpos[1][0] = swap;
    }

    public PCGuiOreSnifferScreen(EntityPlayer player, PCVec4I vec4){
        this.startpos = new PCVec3I[3][3];
        this.world = player.world;
        this.start = new PCVec3I(vec4.x, vec4.y, vec4.z);
        this.vector = PCDirection.fromSide(vec4.side.getIndex()).getOpposite();

        int l = PCMathHelper.floor_double(((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;

        if (this.vector == PCDirection.DOWN) {

            this.startpos[0][0] = this.start.offset(-1, 0, -1);
            this.startpos[1][0] = this.start.offset(0, 0, -1);
            this.startpos[2][0] = this.start.offset(1, 0, -1);
            this.startpos[0][1] = this.start.offset(-1, 0, 0);
            this.startpos[1][1] = this.start;
            this.startpos[2][1] = this.start.offset(1, 0, 0);
            this.startpos[0][2] = this.start.offset(-1, 0, 1);
            this.startpos[1][2] = this.start.offset(0, 0, 1);
            this.startpos[2][2] = this.start.offset(1, 0, 1);

            l = 3 - l;
            l += 3;
            for (int i = 0; i < l; i++) {
                rotateRight();
                rotateRight();
            }


        } else if (this.vector == PCDirection.EAST) {
            this.startpos[0][0] = this.start.offset(0, 1, -1);
            this.startpos[1][0] = this.start.offset(0, 1, 0);
            this.startpos[2][0] = this.start.offset(0, 1, 1);
            this.startpos[0][1] = this.start.offset(0, 0, -1);
            this.startpos[1][1] = this.start;
            this.startpos[2][1] = this.start.offset(0, 0, 1);
            this.startpos[0][2] = this.start.offset(0, -1, -1);
            this.startpos[1][2] = this.start.offset(0, -1, 0);
            this.startpos[2][2] = this.start.offset(0, -1, 1);
        } else if (this.vector == PCDirection.NORTH) {
            this.startpos[0][0] = this.start.offset(-1, 1, 0);
            this.startpos[1][0] = this.start.offset(0, 1, 0);
            this.startpos[2][0] = this.start.offset(1, 1, 0);
            this.startpos[0][1] = this.start.offset(-1, 0, 0);
            this.startpos[1][1] = this.start;
            this.startpos[2][1] = this.start.offset(1, 0, 0);
            this.startpos[0][2] = this.start.offset(-1, -1, 0);
            this.startpos[1][2] = this.start.offset(0, -1, 0);
            this.startpos[2][2] = this.start.offset(1, -1, 0);


        } else if (this.vector == PCDirection.UP) {
            this.startpos[0][2] = this.start.offset(-1, 0, -1);
            this.startpos[1][2] = this.start.offset(0, 0, -1);
            this.startpos[2][2] = this.start.offset(1, 0, -1);
            this.startpos[0][1] = this.start.offset(-1, 0, 0);
            this.startpos[1][1] = this.start;
            this.startpos[2][1] = this.start.offset(1, 0, 0);
            this.startpos[0][0] = this.start.offset(-1, 0, 1);
            this.startpos[1][0] = this.start.offset(0, 0, 1);
            this.startpos[2][0] = this.start.offset(1, 0, 1);

            l += 2;
            for (int i = 0; i < l; i++) {
                rotateRight();
                rotateRight();
            }

        } else if (this.vector == PCDirection.WEST) {
            this.startpos[2][0] = this.start.offset(0, 1, -1);
            this.startpos[1][0] = this.start.offset(0, 1, 0);
            this.startpos[0][0] = this.start.offset(0, 1, 1);
            this.startpos[2][1] = this.start.offset(0, 0, -1);
            this.startpos[1][1] = this.start;
            this.startpos[0][1] = this.start.offset(0, 0, 1);
            this.startpos[2][2] = this.start.offset(0, -1, -1);
            this.startpos[1][2] = this.start.offset(0, -1, 0);
            this.startpos[0][2] = this.start.offset(0, -1, 1);
        } else if (this.vector == PCDirection.SOUTH) {
            this.startpos[2][0] = this.start.offset(-1, 1, 0);
            this.startpos[1][0] = this.start.offset(0, 1, 0);
            this.startpos[0][0] = this.start.offset(1, 1, 0);
            this.startpos[2][1] = this.start.offset(-1, 0, 0);
            this.startpos[1][1] = this.start;
            this.startpos[0][1] = this.start.offset(1, 0, 0);
            this.startpos[2][2] = this.start.offset(-1, -1, 0);
            this.startpos[1][2] = this.start.offset(0, -1, 0);
            this.startpos[0][2] = this.start.offset(1, -1, 0);
        }
    }
    @Override
    public void initGui(PCGresGuiHandler gui) {
        PCGresWindow w = new PCGresWindow(PCLangHelper.tr("item.ItemOreSniffer.name"));
        w.setLayout(new PCGresLayoutVertical());
        PCGresContainer vg = new PCGresGroupContainer().setLayout(new PCGresLayoutVertical());

        vg.add(new PCGresLabel(PCLangHelper.tr("PCco.gui.sniffer.distance")));

        vg.add(this.slider = new PCGresSlider().setSteps(RANGE-1));
        this.slider.setFill(PCGresAlign.Fill.HORIZONTAL);
        this.slider.setEditable(true);
        this.slider.addEventListener(this);
        vg.add(this.distanceL = new PCGresLabel("0"));
        w.add(vg);

        w.add(this.inv = new PCGresInventory(3, 3));
        IInventory inven = new InventoryBasic("", true, 1);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                this.inv.setSlot(x, y, new PCSlot(inven, 0));
            }
        }
        gui.add(w);

        loadBlocksForDistance(0);
    }
    private void loadBlocksForDistance(int distance) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {

                PCVec3I pos = this.startpos[x][y].offset(new PCVec3I(x,y,0).offset(this.vector).mul(distance));

                ItemStack stack = getItemStackFrom(pos);

                ((PCSlot) this.inv.getSlot(x, y)).setBackgroundStack(stack);
            }
        }
    }
    private ItemStack getItemStackFrom(PCVec3I pos){
        Block block = (Block) PCUtils.getBlock(this.world, new BlockPos(pos.x,pos.y,pos.z));
        IBlockState state = (IBlockState) block.getBlockState();
        if (block.isAir(state,this.world, new BlockPos(pos.x, pos.y, pos.z))) {
            return null;
        }
        ItemStack itemStack = block.getItem(this.world, new BlockPos(pos.x, pos.y, pos.z),state);

        if (itemStack.isEmpty())
        {
            return null;
        }
        Item item = itemStack.getItem();
        Block b = item instanceof ItemBlock && !block.isLeaves(state,this.world, new BlockPos(pos.x, pos.y, pos.z)) ? Block.getBlockFromItem(item) : block;
        return itemStack;
    }

    @Override
    public void onEvent(PCGresEvent event) {
        if (event.getComponent() == this.slider) {
            int distance = (int) this.slider.getProgress();
            this.distanceL.setText(""+distance);
            loadBlocksForDistance(distance);
        }
    }
}
