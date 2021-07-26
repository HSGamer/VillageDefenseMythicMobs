package me.hsgamer.villagedefensemythicmobs;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.villagedefensemythicmobs.command.ReloadCommand;
import me.hsgamer.villagedefensemythicmobs.config.MobsConfig;
import me.hsgamer.villagedefensemythicmobs.listener.MythicSpawnListener;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.villagedefense.Main;

public final class VillageDefenseMythicMobs extends BasePlugin {
    private final MobsConfig mobsConfig = new MobsConfig(this);
    private Main parentPlugin;

    @Override
    public void enable() {
        parentPlugin = JavaPlugin.getPlugin(Main.class);
        mobsConfig.setup();
        registerCommand(new ReloadCommand(this));
        registerListener(new MythicSpawnListener());
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
}
