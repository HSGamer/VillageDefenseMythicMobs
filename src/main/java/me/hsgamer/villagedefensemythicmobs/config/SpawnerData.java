package me.hsgamer.villagedefensemythicmobs.config;

import me.hsgamer.villagedefensemythicmobs.VillageDefenseMythicMobs;

import java.util.Map;

public class SpawnerData {
    public final VillageDefenseMythicMobs instance;
    public final String spawnerName;
    public final String mobName;
    public final int priority;
    public final Map<String, Object> options;

    public SpawnerData(VillageDefenseMythicMobs instance, String spawnerName, String mobName, int priority, Map<String, Object> options) {
        this.instance = instance;
        this.spawnerName = spawnerName;
        this.mobName = mobName;
        this.priority = priority;
        this.options = options;
    }
}
