package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import me.frxq15.packetmines.object.GPlayer;
import me.frxq15.packetmines.object.MineRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CreateRegionCommand extends SubCommand {
    private final PacketMines plugin;

    public CreateRegionCommand(PacketMines plugin) {
        super("create", "packetmines.admin.create", "/amine create <regionId> [maxPlayers]",
                Collections.singletonList("createregion"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.format("&cThis command can only be executed by players."));
            return;
        }

        Player player = (Player) sender;
        GPlayer gPlayer = plugin.getMineManager().getPlayer(player);

        if (!gPlayer.hasSelection()) {
            player.sendMessage(plugin.format("&cYou must select a region with the wand first!"));
            player.sendMessage(plugin.format("&eUse the wand to left-click and right-click blocks to select a region."));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.format("&cUsage: /amine create <regionId> [maxPlayers]"));
            return;
        }

        String regionId = args[0];
        int maxPlayers = 10; // Default

        if (args.length >= 2) {
            try {
                maxPlayers = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.format("&cInvalid number for maxPlayers!"));
                return;
            }
        }

        // Check if region already exists
        if (plugin.getMineManager().getRegion(regionId) != null) {
            player.sendMessage(plugin.format("&cA region with ID &e" + regionId + " &calready exists!"));
            return;
        }

        MineRegion region = plugin.getMineManager().createRegion(
            regionId,
            gPlayer.getWandPos1(),
            gPlayer.getWandPos2(),
            maxPlayers
        );

        player.sendMessage(plugin.format("&aRegion &e" + regionId + " &acreated successfully!"));
        player.sendMessage(plugin.format("&aMax players: &e" + maxPlayers));
        player.sendMessage(plugin.format("&aWorld: &e" + region.getWorld().getName()));

        gPlayer.clearSelection();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("<regionId>");
        } else if (args.length == 2) {
            return Collections.singletonList("<maxPlayers>");
        }
        return Collections.emptyList();
    }
}
