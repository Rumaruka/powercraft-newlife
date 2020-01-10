package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.*;
import com.rumaruka.powercraft.api.gres.PCGresAlign.H;
import com.rumaruka.powercraft.api.gres.PCGresAlign.V;
import com.rumaruka.powercraft.api.gres.events.IGresEventListener;
import com.rumaruka.powercraft.api.gres.events.PCGresEvent;
import com.rumaruka.powercraft.api.gres.events.PCGresMouseButtonEvent;
import com.rumaruka.powercraft.api.gres.events.PCGresMouseMoveEvent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import com.rumaruka.powercraft.api.gres.layot.PCGresLayoutVertical;
import com.rumaruka.powercraft.api.inventory.ISidedInventoryPC;
import com.rumaruka.powercraft.api.redstone.PCRedstoneWorkType;

import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

@SideOnly(Side.CLIENT)
public class PCGresWindowSideTab extends PCGresContainer {

    private static PCGresWindowSideTab openSideTab;
    private final PCVec2I size = new PCVec2I(20, 20);
    private final PCVec3 color = new PCVec3(1, 1, 1);
    private PCGresDisplayObject displayObject;

    private float time;

    public PCGresWindowSideTab(String text) {
        super(text);
        this.frame.x = 2;
        this.frame.y = 20;
        this.frame.width = 2;
        this.frame.height = 2;
    }

    public PCGresWindowSideTab(String text, PCGresDisplayObject displayObject) {
        this(text);
        setDisplayObject(displayObject);
    }

    public void setColor(PCVec3 color) {
        this.color.setTo(color);
    }

    public void setDisplayObject(PCGresDisplayObject displayObject) {
        this.displayObject = displayObject;
    }

    public PCGresDisplayObject getDisplayObject() {
        return this.displayObject;
    }

    @Override
    protected void setParent(PCGresContainer parent) {
        if (parent instanceof PCGresWindow) {
            this.parent = parent;
            this.parentVisible = parent.isRecursiveVisible();
            this.parentEnabled = parent.isRecursiveEnabled();
        } else if (parent == null) {
            this.parent = null;
            this.parentVisible = true;
            this.parentEnabled = true;
        }
    }

    @Override
    protected PCVec2I calculateMinSize() {
        return new PCVec2I(20, 20);
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(100, 100);
    }

    @Override
    protected PCVec2I calculatePrefSize() {
        return new PCVec2I(20, 20);
    }

    @Override
    public PCVec2 getRealLocation() {
        if (this.parent == null) {
            return this.rect.getLocationF();
        }
        return this.rect.getLocationF().add(this.parent.getRealLocation());
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
        GL11.glColor3d(this.color.x, this.color.y, this.color.z);
        drawTexture("Frame", -2, 0, this.rect.width + 2, this.rect.height);
        GL11.glColor3f(1, 1, 1);
        if (this.displayObject != null)
            this.displayObject.draw(2, 1, 16, 16);
        PCVec2 loc = getRealLocation();
        PCRect s = setDrawRect(scissor, new PCRect(loc.x + 20, loc.y + 2, this.rect.width - 22, 16), scale, displayHeight, zoom);
        if (s != null)
            drawString(this.text, 20, 2, 100, 16, H.LEFT, V.CENTER, false);
    }

    private boolean update = true;

    @Override
    protected void notifyChange() {
        if (this.update) {
            updateMinSize();
            updatePrefSize();
            updateMaxSize();
        }
        notifyParentOfChange();
        if (this.update) {
            this.rect.setSize(getPrefSize().max(new PCVec2I(fontRenderer.getStringSize(this.text).x + 24, 20)));
            updateLayout();
        }
        this.rect.setSize(this.size);
    }

    @Override
    public PCRectI getChildRect() {
        if (this.update) {
            this.rect.setSize(getPrefSize().max(new PCVec2I(fontRenderer.getStringSize(this.text).x + 24, 20)));
        }
        PCRectI r = super.getChildRect();
        this.rect.setSize(this.size);
        return r;
    }

    @Override
    protected boolean handleMouseButtonClick(PCVec2I mouse, int buttons, int eventButton, PCGresHistory history) {
        if (openSideTab == this) {
            openSideTab = null;
        } else {
            openSideTab = this;
        }
        return true;

    }

    private static Object getTypeDisp(PCRedstoneWorkType type) {
        if (type == null)
            return PCGres.getGresTexture("I_OFF");
        switch (type) {
            case ALWAYS:
                return PCGres.getGresTexture("I_ON");

            case ON_HI_FLANK:
                return PCGres.getGresTexture("I_HFL");
            case ON_LOW_FLANK:
                return PCGres.getGresTexture("I_LFL");
            case ON_OFF:
                return PCGres.getGresTexture("Redstone_Torch_Off");
            case ON_ON:
                return Blocks.REDSTONE_TORCH;
            default:
                return PCGres.getGresTexture("I_OFF");
        }
    }

    @Override
    protected void onDrawTick(float timeStamp) {
        this.time += timeStamp;
        int num = (int) (this.time / 0.01);
        if (num > 0) {
            this.time -= num * 0.01;
            if (openSideTab == this) {

                this.size.setTo(this.size.add(num).min(getPrefSize().max(new PCVec2I(fontRenderer.getStringSize(this.text).x + 24, 20))));
            } else {
                this.size.setTo(this.size.sub(num).max(20));
            }
            this.update = false;
            setSize(this.size);
            this.update = true;
        }
        super.onDrawTick(timeStamp);
    }
    public static PCGresWindowSideTab createRedstoneSideTab(PCTileEntityAPI tileEntity){
        PCGresWindowSideTab sideTab = new PCGresWindowSideTab("Redstone", new PCGresDisplayObject(Items.REDSTONE));
        sideTab.setColor(new PCVec3(1.0, 0.2, 0.2));
        sideTab.setLayout(new PCGresLayoutVertical());
        PCRedstoneWorkType[] types = tileEntity.getAllowedRedstoneWorkTypes();
        if(types==null || types.length==0)
            return null;
        Object[] disps = new Object[types.length];
        int act = 0;
        for(int i=0; i<types.length; i++){
            disps[i] = getTypeDisp(types[i]);
            if(types[i]==tileEntity.getRedstoneWorkType()){
                act = i;
            }
        }
        PCGresDisplayObject dO = new PCGresDisplayObject(disps);
        PCGresDisplay d = new PCGresDisplay(dO);
        dO.setActiveDisplayObjectIndex(act);
        d.addEventListener(new RedstoneConfigEventListener(tileEntity, types));
        d.setBackground(new PCGresDisplayObject(PCGres.getGresTexture("Slot")));
        d.setFrame(new PCRectI(1, 1, 1, 1));
        sideTab.add(d);
        sideTab.add(new PCGresLabel("State: ON"));
        return sideTab;
    }

    private static PCVec2I[] SIDE_POS = {new PCVec2I(18, 35), new PCVec2I(18, 1), new PCVec2I(18, 18), new PCVec2I(35, 35), new PCVec2I(35, 18), new PCVec2I(1, 18)};

    public static PCGresWindowSideTab createIOConfigurationSideTab(ISidedInventoryPC inventory){
        PCGresWindowSideTab sideTab = new PCGresWindowSideTab("Configuration", new PCGresDisplayObject(PCGres.getGresTexture("IO_CONF")));
        sideTab.setColor(new PCVec3(0.2, 1.0, 0.2));
        sideTab.setLayout(new PCGresLayoutVertical());
        PCGresFrame frame = new PCGresFrame();
        frame.setMinSize(new PCVec2I(54, 54));
        sideTab.add(frame);
        Object[] obj = new Object[inventory.getGroupCount()+1];
        obj[0] = PCGres.getGresTexture("NULL");
        for(int i=1; i<obj.length; i++){
            obj[i] = PCGres.getGresTexture("F"+i);
        }
        PCGresDisplay[] sides = new PCGresDisplay[6];
        PCGresDisplayObject dO;
        IOConfigEventListener eventListener = new IOConfigEventListener(inventory, sides);
        for(int i=0; i<6; i++){
            if(i==PCDirection.NORTH.ordinal()){
                frame.add(sides[i] = new PCGresDisplay(new PCGresDisplayObject(obj)));
            }else{
                frame.add(sides[i] = new PCGresDisplay(dO = new PCGresDisplayObject(obj)));
                dO.setActiveDisplayObjectIndex(inventory.getSideGroup(i)+1);
                sides[i].addEventListener(eventListener);
            }
            sides[i].setLocation(SIDE_POS[i]);
            sides[i].setSize(new PCVec2I(16, 16));
        }
        return sideTab;
    }

    public static PCGresWindowSideTab createEnergySideTab(EnergyPerTick energy){
        PCGresWindowSideTab sideTab = new PCGresWindowSideTab("Energy", new PCGresDisplayObject(PCGres.getGresTexture("Energy")));
        sideTab.setColor(new PCVec3(0.2, 0.2, 1.0));
        sideTab.setLayout(new PCGresLayoutVertical());
        sideTab.add(energy.label = new PCGresLabel("Energy: 0 E/T"));
        return sideTab;
    }

    public static class EnergyPerTick{

        PCGresLabel label;

        public void setToValue(float value){
            if(this.label!=null){
                this.label.setText("Energy: "+new DecimalFormat("#.##").format(value)+" E/T");
            }
        }

    }
    private static class RedstoneConfigEventListener implements IGresEventListener{
        private PCTileEntityAPI tileEntity;
        PCRedstoneWorkType types[];
        public RedstoneConfigEventListener(PCTileEntityAPI tileEntity, PCRedstoneWorkType types[]){
            this.tileEntity = tileEntity;
            this.types = types;
        }
        @Override
        public void onEvent(PCGresEvent event) {
            if(event instanceof PCGresMouseButtonEvent){
                PCGresMouseButtonEvent bEvent = (PCGresMouseButtonEvent) event;
                if(bEvent.getEvent()== PCGresMouseButtonEvent.Event.CLICK){
                    PCGresDisplay disp = (PCGresDisplay) event.getComponent();
                    PCRedstoneWorkType rwt = this.types[disp.getDisplayObject().getActiveDisplayObjectIndex()];
                    this.tileEntity.setRedstoneWorkType(rwt);
                }
            }

        }
    }
    private static class IOConfigEventListener implements IGresEventListener{

        private ISidedInventoryPC inventory;
        private PCGresDisplay[] sides;

        public IOConfigEventListener(ISidedInventoryPC inventory, PCGresDisplay[] sides){
            this.inventory = inventory;
            this.sides = sides;
        }

        @Override
        public void onEvent(PCGresEvent event) {
            if(event instanceof PCGresMouseButtonEvent){
                PCGresMouseButtonEvent bEvent = (PCGresMouseButtonEvent) event;
                if(bEvent.getEvent()== PCGresMouseButtonEvent.Event.CLICK){
                    for(int i=0; i<this.sides.length; i++){
                        if(this.sides[i] == bEvent.getComponent()){
                            this.inventory.setSideGroup(i, this.sides[i].getDisplayObject().getActiveDisplayObjectIndex()-1);
                            break;
                        }
                    }
                }
            }
        }

    }
}
