package me.frxq15.packetmines.command;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.command.commands.AdminMineCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public record CommandHandler(PacketMines plugin) {

    public void load() {
        registerCommands();
    }

    private void registerCommands() {
        registerCommand("amine", new AdminMineCommand(plugin));
    }

    private void registerCommand(@NotNull String name, @NotNull CommandExecutor commandExecutor) {
        PluginCommand command = plugin.getCommand(name);

        // Checking whether the command is registered in the plugin.yml
        if (command == null) {
            plugin.getLogger().log(Level.WARNING, "Command " + name + " is not registered in the plugin.yml! Skipping command...");
            return;
        }

        command.setExecutor(commandExecutor);

        // Using command executor as tab completer if it is one
        if (commandExecutor instanceof TabCompleter)
            command.setTabCompleter((TabCompleter) commandExecutor);
    }
}
