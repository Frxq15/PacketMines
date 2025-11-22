package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import me.frxq15.packetmines.object.Mine;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExpandMineCommand extends SubCommand {
    private final PacketMines plugin;

    public ExpandMineCommand(PacketMines plugin) {
        super("expand", "packetmines.admin.expand", "/amine expand <player> <amount>",
                Collections.singletonList("expandmine"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.format("&cUsage: /amine expand <player> <amount>"));
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

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.format("&cInvalid amount: &e" + args[1]));
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.format("&cAmount must be greater than 0!"));
            return;
        }

        int oldVolume = mine.getVolume();
        mine.expand(amount);
        int newVolume = mine.getVolume();

        // Refresh the mine with fake blocks
        if (target.isOnline()) {
            plugin.getPacketHandler().fillMineWithFakeBlocks(target, mine);
        }

        sender.sendMessage(plugin.format("&aExpanded &e" + target.getName() + "'s &amine by &e" + amount + " &ablocks"));
        sender.sendMessage(plugin.format("&aOld volume: &e" + oldVolume + " &a| New volume: &e" + newVolume));
        target.sendMessage(plugin.format("&aYour mine has been expanded by &e" + amount + " &ablocks!"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return List.of("5", "10", "20", "50");
        }
        return Collections.emptyList();
    }
}
