package me.endergaming.plugins.controllers;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigController {
    private FileConfiguration config;

    public static boolean DEBUG = false;
    public static boolean LUNAR_CLIENT = false;
    public static boolean MONEY_FROM_MOBS = false;
    public static boolean CMI = false;
    public static boolean PLACEHOLDER_API = false;
    public static boolean MCMMO = false;
    public static boolean SKILLS = false;
    public static boolean LEVELLED_MOBS = false;
    public static boolean RESTRICT_SPAWNERS = false;
    public static boolean KONQUEST = false;
    public static double DROP_CHANCE_MOD = 0.00;
    public static double SPAWNER_NERF_MOD = 1.0;


    public void init() {
//        this.config = FileUtils.getConfig("config", "yml");
        this.read();
    }

    public void read() {
        DEBUG = this.config.getBoolean("debug", true);

        LUNAR_CLIENT = this.getState("lunar-client.enabled");

        MONEY_FROM_MOBS = this.getState("money-from-mobs.enabled");
        RESTRICT_SPAWNERS = this.getState("money-from-mobs.restrict-spawners");
        DROP_CHANCE_MOD = this.config.getDouble("money-from-mobs.levelled-mob-chance");
        SPAWNER_NERF_MOD = this.config.getDouble("money-from-mobs.spawner-nerf");

        CMI = this.getState("cmi.enabled");

        PLACEHOLDER_API = this.getState("placeholder-api.enabled");

        MCMMO = this.getState("mcmmo.enabled");

        SKILLS = this.getState("skills.enabled");

        LEVELLED_MOBS = this.getState("levelled-mobs.enabled");

        KONQUEST = this.getState("konquest.enabled");
    }

    private boolean getState(String path) {
        return this.config.getBoolean(path, false);
    }
}
