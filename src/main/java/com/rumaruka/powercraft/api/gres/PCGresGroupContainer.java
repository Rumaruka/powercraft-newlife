package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCVec2I;

public class PCGresGroupContainer extends PCGresContainer {

    public PCGresGroupContainer(){

    }

    @Override
    protected PCVec2I calculateMinSize() {
        return new PCVec2I(-1, -1);
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
        //
    }
}
