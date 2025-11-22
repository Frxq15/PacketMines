package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GivePickaxeCommand extends SubCommand {
    private final PacketMines plugin;

    public GivePickaxeCommand(PacketMines plugin) {
        super("givepickaxe", "packetmines.admin.givepickaxe", "/amine givepickaxe [player]",
                Collections.singletonList("pickaxe"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.format("&cYou must specify a player from console!"));
                sender.sendMessage(plugin.format("&eUsage: /amine givepickaxe <player>"));
                return;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.format("&cPlayer &e" + args[0] + " &cis not online!"));
                return;
            }
        }

        plugin.getItemUtils().givePickaxe(target);

        if (sender.equals(target)) {
            target.sendMessage(plugin.format("&aYou received a Packet Mine Pickaxe!"));
        } else {
            sender.sendMessage(plugin.format("&aGave Packet Mine Pickaxe to &e" + target.getName()));
            target.sendMessage(plugin.format("&aYou received a Packet Mine Pickaxe!"));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
