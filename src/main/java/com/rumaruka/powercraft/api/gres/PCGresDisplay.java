package com.rumaruka.powercraft.api.gres;


import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCRectI;
import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCGresDisplay extends  PCGresComponent{

    private PCGresDisplayObject displayObject;
    private PCGresDisplayObject background;
    private final PCRectI frame = new PCRectI();

    public PCGresDisplay(PCGresDisplayObject displayObject){
        this.displayObject = displayObject;

        setDisplayObject(displayObject);
    }

    public void setDisplayObject(PCGresDisplayObject displayObject){
        removeEventListener(this.displayObject);
        addEventListener(displayObject);
        this.displayObject = displayObject;
        notifyChange();
    }

    public PCGresDisplayObject getDisplayObject(){
        return this.displayObject;
    }

    public void setBackground(PCGresDisplayObject background){
        this.background = background;
    }

    public PCGresDisplayObject getBackground(){
        return this.background;
    }

    public void setFrame(PCRectI frame){
        this.frame.setTo(frame);
        notifyChange();
    }
    @Override
    protected PCVec2I calculateMinSize() {
        return this.displayObject==null?new PCVec2I(0, 0):this.displayObject.getMinSize().add(this.frame.getLocation()).add(this.frame.getSize());
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1, -1);
    }

    @Override
    protected PCVec2I calculatePrefSize() {
        return (this.displayObject==null?new PCVec2I(16, 16):this.displayObject.getPrefSize()).add(this.frame.getLocation()).add(this.frame.getSize());
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
        if(this.background!=null)
            this.background.draw(0, 0, this.rect.width, this.rect.height);
        if(this.displayObject!=null)
            this.displayObject.draw(this.frame.x, this.frame.y, this.rect.width-this.frame.x-this.frame.width, this.rect.height-this.frame.y-this.frame.height);
    }

}


