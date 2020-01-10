package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCVec2I;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCGresButton extends PCGresComponent {
    private static final String textureName = "Button";


    public PCGresButton(String title) {

        setText(title);
        this.fontColors[0] = 0xe0e0e0;
        this.fontColors[1] = 0xffffa0;
        this.fontColors[2] = 0xffffa0;
        this.fontColors[3] = 0x7a7a7a;
    }

    @Override
    protected PCVec2I calculateMinSize() {
        PCVec2I size = fontRenderer.getStringSize(this.text);
        return getTextureMinSize(textureName).max(size.x + 6, size.y + 6);
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1, -1);    }

    @Override
    protected PCVec2I calculatePrefSize() {
        PCVec2I size = fontRenderer.getStringSize(this.text);
        return calculateMinSize().max(new PCVec2I(size.x + 3, size.y + 3));
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {

        drawTexture(textureName, 0, 0, this.rect.width, this.rect.height);
        drawString(this.text, 3, this.mouseDown ? 4 : 3, this.rect.width - 6, this.rect.height - 6, PCGresAlign.H.CENTER, PCGresAlign.V.CENTER, true);
    }

}



