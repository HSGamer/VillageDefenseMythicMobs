package me.hsgamer.villagedefensemythicmobs.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.villagedefensemythicmobs.VillageDefenseMythicMobs;
import me.hsgamer.villagedefensemythicmobs.spawner.AbstractMythicSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MobsConfig {
    private final VillageDefenseMythicMobs plugin;
    private final BukkitConfig config;
    private final List<AbstractMythicSpawner> spawnerList = new ArrayList<>();

    public MobsConfig(VillageDefenseMythicMobs plugin) {
        this.plugin = plugin;
        this.config = new BukkitConfig(plugin, "mobs.yml");
    }

    public void setup() {
        config.setup();
    }

    public void reload() {
        clearSpawners();
        config.reload();
        initSpawners();
    }

    public void initSpawners() {
        config.getKeys(false).forEach(key -> {
            Map<String, Object> values = config.getNormalizedValues(key, false);
            getSpawner(key, values).ifPresent(spawner -> {
                spawnerList.add(spawner);
                plugin.getParentPlugin().getEnemySpawnerRegistry().getEnemySpawnerSet().add(spawner);
            });
        });
    }

    private Optional<AbstractMythicSpawner> getSpawner(String spawnerName, Map<String, Object> values) {
        int priority = Optional.ofNullable(values.get("priority"))
                .map(String::valueOf)
                .map(Integer::parseInt)
                .orElse(0);
        Optional<String> mobName = Optional.ofNullable(values.get("name"))
                .map(String::valueOf);
        Optional<String> type = Optional.ofNullable(values.get("type"))
                .map(String::valueOf);
        if (!type.isPresent()) {
            plugin.getLogger().warning(() -> "The spawner '" + spawnerName + "' is missing a 'type' value");
            return Optional.empty();
        }
        if (!mobName.isPresent()) {
            plugin.getLogger().warning(() -> "The spawner '" + spawnerName + "' is missing a 'name' value");
            return Optional.empty();
        }
        SpawnerData spawnerData = new SpawnerData(plugin, spawnerName, mobName.get(), priority, values);
        Optional<AbstractMythicSpawner> optionalSpawner = plugin.getMythicSpawnerBuilder().build(type.get(), spawnerData);
        if (!optionalSpawner.isPresent()) {
            plugin.getLogger().warning(() -> "Unknown spawner type for the spawner '" + spawnerName + "'");
        }
        return optionalSpawner;
    }

    public void clearSpawners() {
        spawnerList.forEach(plugin.getParentPlugin().getEnemySpawnerRegistry().getEnemySpawnerSet()::remove);
        spawnerList.clear();
    }
}
