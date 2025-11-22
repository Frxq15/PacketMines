package me.frxq15.packetmines.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.object.Mine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles sending fake block packets to players using PacketEvents
 */
public class PacketHandler {
    private final PacketMines plugin;

    public PacketHandler(PacketMines plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends a fake block change to a specific player
     */
    public void sendFakeBlock(Player player, Location location, Material material) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) return;

        Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        int stateId = getStateId(material);

        WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(position, stateId);
        user.sendPacket(blockChange);
    }

    /**
     * Sends multiple fake block changes to a player (more efficient for bulk operations)
     */
    public void sendFakeBlocks(Player player, List<Location> locations, Material material) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) return;

        int stateId = getStateId(material);

        // Send in chunks to avoid packet size limits
        int chunkSize = 4096; // PacketEvents multi-block change limit
        for (int i = 0; i < locations.size(); i += chunkSize) {
            List<Location> chunk = locations.subList(i, Math.min(i + chunkSize, locations.size()));

            WrapperPlayServerMultiBlockChange.EncodedBlock[] blocks =
                new WrapperPlayServerMultiBlockChange.EncodedBlock[chunk.size()];

            for (int j = 0; j < chunk.size(); j++) {
                Location loc = chunk.get(j);
                blocks[j] = new WrapperPlayServerMultiBlockChange.EncodedBlock(
                    stateId,
                    (byte) (loc.getBlockX() & 0xF),
                    (short) loc.getBlockY(),
                    (byte) (loc.getBlockZ() & 0xF)
                );
            }

            // Get chunk coordinates
            Location firstLoc = chunk.get(0);
            int chunkX = firstLoc.getBlockX() >> 4;
            int chunkZ = firstLoc.getBlockZ() >> 4;

            Vector3i chunkPosition = new Vector3i(chunkX, 0, chunkZ);
            WrapperPlayServerMultiBlockChange multiBlockChange =
                new WrapperPlayServerMultiBlockChange(chunkPosition, true, blocks);

            user.sendPacket(multiBlockChange);
        }
    }

    /**
     * Fills an entire mine with fake blocks for a player
     */
    public void fillMineWithFakeBlocks(Player player, Mine mine) {
        List<Location> blocks = mine.getAllBlocks();
        sendFakeBlocks(player, blocks, mine.getFillMaterial());
    }

    /**
     * Removes fake blocks by sending the real block state
     */
    public void sendRealBlock(Player player, Location location) {
        Material realMaterial = location.getBlock().getType();
        sendFakeBlock(player, location, realMaterial);
    }

    /**
     * Clears all fake blocks in a mine by sending real block states
     */
    public void clearMineFakeBlocks(Player player, Mine mine) {
        List<Location> blocks = mine.getAllBlocks();

        for (Location location : blocks) {
            sendRealBlock(player, location);
        }
    }

    /**
     * Gets the state ID for a material
     * This is a simplified version - in production you'd want a proper block state mapping
     */
    private int getStateId(Material material) {
        // Use Bukkit's ordinal as a basic state ID
        // For proper implementation, you'd need to map to Minecraft's actual state IDs
        try {
            return material.ordinal();
        } catch (Exception e) {
            return 0; // Default to air
        }
    }

    /**
     * Sends a fake block break animation and then restores the block
     */
    public void sendFakeBreak(Player player, Location location, Mine mine) {
        // First show the break (air)
        sendFakeBlock(player, location, Material.AIR);

        // After a short delay, restore the block
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                sendFakeBlock(player, location, mine.getFillMaterial());
            }
        }, 10L); // 0.5 second delay
    }
}
