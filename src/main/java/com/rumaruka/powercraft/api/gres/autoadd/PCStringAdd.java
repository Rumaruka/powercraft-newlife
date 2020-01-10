package com.rumaruka.powercraft.api.gres.autoadd;


import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.document.PCGresDocument;
import com.rumaruka.powercraft.api.gres.document.PCGresDocumentLine;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCStringAdd {

    public PCGresComponent component;
    public PCGresDocument document;
    public PCGresDocumentLine documentLine;
    public int pos;
    public String toAdd;
    public int cursorPos;
}
