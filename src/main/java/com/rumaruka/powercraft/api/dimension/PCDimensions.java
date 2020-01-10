package com.rumaruka.powercraft.api.dimension;

import com.rumaruka.powercraft.PowerCraft;

import com.rumaruka.powercraft.api.PCImmutableList;
import com.rumaruka.powercraft.api.reflect.PCSecurity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCDimensions {


    private static boolean done;
    private static List<PCDimension> dimensions = new ArrayList<PCDimension>();
    private static List<PCDimension> immutableDimensions = new PCImmutableList<PCDimension>(dimensions);
    private static HashMap<Class<? extends PCWorldProvider>, PCDimension> providers = new HashMap<Class<? extends PCWorldProvider>, PCDimension>();

    static void addDimensions(PCDimension dimension) {
        if(done){
        }else{
            dimensions.add(dimension);
            providers.put(dimension.getWorldProvider(), dimension);
        }
    }

    public static List<PCDimension> getDimensions(){
        return immutableDimensions;
    }

    public static void construct(){
        PCSecurity.allowedCaller("PCDimensions.construct()", PowerCraft.class);
        if(!done){
            done = true;
            for(PCDimension dimension:dimensions){
                dimension.construct();
            }
        }
    }

    public static PCDimension getDimenstionForProvider(Class<? extends PCWorldProvider> provider){
        return providers.get(provider);
    }

}
