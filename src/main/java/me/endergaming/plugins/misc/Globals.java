package me.endergaming.plugins.misc;

import me.endergaming.plugins.backend.Addon;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

public class Globals {
    public static final String CURRENCY_1 = "MC";
    public static final String PLUGIN_KEY = "sap";

    public static final String PREFIX = "&7[&a\uD83E\uDDEA&7] ";

    public static final Random RANDOM = new Random();

    public enum Plugins {
        CMI("CMI"),
        PLACEHOLDER_API("PlaceholderAPI"),
        MCMMO("mcMMO"),
        MONEY_FROM_MOBS("MoneyFromMobs"),
        LUNAR_CLIENT("LunarClient-API"),
        LEVELLED_MOBS("LevelledMobs"),

        AURELIUM_SKILLS("AureliumSkills"),
        KONQUEST("Konquest"),
        MMOCORE("MMOCore"),
        NONE("");

        public final String name;

        Plugins(String value) {
            this.name = value;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Addons {
        CMIManager("CMIManager"),
        PlaceholderManager("PlaceholderManager"),
        SkillsManager("SkillsManager"),
        MFMManager("MFMManager"),
        LCManager("LCManager"),
        KQManager("KQManager"),
        LMManager("LMManager"),
        MMOManager("MMOManager"),
        AnvilManager("AnvilManager");
        ;

        private final String name;
        private Supplier<? extends Addon> supplier;

        Addons(String value) {
            this.name = value;
            this.supplier = null;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public void supply(Supplier<? extends Addon> supplier) {
            if (this.supplier != null) {
                return;
            }

            this.supplier = supplier;
        }

        public @Nullable Addon get() {
            if (this.supplier == null) {
                return null;
            }
            var addon = this.supplier.get();

            return addon.isRegistered() ? addon : null;
        }
    }
}
