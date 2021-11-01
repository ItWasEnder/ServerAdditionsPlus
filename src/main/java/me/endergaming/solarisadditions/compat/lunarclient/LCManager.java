package me.endergaming.solarisadditions.compat.lunarclient;

import me.endergaming.enderlibs.file.FileManager;
import me.endergaming.enderlibs.util.Task;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
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

import static me.endergaming.solarisadditions.SolarisAdditions.FIVE_MINUTES_IN_TICKS;

public class LCManager extends Manager {
    private Set<UUID> ignoredUUIDs;
    private LCListener listener;
    private FileConfiguration clientDat;
    private File dataFile;
    private Task saveTask;
    private double reward;

    public LCManager(@NotNull final SolarisAdditions instance, @NotNull String reqPlugin, @NotNull String... reqManagers) {
        super(instance, "lunar-client", reqPlugin, reqManagers);
    }

    @Override
    public void register() {
        if (super.registered) unregister();
        listener = new LCListener(this);
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        if (ignoredUUIDs == null) ignoredUUIDs = new HashSet<>();
        else ignoredUUIDs.clear();

        dataFile = FileManager.getFile("compatibilities.UserClientData", "yml", getPlugin());
        clientDat = YamlConfiguration.loadConfiguration(dataFile);

        readClientData();

        if (saveTask == null) saveTask = Task.asyncRepeating(this::saveClientData, FIVE_MINUTES_IN_TICKS, FIVE_MINUTES_IN_TICKS * 5);

        super.register();
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(listener);
        saveClientData();
        clearIDs();
        super.unregister();
    }

    public double getReward() {
        return reward;
    }

    public boolean isIgnored(@NotNull UUID uuid) {
        return ignoredUUIDs.contains(uuid);
    }

    public void addUUID(@NotNull UUID uuid) {
        ignoredUUIDs.add(uuid);
    }

    public void clearIDs() {
        if (ignoredUUIDs != null) ignoredUUIDs.clear();
    }

    public void readClientData() {
        reward = clientDat.getDouble("reward");
        try {
            JSONParser jsonParser = new JSONParser();
            String jsonFromKey = clientDat.getString("ignored");
            if (jsonFromKey != null && !(jsonFromKey.equals("[]") || jsonFromKey.isEmpty())) {
                Object parsed = jsonParser.parse(jsonFromKey);
                if (parsed != null) {
                    JSONArray ids = (JSONArray) parsed;
                    for (Object object : ids) {
                        UUID uuid = UUID.fromString(object.toString());
                        ignoredUUIDs.add(uuid);
                    }
                }
            }
        } catch (ParseException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void saveClientData() {
        JSONArray ids = new JSONArray();
        Collection<String> uuidCollection = ignoredUUIDs.stream().map(UUID::toString).collect(Collectors.toList());
        ids.addAll(uuidCollection);
        clientDat.set("ignored", ids.toJSONString());
        // Save Data
        try {
            clientDat.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
