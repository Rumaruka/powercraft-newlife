package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCRectI;
import com.rumaruka.powercraft.api.PCVec2;
import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import com.rumaruka.powercraft.api.renderer.PCOpenGL;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@SideOnly(Side.CLIENT)
public class PCGresWindow extends PCGresContainer {

    private static final String textureName = "Window";

    private List<PCGresWindowSideTab> sideTabs = new ArrayList<PCGresWindowSideTab>();

    public PCGresWindow(String title) {
        this.frame.setTo(new PCRectI(4, 4 + fontRenderer.getStringSize(title).y + 2, 4, 4));
        setText(title);
        this.fontColors[0] = 0x404040;
        this.fontColors[1] = 0x404040;
        this.fontColors[2] = 0x404040;
        this.fontColors[3] = 0x404040;
    }

    public void addSideTab(PCGresWindowSideTab sideTab) {
        if (!this.sideTabs.contains(sideTab)) {
            this.sideTabs.add(sideTab);
            sideTab.setParent(this);
            sideTab.setSize(sideTab.getMinSize());
            notifyChange();
        }
    }


    public void removeSideTab(PCGresWindowSideTab sideTab) {
        this.sideTabs.remove(sideTab);
        sideTab.setParent(null);
        notifyChange();
    }


    public void removeAllSideTabs() {
        while (!this.sideTabs.isEmpty())
            this.sideTabs.remove(0).setParent(null);
    }


    public boolean isSideTab(PCGresWindowSideTab sideTab) {
        return this.sideTabs.contains(sideTab);
    }

    @Override
    protected PCVec2I calculateMinSize() {
        return getTextureMinSize(textureName).max(fontRenderer.getStringSize(this.text).x + 8, 0);
    }


    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1, -1);
    }


    @Override
    protected PCVec2I calculatePrefSize() {

        return new PCVec2I(-1, -1);
    }

    private void calculateMaxSideTabX(){
        for (PCGresWindowSideTab sideTab : this.sideTabs) {
            if(sideTab.getSize().x+4>this.frame.x || sideTab.getSize().x+4>this.frame.width){
                this.frame.x = this.frame.width = sideTab.getSize().x+4;
            }
        }
    }

    @Override
    protected void notifyChange() {
        calculateMaxSideTabX();
        super.notifyChange();
        PCVec2I pos = new PCVec2I(this.rect.width-this.frame.width+4, 4);
        for (PCGresWindowSideTab sideTab : this.sideTabs) {
            sideTab.setLocation(pos);
            pos.y += sideTab.getSize().y+2;
        }
    }

    @Override
    protected void setParentVisible(boolean visible) {
        super.setParentVisible(this.enabled);
        for (PCGresWindowSideTab sideTab : this.sideTabs) {
            sideTab.setParentVisible(visible);
        }
    }

    @Override
    protected void setParentEnabled(boolean enabled) {
        super.setParentEnabled(enabled);
        for (PCGresWindowSideTab sideTab : this.sideTabs) {
            sideTab.setParentEnabled(this.visible);
        }
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
        drawTexture(textureName, this.frame.x - 4, 0, this.rect.width - this.frame.width - this.frame.x + 8, this.rect.height);
        drawString(this.text, this.frame.x, 4, this.rect.width - this.frame.width - this.frame.x, PCGresAlign.H.CENTER, false);
    }

    @SuppressWarnings("hiding")
    @Override
    protected void doPaint(PCVec2 offset, PCRect scissorOld, double scale, int displayHeight, float timeStamp, float zoom) {
        if (this.visible) {
            PCRect rect = new PCRect(this.rect);
            rect.x += offset.x;
            rect.y += offset.y;
            PCRect scissor = setDrawRect(scissorOld, rect, scale, displayHeight, zoom);
            if(scissor==null)
                return;
            PCOpenGL.pushMatrix();
            GL11.glTranslatef(this.rect.x, this.rect.y, 0);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            paint(scissor, scale, displayHeight, timeStamp, zoom);
            doDebugRendering(0, 0, rect.width, rect.height);
            rect.x += this.frame.x*zoom;
            rect.y += this.frame.y*zoom;
            GL11.glTranslatef(this.frame.x, this.frame.y, 0);
            PCVec2 noffset = rect.getLocation();
            rect.width -= this.frame.x + this.frame.width;
            rect.height -= this.frame.y + this.frame.height;
            PCRect nScissor = setDrawRect(scissor, rect, scale, displayHeight, zoom);
            if(nScissor!=null){
                ListIterator<PCGresComponent> iterator = this.children.listIterator(this.children.size());
                while(iterator.hasPrevious()){
                    iterator.previous().doPaint(noffset, nScissor, scale, displayHeight, timeStamp, zoom);
                }
            }
            GL11.glTranslatef(-this.frame.x, -this.frame.y, 0);
            noffset = noffset.sub(this.frame.getLocationF());
            ListIterator<PCGresWindowSideTab> iterator2 = this.sideTabs.listIterator(this.sideTabs.size());
            while(iterator2.hasPrevious()){
                iterator2.previous().doPaint(noffset, scissor, scale, displayHeight, timeStamp, zoom);
            }
            PCOpenGL.popMatrix();
        }
    }

    @SuppressWarnings("hiding")
    @Override
    public PCGresComponent getComponentAtPosition(PCVec2I position) {
        PCGresComponent component = super.getComponentAtPosition(position);
        if(component!=this) return component;
        if (this.visible) {
            for (PCGresWindowSideTab sideTab:this.sideTabs) {
                PCRectI rect = sideTab.getRect();
                if (rect.contains(position)){
                    component = sideTab.getComponentAtPosition(position.sub(rect.getLocation()));
                    if (component != null) return component;
                }
            }
            PCRectI rect = new PCRectI(this.rect);
            rect.x = this.frame.x;
            rect.y = this.frame.y;
            rect.width -= this.frame.x+this.frame.width;
            rect.height -= this.frame.y+this.frame.height;
            if(rect.contains(position))
                return this;
        }
        return null;
    }

    @Override
    protected void onTick() {
        super.onTick();
        for(PCGresWindowSideTab sideTab:this.sideTabs){
            sideTab.onTick();
        }
    }

    @Override
    protected void onDrawTick(float timeStamp) {
        super.onDrawTick(timeStamp);
        for(PCGresWindowSideTab sideTab:this.sideTabs){
            sideTab.onDrawTick(timeStamp);
        }
    }

    @SuppressWarnings("hiding")
    @Override
    public Slot getSlotAtPosition(PCVec2I position) {
        Slot slot = super.getSlotAtPosition(position);
        if (slot != null) return slot;
        if (this.visible) {
            for (PCGresWindowSideTab sideTab:this.sideTabs) {
                PCRectI rect = sideTab.getRect();
                slot = sideTab.getSlotAtPosition(position.sub(rect.getLocation()));
                if (slot != null) return slot;
            }
        }
        return null;
    }

    @Override
    protected void tryActionOnKeyTyped(char key, int keyCode, boolean repeat, PCGresHistory history) {
        super.tryActionOnKeyTyped(key, keyCode, repeat, history);
        for(PCGresWindowSideTab sideTab:this.sideTabs){
            sideTab.tryActionOnKeyTyped(key, keyCode, repeat, null);
        }
    }
}
