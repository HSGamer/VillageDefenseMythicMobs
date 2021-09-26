package me.hsgamer.villagedefensemythicmobs.config;

public class SpawnerData {
    public final String spawnerName;
    public final String mobName;
    public final int priority;

    public SpawnerData(String spawnerName, String mobName, int priority) {
        this.spawnerName = spawnerName;
        this.mobName = mobName;
        this.priority = priority;
    }
}
