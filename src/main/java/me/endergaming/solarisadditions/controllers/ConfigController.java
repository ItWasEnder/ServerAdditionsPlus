package me.endergaming.solarisadditions.controllers;

import me.endergaming.enderlibs.file.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigController {
    private FileConfiguration config;
    private File configFile;

    public static boolean DEBUG = false;
    public static boolean LUNAR_CLIENT = false;
    public static boolean MONEY_FROM_MOBS = false;
    public static boolean CMI = false;
    public static boolean PLACEHOLDER_API = false;
    public static boolean MCMMO = false;
    public static boolean LEVELLED_MOBS = false;
    public static boolean RESTRICT_SPAWNS = true;
    public static double DROP_CHANCE_MOD = 0.00;
    public static double SPAWNER_NERF_MOD = 1.0;


    public void init() {
        config = FileManager.getConfig("config", "yml");
        configFile = FileManager.getFile("config", "yml");
        read();
    }

    public void read() {
        DEBUG = config.getBoolean("debug");

        LUNAR_CLIENT = config.getBoolean("lunar-client.enabled");

        MONEY_FROM_MOBS = config.getBoolean("money-from-mobs.enabled");
        RESTRICT_SPAWNS = config.getBoolean("money-from-mobs.restrict-spawners");
        DROP_CHANCE_MOD = config.getDouble("money-from-mobs.levelled-mob-chance");
        SPAWNER_NERF_MOD = config.getDouble("money-from-mobs.spawner-nerf");

        CMI = config.getBoolean("cmi.enabled");

        PLACEHOLDER_API = config.getBoolean("placeholder-api.enabled");

        MCMMO = config.getBoolean("mcmmo.enabled");

        LEVELLED_MOBS = config.getBoolean("levelled-mobs.enabled");
    }
}
