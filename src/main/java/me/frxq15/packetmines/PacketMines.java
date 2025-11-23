package me.frxq15.packetmines;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.*;
import me.frxq15.packetmines.command.CommandHandler;
import me.frxq15.packetmines.file.FileManager;
import me.frxq15.packetmines.listener.DataListeners;
import me.frxq15.packetmines.listener.PacketListeners;
import me.frxq15.packetmines.manager.MineManager;
import me.frxq15.packetmines.packet.PacketHandler;
import me.frxq15.packetmines.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class PacketMines extends JavaPlugin {
    public static PacketMines instance;
    private ItemUtils itemUtils;
    private CommandHandler commandHandler;
    private MineManager mineManager;
    private PacketHandler packetHandler;
    private FileManager fileManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        PacketEvents.getAPI().init();

        registry();
        log("&aPacketMines enabled successfully!");
        log("&eUsing PacketEvents v" + PacketEvents.getAPI().getVersion());
    }

    @Override
    public void onDisable() {
        // Save all data before shutdown
        if (fileManager != null) {
            fileManager.saveAll();
        }

        PacketEvents.getAPI().terminate();
        log("&cPacketMines disabled!");
    }

    public void registry() {
        // Initialize managers
        mineManager = new MineManager(this);
        packetHandler = new PacketHandler(this);
        itemUtils = new ItemUtils(this);
        fileManager = new FileManager(this);

        // Load data
        fileManager.loadAll();

        // Register commands
        commandHandler = new CommandHandler(this);
        commandHandler.load();

        // Register listeners
        getServer().getPluginManager().registerEvents(new DataListeners(this), this);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListeners(this));

        // Auto-save every 5 minutes (6000 ticks)
        getServer().getScheduler().runTaskTimer(this, () -> {
            fileManager.saveAll();
        }, 6000L, 6000L);
    }

    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void log(String text) {
        Bukkit.getConsoleSender().sendMessage(format("&b[PacketMines] " + text));
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

    public MineManager getMineManager() {
        return mineManager;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}

