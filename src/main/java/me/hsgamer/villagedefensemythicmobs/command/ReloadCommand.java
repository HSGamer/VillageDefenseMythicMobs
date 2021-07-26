package me.hsgamer.villagedefensemythicmobs.command;

import me.hsgamer.villagedefensemythicmobs.VillageDefenseMythicMobs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ReloadCommand extends Command {
    private static final Permission PERMISSION = new Permission("vdmythicspawner.reload", PermissionDefault.OP);
    private final VillageDefenseMythicMobs instance;

    public ReloadCommand(VillageDefenseMythicMobs instance) {
        super("reloadmythicspawner", "Reload mythic spawners", "/reloadmythicspawner", Arrays.asList("rlmythicspawner", "rlms"));
        this.instance = instance;
        setPermission(PERMISSION.getName());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        instance.getMobsConfig().reload();
        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded");
        return true;
    }
}
