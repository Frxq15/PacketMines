package me.frxq15.packetmines.command.commands;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.ParentCommand;
import me.frxq15.packetmines.command.SubCommand;
import me.frxq15.packetmines.command.subcommands.amine.WandCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminMineCommand extends ParentCommand {

    public AdminMineCommand(PacketMines plugin) {
        super(plugin, "amine", "packetmines.admin");
        register(new WandCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage("No permission");
            return true;
        }
        if (args.length == 0) {
            //help
            sender.sendMessage("send help pls");
            return true;
        }
        subLabel = args[0];
        subArgs = Arrays.copyOfRange(args, 1, args.length);

        if (!exists(subLabel)) {
            sender.sendMessage("Sub command not found");
            return true;
        }
        SubCommand subCommand = getExecutor(subLabel);

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage("No permission");
            return true;
        }
        subCommand.onCommand(sender, command, subLabel, subArgs);
        return true;
    }
}
