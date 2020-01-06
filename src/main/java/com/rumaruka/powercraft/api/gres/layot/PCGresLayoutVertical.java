package com.rumaruka.powercraft.api.gres.layot;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresAlign;
import com.rumaruka.powercraft.api.gres.PCGresContainer;

public class PCGresLayoutVertical implements IGresLayout{

    private PCGresAlign.Size size;

    public PCGresLayoutVertical(){
        this.size = PCGresAlign.Size.SELV;
    }

    public PCGresLayoutVertical(PCGresAlign.Size size){
        this.size = size;
    }
    @Override
    public PCVec2I getPreferredLayoutSize(PCGresContainer container) {
        return null;
    }

    @Override
    public PCVec2I getMinimumLayoutSize(PCGresContainer container) {
        return null;
    }

    @Override
    public void updateLayout(PCGresContainer container) {

    }
}
