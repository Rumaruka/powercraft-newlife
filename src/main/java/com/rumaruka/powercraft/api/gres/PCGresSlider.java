package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.events.PCGresMouseWheelEvent;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCGresSlider extends PCGresComponent {

    private static final String textureName = "Button";
    private static final int SLIDER_SIZE = 7;

    private float progress;
    private int steps = 100;

    public PCGresSlider(){

    }

    public float getProgress() {
        return progress;
    }

    public PCGresSlider setProgress(float progress) {
        this.progress=progress;
        return this;
    }

    public int getSteps() {
        return steps;
    }

    public PCGresSlider setSteps(int steps) {
        this.steps = steps;
        return this;
    }

    @Override
    protected PCVec2I calculateMinSize() {
        return getTextureMinSize(textureName);
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1, -1);
    }

    @Override
    protected PCVec2I calculatePrefSize() {
        return getTextureDefaultSize(textureName);
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
        drawTexture(textureName, 0, 0, this.rect.width, this.rect.height, 3);
        int x = (int) (this.progress*(this.rect.width-SLIDER_SIZE)/this.steps+0.5);
        drawTexture(textureName, x, 0, SLIDER_SIZE, this.rect.height);
    }

    @Override
    protected boolean handleMouseButtonDown(PCVec2I mouse, int buttons, int eventButton, boolean doubleClick, PCGresHistory history) {
        int x = (int) (this.progress*(this.rect.width-SLIDER_SIZE)/this.steps+0.5);
        if(mouse.x>=x && mouse.x<=x+SLIDER_SIZE && this.enabled && this.parentEnabled){
            this.mouseDown = true;
            moveBarToMouse(mouse);
        }
        return true;
    }

    @Override
    protected boolean handleMouseMove(PCVec2I mouse, int buttons, PCGresHistory history) {
        if(this.mouseDown){
            moveBarToMouse(mouse);
            return true;
        }
        return false;
    }

    private void moveBarToMouse(PCVec2I mouse){
        this.progress = (mouse.x - SLIDER_SIZE/2)/(float)(this.rect.width-SLIDER_SIZE)*this.steps;
        if(this.progress<0)
            this.progress = 0;
        if(this.progress>this.steps)
            this.progress = this.steps;
    }

    @Override
    protected boolean handleMouseButtonUp(PCVec2I mouse, int buttons, int eventButton, PCGresHistory history) {
        this.progress = (int)(this.progress+0.5);
        return super.handleMouseButtonUp(mouse, buttons, eventButton, history);
    }

    @Override
    protected void handleMouseLeave(PCVec2I mouse, int buttons, PCGresHistory history) {
        this.mouseOver = false;
    }

    @Override
    protected void handleMouseWheel(PCGresMouseWheelEvent event, PCGresHistory history) {
        float bevore = this.progress;
        this.progress += event.getWheel();
        if(this.progress<0)
            this.progress = 0;
        if(this.progress>this.steps)
            this.progress = this.steps;
        if(this.progress!=bevore)
            event.consume();
    }
}
