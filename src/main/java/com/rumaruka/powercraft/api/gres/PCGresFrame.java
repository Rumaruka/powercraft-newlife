package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCGresFrame extends PCGresContainer{
    private static final String textureName = "Frame";

    public PCGresFrame(){
        this.frame.setTo(getTextureFrame(textureName));
    }
    @Override
    protected PCVec2I calculateMinSize() {
        return getTextureMinSize(textureName);
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1,-1);
    }

    @Override
    protected PCVec2I calculatePrefSize() {
        return new PCVec2I(-1,-1);
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
drawTexture(textureName,0,0,this.rect.width,this.rect.height);
    }
}
