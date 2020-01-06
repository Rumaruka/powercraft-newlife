package com.rumaruka.powercraft.modules;

import com.rumaruka.powercraft.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;

import java.util.logging.Level;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public class PCConfig {

    public static boolean coreModule =true;
    public static boolean checkpointModule;
    public static boolean teleportModule;

    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            setupConfig(cfg);
        } catch (Exception ignored) {

        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void setupConfig(Configuration cfg){

        checkpointModule = cfg.getBoolean("Checkpoint Module",CATEGORY_GENERAL,checkpointModule,"Enable checkpoint module");
        coreModule = cfg.getBoolean("Core Module",CATEGORY_GENERAL,coreModule,"Enable Core module");

    }
}
