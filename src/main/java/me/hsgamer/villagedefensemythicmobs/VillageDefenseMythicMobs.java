package me.hsgamer.villagedefensemythicmobs;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.villagedefensemythicmobs.command.ReloadCommand;
import me.hsgamer.villagedefensemythicmobs.config.MobsConfig;
import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import me.hsgamer.villagedefensemythicmobs.hook.boss.BossSpawnListener;
import me.hsgamer.villagedefensemythicmobs.hook.boss.BossSpawner;
import me.hsgamer.villagedefensemythicmobs.hook.mythicmobs.MythicMobSpawnListener;
import me.hsgamer.villagedefensemythicmobs.hook.mythicmobs.MythicMobSpawner;
import me.hsgamer.villagedefensemythicmobs.spawner.AbstractMythicSpawner;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.villagedefense.Main;

public final class VillageDefenseMythicMobs extends BasePlugin {
    private final MobsConfig mobsConfig = new MobsConfig(this);
    private final Builder<SpawnerData, AbstractMythicSpawner> mythicSpawnerBuilder = new Builder<>();
    private Main parentPlugin;

    @Override
    public void enable() {
        parentPlugin = JavaPlugin.getPlugin(Main.class);
        mobsConfig.setup();
        registerCommand(new ReloadCommand(this));
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            mythicSpawnerBuilder.register(MythicMobSpawner::new, "mythicmob", "mythic", "mythicmobs");
            registerListener(new MythicMobSpawnListener());
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Boss")) {
            mythicSpawnerBuilder.register(BossSpawner::new, "boss");
            registerListener(new BossSpawnListener());
        }
    }

    @Override
    public void postEnable() {
        mobsConfig.initSpawners();
    }

    @Override
    public void disable() {
        mobsConfig.clearSpawners();
    }

    public MobsConfig getMobsConfig() {
        return mobsConfig;
    }

    public Main getParentPlugin() {
        return parentPlugin;
    }

    public Builder<SpawnerData, AbstractMythicSpawner> getMythicSpawnerBuilder() {
        return mythicSpawnerBuilder;
    }
}
