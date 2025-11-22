package me.frxq15.packetmines.file;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.object.Mine;
import me.frxq15.packetmines.object.MineRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Manages saving and loading of mine and region data
 */
public class FileManager {
    private final PacketMines plugin;
    private File regionsFile;
    private File minesFile;
    private FileConfiguration regionsConfig;
    private FileConfiguration minesConfig;

    public FileManager(PacketMines plugin) {
        this.plugin = plugin;
        setupFiles();
    }

    private void setupFiles() {
        regionsFile = new File(plugin.getDataFolder(), "regions.yml");
        minesFile = new File(plugin.getDataFolder(), "mines.yml");

        if (!regionsFile.exists()) {
            try {
                regionsFile.createNewFile();
            } catch (IOException e) {
                plugin.log("&cFailed to create regions.yml: " + e.getMessage());
            }
        }

        if (!minesFile.exists()) {
            try {
                minesFile.createNewFile();
            } catch (IOException e) {
                plugin.log("&cFailed to create mines.yml: " + e.getMessage());
            }
        }

        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
        minesConfig = YamlConfiguration.loadConfiguration(minesFile);
    }

    public void saveRegions() {
        regionsConfig = new YamlConfiguration();

        for (Map.Entry<String, MineRegion> entry : plugin.getMineManager().getRegions().entrySet()) {
            String regionId = entry.getKey();
            MineRegion region = entry.getValue();

            regionsConfig.set("regions." + regionId, region.serialize());
        }

        try {
            regionsConfig.save(regionsFile);
            plugin.log("&aSaved " + plugin.getMineManager().getRegions().size() + " regions");
        } catch (IOException e) {
            plugin.log("&cFailed to save regions: " + e.getMessage());
        }
    }

    public void loadRegions() {
        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);

        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");
        if (regionsSection == null) {
            plugin.log("&eNo regions to load");
            return;
        }

        int count = 0;
        for (String regionId : regionsSection.getKeys(false)) {
            try {
                Map<String, Object> data = regionsSection.getConfigurationSection(regionId).getValues(true);
                MineRegion region = MineRegion.deserialize(data);
                plugin.getMineManager().getRegions().put(regionId, region);
                count++;
            } catch (Exception e) {
                plugin.log("&cFailed to load region " + regionId + ": " + e.getMessage());
            }
        }

        plugin.log("&aLoaded " + count + " regions");
    }

    public void saveMines() {
        minesConfig = new YamlConfiguration();

        for (Map.Entry<UUID, Mine> entry : plugin.getMineManager().getPlayerMines().entrySet()) {
            UUID uuid = entry.getKey();
            Mine mine = entry.getValue();

            minesConfig.set("mines." + uuid.toString(), mine.serialize());
        }

        try {
            minesConfig.save(minesFile);
            plugin.log("&aSaved " + plugin.getMineManager().getPlayerMines().size() + " mines");
        } catch (IOException e) {
            plugin.log("&cFailed to save mines: " + e.getMessage());
        }
    }

    public void loadMines() {
        minesConfig = YamlConfiguration.loadConfiguration(minesFile);

        ConfigurationSection minesSection = minesConfig.getConfigurationSection("mines");
        if (minesSection == null) {
            plugin.log("&eNo mines to load");
            return;
        }

        int count = 0;
        for (String uuidStr : minesSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Object> data = minesSection.getConfigurationSection(uuidStr).getValues(true);
                Mine mine = Mine.deserialize(data);
                plugin.getMineManager().getPlayerMines().put(uuid, mine);
                count++;
            } catch (Exception e) {
                plugin.log("&cFailed to load mine for " + uuidStr + ": " + e.getMessage());
            }
        }

        plugin.log("&aLoaded " + count + " mines");
    }

    public void saveAll() {
        saveRegions();
        saveMines();
    }

    public void loadAll() {
        loadRegions();
        loadMines();
    }
}
