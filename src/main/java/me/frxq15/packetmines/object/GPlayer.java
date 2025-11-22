package me.frxq15.packetmines.object;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Represents a player's data for the PacketMines system
 */
public class GPlayer {
    private final UUID uuid;
    private Location wandPos1;
    private Location wandPos2;
    private String assignedRegionId;
    private Mine assignedMine;

    public GPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getWandPos1() {
        return wandPos1;
    }

    public void setWandPos1(Location wandPos1) {
        this.wandPos1 = wandPos1;
    }

    public Location getWandPos2() {
        return wandPos2;
    }

    public void setWandPos2(Location wandPos2) {
        this.wandPos2 = wandPos2;
    }

    public String getAssignedRegionId() {
        return assignedRegionId;
    }

    public void setAssignedRegionId(String assignedRegionId) {
        this.assignedRegionId = assignedRegionId;
    }

    public Mine getAssignedMine() {
        return assignedMine;
    }

    public void setAssignedMine(Mine assignedMine) {
        this.assignedMine = assignedMine;
    }

    public boolean hasSelection() {
        return wandPos1 != null && wandPos2 != null;
    }

    public void clearSelection() {
        this.wandPos1 = null;
        this.wandPos2 = null;
    }
}
