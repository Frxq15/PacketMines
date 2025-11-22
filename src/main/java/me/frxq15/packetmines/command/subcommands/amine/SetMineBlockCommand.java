package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import me.frxq15.packetmines.object.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetMineBlockCommand extends SubCommand {
    private final PacketMines plugin;

    public SetMineBlockCommand(PacketMines plugin) {
        super("setmineblock", "packetmines.admin.setmineblock", "/amine setmineblock <player> <block>",
                Arrays.asList("setblock", "fillmine"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.format("&cUsage: /amine setmineblock <player> <block>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.format("&cPlayer &e" + args[0] + " &cis not online!"));
            return;
        }

        Mine mine = plugin.getMineManager().getPlayerMine(target);
        if (mine == null) {
            sender.sendMessage(plugin.format("&cPlayer &e" + target.getName() + " &cdoes not have a mine!"));
            return;
        }

        Material material;
        try {
            material = Material.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.format("&cInvalid material: &e" + args[1]));
            return;
        }

        if (!material.isBlock()) {
            sender.sendMessage(plugin.format("&c" + args[1] + " &cis not a valid block!"));
            return;
        }

        mine.setFillMaterial(material);

        // Update the mine with fake blocks for the player
        if (target.isOnline()) {
            plugin.getPacketHandler().fillMineWithFakeBlocks(target, mine);
        }

        sender.sendMessage(plugin.format("&aFilled &e" + target.getName() + "'s &amine with &e" + material.name()));
        target.sendMessage(plugin.format("&aYour mine has been filled with &e" + material.name()));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String input = args[1].toUpperCase();
            return Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Enum::name)
                    .filter(name -> name.startsWith(input))
                    .limit(10)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
