package me.hsgamer.villagedefensemythicmobs.config;

import java.util.Map;

public class SpawnerData {
    public final String spawnerName;
    public final String mobName;
    public final int priority;
    public final Map<String, Object> options;

    public SpawnerData(String spawnerName, String mobName, int priority, Map<String, Object> options) {
        this.spawnerName = spawnerName;
        this.mobName = mobName;
        this.priority = priority;
        this.options = options;
    }
}
