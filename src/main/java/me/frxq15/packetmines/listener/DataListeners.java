package me.frxq15.packetmines.listener;

import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.object.GPlayer;
import me.frxq15.packetmines.object.Mine;
import me.frxq15.packetmines.object.MineRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles player data events like login, wand usage, and block breaking
 */
public class DataListeners implements Listener {
    private final PacketMines plugin;

    public DataListeners(PacketMines plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GPlayer gPlayer = plugin.getMineManager().getPlayer(player);

        // Check if player has an assigned region
        if (gPlayer.getAssignedRegionId() == null) {
            // Auto-assign to a region
            MineRegion region = plugin.getMineManager().autoAssignRegion(player.getUniqueId());

            if (region != null) {
                // Create a default mine for the player
                Mine mine = plugin.getMineManager().createDefaultMine(player.getUniqueId(), region);

                // Fill the mine with fake blocks
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        plugin.getPacketHandler().fillMineWithFakeBlocks(player, mine);
                    }
                }, 20L); // Wait 1 second after join

                player.sendMessage(plugin.format("&aYou have been assigned to mine region: &e" + region.getId()));
            } else {
                player.sendMessage(plugin.format("&cNo mine regions available! Contact an administrator."));
            }
        } else {
            // Player already has a region, reload their mine
            Mine mine = plugin.getMineManager().getPlayerMine(player);
            if (mine != null) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        plugin.getPacketHandler().fillMineWithFakeBlocks(player, mine);
                    }
                }, 20L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Keep player data in memory, but could be saved to disk
        // We don't remove the player from their region on quit
    }

    @EventHandler
    public void onWandInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !plugin.getItemUtils().isWand(item)) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        Location location = clickedBlock.getLocation();
        GPlayer gPlayer = plugin.getMineManager().getPlayer(player);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Set position 1
            event.setCancelled(true);
            gPlayer.setWandPos1(location);
            player.sendMessage(plugin.format("&aPosition 1 set to: &e" +
                location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));

        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Set position 2
            event.setCancelled(true);
            gPlayer.setWandPos2(location);
            player.sendMessage(plugin.format("&aPosition 2 set to: &e" +
                location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));

            if (gPlayer.hasSelection()) {
                player.sendMessage(plugin.format("&aSelection complete! Use &e/amine create <regionId> &ato create a region."));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        // Check if player has a mine
        Mine mine = plugin.getMineManager().getPlayerMine(player);
        if (mine == null) {
            return;
        }

        // Check if the block is in the player's mine
        if (!mine.contains(location)) {
            return;
        }

        // Check if player is using the custom pickaxe
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool != null && plugin.getItemUtils().isPickaxe(tool)) {
            // Cancel the real break and send fake break
            event.setCancelled(true);

            // Send fake break animation
            plugin.getPacketHandler().sendFakeBreak(player, location, mine);

            // Give player the drop (optional - you can customize this)
            Material dropMaterial = mine.getFillMaterial();
            if (dropMaterial != Material.AIR && dropMaterial.isBlock()) {
                player.getInventory().addItem(new ItemStack(dropMaterial, 1));
            }
        }
    }
}
