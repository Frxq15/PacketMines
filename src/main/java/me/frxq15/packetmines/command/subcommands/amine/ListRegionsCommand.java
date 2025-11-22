package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import me.frxq15.packetmines.object.MineRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ListRegionsCommand extends SubCommand {
    private final PacketMines plugin;

    public ListRegionsCommand(PacketMines plugin) {
        super("list", "packetmines.admin.list", "/amine list",
                Collections.singletonList("listregions"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (plugin.getMineManager().getAllRegions().isEmpty()) {
            sender.sendMessage(plugin.format("&cNo regions have been created yet!"));
            return;
        }

        sender.sendMessage(plugin.format("&e&l===== Mine Regions ====="));
        for (MineRegion region : plugin.getMineManager().getAllRegions()) {
            int assigned = region.getAssignedPlayers().size();
            int max = region.getMaxPlayers();
            String status = region.isFull() ? "&c[FULL]" : "&a[AVAILABLE]";

            sender.sendMessage(plugin.format("&e" + region.getId() + " " + status +
                " &7- &f" + assigned + "/" + max + " players"));
            sender.sendMessage(plugin.format("  &7World: &f" + region.getWorld().getName()));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
