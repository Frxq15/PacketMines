package me.frxq15.packetmines.manager;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.object.GPlayer;
import me.frxq15.packetmines.object.Mine;
import me.frxq15.packetmines.object.MineRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all mines and regions
 */
public class MineManager {
    private final PacketMines plugin;
    private final Map<String, MineRegion> regions;
    private final Map<UUID, Mine> playerMines;
    private final Map<UUID, GPlayer> players;

    public MineManager(PacketMines plugin) {
        this.plugin = plugin;
        this.regions = new ConcurrentHashMap<>();
        this.playerMines = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
    }

    public GPlayer getPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, GPlayer::new);
    }

    public GPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public MineRegion createRegion(String id, Location pos1, Location pos2, int maxPlayers) {
        MineRegion region = new MineRegion(id, pos1, pos2, maxPlayers);
        regions.put(id, region);
        return region;
    }

    public void deleteRegion(String id) {
        MineRegion region = regions.remove(id);
        if (region != null) {
            // Remove all players from this region
            for (UUID uuid : region.getAssignedPlayers()) {
                GPlayer gPlayer = getPlayer(uuid);
                gPlayer.setAssignedRegionId(null);
                playerMines.remove(uuid);
            }
        }
    }

    public MineRegion getRegion(String id) {
        return regions.get(id);
    }

    public Collection<MineRegion> getAllRegions() {
        return regions.values();
    }

    public Mine getPlayerMine(UUID uuid) {
        return playerMines.get(uuid);
    }

    public Mine getPlayerMine(Player player) {
        return getPlayerMine(player.getUniqueId());
    }

    public void setPlayerMine(UUID uuid, Mine mine) {
        playerMines.put(uuid, mine);
    }

    /**
     * Auto-assigns a player to a region
     * Prefers regions with available slots, but can exceed if needed
     */
    public MineRegion autoAssignRegion(UUID uuid) {
        // First try to find a non-full region
        MineRegion availableRegion = regions.values().stream()
                .filter(region -> !region.isFull())
                .min(Comparator.comparingInt(r -> r.getAssignedPlayers().size()))
                .orElse(null);

        // If no available region, use the one with fewest players (breaking threshold)
        if (availableRegion == null) {
            availableRegion = regions.values().stream()
                    .min(Comparator.comparingInt(r -> r.getAssignedPlayers().size()))
                    .orElse(null);
        }

        if (availableRegion != null) {
            availableRegion.addPlayer(uuid);
            GPlayer gPlayer = getPlayer(uuid);
            gPlayer.setAssignedRegionId(availableRegion.getId());
            plugin.log("Auto-assigned player " + uuid + " to region " + availableRegion.getId() +
                      " (" + availableRegion.getAssignedPlayers().size() + "/" + availableRegion.getMaxPlayers() + ")");
        }

        return availableRegion;
    }

    /**
     * Creates a default mine for a player in their assigned region
     */
    public Mine createDefaultMine(UUID uuid, MineRegion region) {
        // Create a default mine area within the region
        Location regionCenter = region.getPos1().clone().add(region.getPos2()).multiply(0.5);

        // Create a 10x10x10 mine by default
        Location minePos1 = regionCenter.clone().subtract(5, 0, 5);
        Location minePos2 = regionCenter.clone().add(5, 10, 5);

        Mine mine = new Mine(uuid, minePos1, minePos2, region.getId());
        playerMines.put(uuid, mine);

        GPlayer gPlayer = getPlayer(uuid);
        gPlayer.setAssignedMine(mine);

        return mine;
    }

    public Map<String, MineRegion> getRegions() {
        return regions;
    }

    public Map<UUID, Mine> getPlayerMines() {
        return playerMines;
    }
}
