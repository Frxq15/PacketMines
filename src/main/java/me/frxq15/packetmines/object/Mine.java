package me.frxq15.packetmines.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Represents an individual player's mine
 * Contains fake blocks sent via packets
 */
public class Mine implements ConfigurationSerializable {
    private final UUID playerUUID;
    private Location pos1;
    private Location pos2;
    private Material fillMaterial;
    private final String regionId;

    public Mine(UUID playerUUID, Location pos1, Location pos2, String regionId) {
        this.playerUUID = playerUUID;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.regionId = regionId;
        this.fillMaterial = Material.STONE;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
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

    public Material getFillMaterial() {
        return fillMaterial;
    }

    public void setFillMaterial(Material material) {
        this.fillMaterial = material;
    }

    public String getRegionId() {
        return regionId;
    }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(pos1.getWorld())) return false;

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

    public void expand(int amount) {
        // Expand in all horizontal directions
        pos2.setX(pos2.getX() + amount);
        pos2.setZ(pos2.getZ() + amount);
    }

    public int getVolume() {
        int sizeX = (int) Math.abs(pos2.getBlockX() - pos1.getBlockX()) + 1;
        int sizeY = (int) Math.abs(pos2.getBlockY() - pos1.getBlockY()) + 1;
        int sizeZ = (int) Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) + 1;
        return sizeX * sizeY * sizeZ;
    }

    public List<Location> getAllBlocks() {
        List<Location> blocks = new ArrayList<>();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new Location(pos1.getWorld(), x, y, z));
                }
            }
        }

        return blocks;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUUID", playerUUID.toString());
        map.put("pos1", pos1);
        map.put("pos2", pos2);
        map.put("fillMaterial", fillMaterial.name());
        map.put("regionId", regionId);
        return map;
    }

    public static Mine deserialize(Map<String, Object> map) {
        UUID playerUUID = UUID.fromString((String) map.get("playerUUID"));
        Location pos1 = (Location) map.get("pos1");
        Location pos2 = (Location) map.get("pos2");
        String regionId = (String) map.get("regionId");

        Mine mine = new Mine(playerUUID, pos1, pos2, regionId);

        if (map.containsKey("fillMaterial")) {
            mine.setFillMaterial(Material.valueOf((String) map.get("fillMaterial")));
        }

        return mine;
    }
}
