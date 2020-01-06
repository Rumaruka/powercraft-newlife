package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCGresLabel extends PCGresComponent {


    public PCGresLabel(String text) {

        setText(text);
    }
    @Override
    protected PCVec2I calculateMinSize() {
        return fontRenderer.getStringSize(this.text);
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1, -1);
    }

    @Override
    protected PCVec2I calculatePrefSize() {
        return new PCVec2I(-1, -1);
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
        drawString(this.text, 0, 0, this.rect.width, this.rect.height, this.alignH, this.alignV, false);
    }
}
