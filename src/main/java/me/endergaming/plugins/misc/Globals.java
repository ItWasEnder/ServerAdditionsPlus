package me.endergaming.plugins.misc;

public class Globals {
    public static final String CURRENCY_1 = "MC";

    public enum Plugins {
        CMI("CMI"),
        PLACEHOLDER_API("PlaceholderAPI"),
        MCMMO("mcMMO"),
        MONEY_FROM_MOBS("MoneyFromMobs"),
        LUNAR_CLIENT("LunarClient-API"),
        LEVELLED_MOBS("LevelledMobs"),

        AURELIUM_SKILLS("AureliumSkills"),
        KONQUEST("Konquest");

        public final String name;

        Plugins(String value) {
            this.name = value;
        }
    }

    public enum Addons {
        CMIManager("CMIManager"),
        PlaceholderManager("PlaceholderManager"),
        SkillsManager("SkillsManager"),
        MFMManager("MFMManager"),
        LCManager("LCManager"),
        KQManager("KQManager"),
        LMManager("LMManager");

        public final String name;

        Addons(String value) {
            this.name = value;
        }
    }
}
