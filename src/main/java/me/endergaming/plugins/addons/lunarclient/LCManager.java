package me.endergaming.plugins.addons.lunarclient;

import me.endergaming.enderlibs.file.FileUtils;
import me.endergaming.enderlibs.misc.Task;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.misc.Globals;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.endergaming.plugins.ServerAdditionsPlus.FIVE_MINUTES_IN_TICKS;

public class LCManager extends Addon {
    private Set<UUID> ignoredUUIDs;
    private LCListener listener;
    private FileConfiguration clientDat;
    private File dataFile;
    private Task saveTask;
    private double reward;

    public LCManager(@NotNull final ServerAdditionsPlus instance, @NotNull Globals.Plugins reqPlugin, @NotNull String... reqManagers) {
        super(instance, "lunar-client", reqPlugin, reqManagers);
    }

    @Override
    public void onEnable() {
        this.listener = new LCListener(this);
        Bukkit.getPluginManager().registerEvents(this.listener, this.getPlugin());
        if (this.ignoredUUIDs == null) {
            this.ignoredUUIDs = new HashSet<>();
        } else {
            this.ignoredUUIDs.clear();
        }

        this.dataFile = FileUtils.getFile("compatibilities.UserClientData", "yml", this.getPlugin());
        this.clientDat = YamlConfiguration.loadConfiguration(this.dataFile);

        this.readClientData();

        if (this.saveTask == null) {
            this.saveTask = Task.asyncRepeating(this::saveClientData, FIVE_MINUTES_IN_TICKS, FIVE_MINUTES_IN_TICKS * 5);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this.listener);
        this.saveClientData();
        this.clearIDs();
    }

    public double getReward() {
        return this.reward;
    }

    public boolean isIgnored(@NotNull UUID uuid) {
        return this.ignoredUUIDs.contains(uuid);
    }

    public void addUUID(@NotNull UUID uuid) {
        this.ignoredUUIDs.add(uuid);
    }

    public void clearIDs() {
        if (this.ignoredUUIDs != null) {
            this.ignoredUUIDs.clear();
        }
    }

    public void readClientData() {
        this.reward = this.clientDat.getDouble("reward", 100.0);
        try {
            JSONParser jsonParser = new JSONParser();
            String jsonFromKey = this.clientDat.getString("ignored");
            if (jsonFromKey != null && !(jsonFromKey.equals("[]") || jsonFromKey.isEmpty())) {
                Object parsed = jsonParser.parse(jsonFromKey);
                if (parsed != null) {
                    JSONArray ids = (JSONArray) parsed;
                    for (Object object : ids) {
                        UUID uuid = UUID.fromString(object.toString());
                        this.ignoredUUIDs.add(uuid);
                    }
                }
            }
        } catch (ParseException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void saveClientData() {
        JSONArray ids = new JSONArray();
        Collection<String> uuidCollection = this.ignoredUUIDs.stream().map(UUID::toString).collect(Collectors.toList());
        ids.addAll(uuidCollection);
        this.clientDat.set("ignored", ids.toJSONString());
        // Save Data
        try {
            this.clientDat.save(this.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
