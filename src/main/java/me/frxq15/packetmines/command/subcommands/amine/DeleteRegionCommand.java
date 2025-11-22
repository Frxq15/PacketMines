package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeleteRegionCommand extends SubCommand {
    private final PacketMines plugin;

    public DeleteRegionCommand(PacketMines plugin) {
        super("delete", "packetmines.admin.delete", "/amine delete <regionId>",
                Collections.singletonList("deleteregion"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.format("&cUsage: /amine delete <regionId>"));
            return;
        }

        String regionId = args[0];

        if (plugin.getMineManager().getRegion(regionId) == null) {
            sender.sendMessage(plugin.format("&cRegion &e" + regionId + " &cdoes not exist!"));
            return;
        }

        plugin.getMineManager().deleteRegion(regionId);
        sender.sendMessage(plugin.format("&aRegion &e" + regionId + " &adeleted successfully!"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getMineManager().getRegions().keySet());
        }
        return Collections.emptyList();
    }
}
