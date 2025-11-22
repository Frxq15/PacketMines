package me.frxq15.packetmines.object;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Represents a physical region that can contain multiple player mines
 * Max 10 players per region (can be exceeded if no available regions)
 */
public class MineRegion implements ConfigurationSerializable {
    private final String id;
    private Location pos1;
    private Location pos2;
    private final World world;
    private final Set<UUID> assignedPlayers;
    private final int maxPlayers;

    public MineRegion(String id, Location pos1, Location pos2, int maxPlayers) {
        this.id = id;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.world = pos1.getWorld();
        this.assignedPlayers = new HashSet<>();
        this.maxPlayers = maxPlayers;
    }

    public String getId() {
        return id;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public World getWorld() {
        return world;
    }

    public Set<UUID> getAssignedPlayers() {
        return assignedPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isFull() {
        return assignedPlayers.size() >= maxPlayers;
    }

    public void addPlayer(UUID uuid) {
        assignedPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        assignedPlayers.remove(uuid);
    }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) return false;

        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("pos1", pos1);
        map.put("pos2", pos2);
        map.put("maxPlayers", maxPlayers);
        map.put("assignedPlayers", new ArrayList<>(assignedPlayers).stream().map(UUID::toString).toArray(String[]::new));
        return map;
    }

    public static MineRegion deserialize(Map<String, Object> map) {
        String id = (String) map.get("id");
        Location pos1 = (Location) map.get("pos1");
        Location pos2 = (Location) map.get("pos2");
        int maxPlayers = (int) map.getOrDefault("maxPlayers", 10);

        MineRegion region = new MineRegion(id, pos1, pos2, maxPlayers);

        if (map.containsKey("assignedPlayers")) {
            List<String> uuids = (List<String>) map.get("assignedPlayers");
            for (String uuidStr : uuids) {
                region.addPlayer(UUID.fromString(uuidStr));
            }
        }

        return region;
    }
}
