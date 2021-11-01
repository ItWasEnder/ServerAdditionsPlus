package me.endergaming.solarisadditions.compat.moneyfrommobs;

import me.endergaming.enderlibs.file.FileManager;
import me.endergaming.enderlibs.util.LocationUtils;
import me.endergaming.enderlibs.util.Task;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static me.endergaming.solarisadditions.SolarisAdditions.FIVE_MINUTES_IN_TICKS;

public class MFMManager extends Manager {
    private HashMap<EntityType, Set<Location>> cachedSpawners;
    private MFMListener listener;
    private FileConfiguration spawnerDat;
    private File dataFile;
    private Task saveTask;

    public MFMManager(@NotNull final SolarisAdditions instance, @NotNull String reqPlugin) {
        super(instance, "money-from-mobs", reqPlugin);
    }

    @Override
    public void register() {
        if (super.registered) unregister();
        listener = new MFMListener(this);
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        if (cachedSpawners == null) cachedSpawners = new HashMap<>();
        else cachedSpawners.clear();

        dataFile = FileManager.getFile("compatibilities.PlacedSpawnerData", "yml", getPlugin());
        spawnerDat = YamlConfiguration.loadConfiguration(dataFile);

        readSpawnerData();

        if (saveTask == null) saveTask = Task.asyncRepeating(this::saveSpawnerData, FIVE_MINUTES_IN_TICKS, FIVE_MINUTES_IN_TICKS * 5);

        super.registered = true;
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(listener);
        saveSpawnerData();
        clearSpawners();
        super.registered = false;
    }

    public boolean isUnnatural(@NotNull CreatureSpawner spawner) {
        if (!cachedSpawners.containsKey(spawner.getSpawnedType())) return false;
        return cachedSpawners.get(spawner.getSpawnedType()).contains(spawner.getLocation());
    }

    public void addSpawner(@NotNull CreatureSpawner spawner) {
        Set<Location> locations = cachedSpawners.getOrDefault(spawner.getSpawnedType(), new HashSet<>());
        locations.add(spawner.getLocation());
        cachedSpawners.put(spawner.getSpawnedType(), locations);
    }

    public void removeSpawner(@NotNull CreatureSpawner spawner) {
        if (!cachedSpawners.containsKey(spawner.getSpawnedType())) return;
        cachedSpawners.get(spawner.getSpawnedType()).remove(spawner.getLocation());
    }

    public void clearSpawners() {
        if (cachedSpawners != null) cachedSpawners.clear();
    }

    public void readSpawnerData() {
        for (String key : spawnerDat.getKeys(false)) {
            try {
                JSONParser jsonParser = new JSONParser();
                String jsonFromKey = spawnerDat.getString(key);
                Object parsed = jsonParser.parse(jsonFromKey);
                if (parsed == null) continue;
                JSONArray locations = (JSONArray) parsed;
                EntityType spawnerType = EntityType.valueOf(key);
                Set<Location> list = cachedSpawners.getOrDefault(spawnerType, new HashSet<>());
                for (Object object : locations) {
                    Location loc = LocationUtils.fromString(object.toString(), ",");
                    list.add(loc);
                }
                cachedSpawners.put(spawnerType, list);
            } catch (ParseException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSpawnerData() {
        for (Map.Entry<EntityType, Set<Location>> entry : cachedSpawners.entrySet()) {
            String spawnerType = entry.getKey().toString();
            JSONArray locationArray = new JSONArray();
            Collection<String> locations = entry.getValue().stream().map(l -> LocationUtils.toString(l, ",")).collect(Collectors.toList());
            locationArray.addAll(locations);
            spawnerDat.set(spawnerType, locationArray.toJSONString());
        }
        // Save Data
        try {
            spawnerDat.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
