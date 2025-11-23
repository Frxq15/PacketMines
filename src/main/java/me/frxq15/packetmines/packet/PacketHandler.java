package me.frxq15.packetmines.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.object.Mine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Group blocks by chunk coordinates
        Map<Long, List<Location>> blocksByChunk = new HashMap<>();

        for (Location loc : locations) {
            int chunkX = loc.getBlockX() >> 4;
            int chunkZ = loc.getBlockZ() >> 4;
            long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

            blocksByChunk.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(loc);
        }

        // Send a packet for each chunk
        for (Map.Entry<Long, List<Location>> entry : blocksByChunk.entrySet()) {
            long chunkKey = entry.getKey();
            List<Location> chunkBlocks = entry.getValue();

            int chunkX = (int) (chunkKey >> 32);
            int chunkZ = (int) chunkKey;

            // Create encoded blocks for this chunk
            WrapperPlayServerMultiBlockChange.EncodedBlock[] blocks =
                new WrapperPlayServerMultiBlockChange.EncodedBlock[chunkBlocks.size()];

            for (int i = 0; i < chunkBlocks.size(); i++) {
                Location loc = chunkBlocks.get(i);
                // Calculate relative position within the chunk (0-15 for X and Z)
                int relX = loc.getBlockX() & 0xF;
                int relZ = loc.getBlockZ() & 0xF;

                blocks[i] = new WrapperPlayServerMultiBlockChange.EncodedBlock(
                    stateId,
                    (byte) relX,
                    (short) loc.getBlockY(),
                    (byte) relZ
                );
            }

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
     * Gets the state ID for a material using PacketEvents' StateTypes
     */
    private int getStateId(Material material) {
        try {
            // Convert Bukkit Material name to PacketEvents StateType
            String materialName = material.name();

            // Use PacketEvents' StateTypes to get the proper state
            // StateTypes uses the same naming convention as Bukkit materials
            WrappedBlockState blockState = WrappedBlockState.getByString("minecraft:" + materialName.toLowerCase());

            if (blockState != null) {
                return blockState.getGlobalId();
            }

            // Fallback: try to get by StateTypes enum
            try {
                var stateType = StateTypes.getByName("minecraft:" + materialName.toLowerCase());
                if (stateType != null) {
                    return stateType.getDefaultState().getGlobalId();
                }
            } catch (Exception ignored) {
            }

            // Default to air if not found
            return StateTypes.AIR.getDefaultState().getGlobalId();
        } catch (Exception e) {
            return StateTypes.AIR.getDefaultState().getGlobalId();
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
