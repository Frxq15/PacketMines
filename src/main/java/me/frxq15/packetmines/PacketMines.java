package me.frxq15.packetmines;

import me.frxq15.packetmines.command.CommandHandler;
import me.frxq15.packetmines.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class PacketMines extends JavaPlugin {
    public static PacketMines instance;
    private ItemUtils itemUtils;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registry();
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void registry() {
        commandHandler = new CommandHandler(this);
        commandHandler.load();
        itemUtils = new ItemUtils(this);
    }
    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public void log(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[PacketMines] " + text);
    }
    public static PacketMines getInstance() {
        return instance;
    }
    public ItemUtils getItemUtils() {
        return itemUtils;
    }
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
