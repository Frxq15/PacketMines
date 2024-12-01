package me.frxq15.packetmines.command.subcommands.amine;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class WandCommand extends SubCommand {
    private final PacketMines plugin;
    public WandCommand(PacketMines plugin) {
        super("wand", "packetmines.admin.wand", "/amine wand",
                Arrays.asList("getwand"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            plugin.log("This command cannot be executed from console.");
        }
        Player player = (Player) sender;
        plugin.getItemUtils().giveWand(player);
        player.sendMessage("Mine Wand Received!");
        return;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
