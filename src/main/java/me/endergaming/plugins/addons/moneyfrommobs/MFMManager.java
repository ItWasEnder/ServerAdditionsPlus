package me.endergaming.plugins.addons.moneyfrommobs;

import me.endergaming.enderlibs.file.FileUtils;
import me.endergaming.enderlibs.misc.LocationUtils;
import me.endergaming.enderlibs.misc.Task;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.Addon;
import me.endergaming.plugins.misc.Globals;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.endergaming.plugins.ServerAdditionsPlus.FIVE_MINUTES_IN_TICKS;

public class MFMManager extends Addon {
    private HashMap<EntityType, Set<Location>> cachedSpawners;
    private MFMListener listener;
    private FileConfiguration spawnerDat;
    private File dataFile;
    private Task saveTask;

    public MFMManager(@NotNull final ServerAdditionsPlus instance, @NotNull Globals.Plugins reqPlugin) {
        super(instance, "money-from-mobs", reqPlugin);
    }

    @Override
    public void onEnable() {
        this.listener = new MFMListener(this);
        Bukkit.getPluginManager().registerEvents(this.listener, this.getPlugin());
        if (this.cachedSpawners == null) {
            this.cachedSpawners = new HashMap<>();
        } else {
            this.cachedSpawners.clear();
        }

        this.dataFile = FileUtils.getFile("compatibilities.PlacedSpawnerData", "yml", this.getPlugin());
        this.spawnerDat = YamlConfiguration.loadConfiguration(this.dataFile);

        this.readSpawnerData();

        if (this.saveTask == null) {
            this.saveTask = Task.asyncRepeating(this::saveSpawnerData, FIVE_MINUTES_IN_TICKS, FIVE_MINUTES_IN_TICKS * 5);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this.listener);
        this.saveSpawnerData();
        this.clearSpawners();
    }

    public boolean isUnnatural(@NotNull CreatureSpawner spawner) {
        if (!this.cachedSpawners.containsKey(spawner.getSpawnedType())) {
            return false;
        }
        return this.cachedSpawners.get(spawner.getSpawnedType()).contains(spawner.getLocation());
    }

    public void addSpawner(@NotNull CreatureSpawner spawner) {
        Set<Location> locations = this.cachedSpawners.getOrDefault(spawner.getSpawnedType(), new HashSet<>());
        locations.add(spawner.getLocation());
        this.cachedSpawners.put(spawner.getSpawnedType(), locations);
    }

    public void removeSpawner(@NotNull CreatureSpawner spawner) {
        if (!this.cachedSpawners.containsKey(spawner.getSpawnedType())) {
            return;
        }
        this.cachedSpawners.get(spawner.getSpawnedType()).remove(spawner.getLocation());
    }

    public void clearSpawners() {
        if (this.cachedSpawners != null) {
            this.cachedSpawners.clear();
        }
    }

    public void readSpawnerData() {
        for (String key : this.spawnerDat.getKeys(false)) {
            try {
                JSONParser jsonParser = new JSONParser();
                String jsonFromKey = this.spawnerDat.getString(key);
                Object parsed = jsonParser.parse(jsonFromKey);
                if (parsed == null) {
                    continue;
                }
                JSONArray locations = (JSONArray) parsed;
                EntityType spawnerType = EntityType.valueOf(key);
                Set<Location> list = this.cachedSpawners.getOrDefault(spawnerType, new HashSet<>());
                for (Object object : locations) {
                    Location loc = LocationUtils.fromString(object.toString(), ",");
                    list.add(loc);
                }
                this.cachedSpawners.put(spawnerType, list);
            } catch (ParseException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSpawnerData() {
        for (Map.Entry<EntityType, Set<Location>> entry : this.cachedSpawners.entrySet()) {
            String spawnerType = entry.getKey().toString();
            JSONArray locationArray = new JSONArray();
            Collection<String> locations = entry.getValue().stream().map(l -> LocationUtils.toString(l, ",")).collect(Collectors.toList());
            locationArray.addAll(locations);
            this.spawnerDat.set(spawnerType, locationArray.toJSONString());
        }
        // Save Data
        try {
            this.spawnerDat.save(this.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
