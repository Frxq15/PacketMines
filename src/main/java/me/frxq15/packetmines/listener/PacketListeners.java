package me.frxq15.packetmines.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.frxq15.packetmines.PacketMines;
import me.frxq15.packetmines.object.Mine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Listens to PacketEvents for handling fake block interactions
 */
public class PacketListeners extends PacketListenerAbstract {
    private final PacketMines plugin;

    public PacketListeners(PacketMines plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Check if this is a player digging packet
        if (event.getPacketType() != PacketType.Play.Client.PLAYER_DIGGING) {
            return;
        }

        // Get the player
        Player player = (Player) event.getPlayer();
        if (player == null) {
            return;
        }

        // Parse the digging packet
        WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
        DiggingAction action = packet.getAction();

        // We want to handle when the player finishes breaking the block
        if (action != DiggingAction.FINISHED_DIGGING) {
            return;
        }

        // Get the block position
        Vector3i blockPos = packet.getBlockPosition();
        Location location = new Location(
            player.getWorld(),
            blockPos.getX(),
            blockPos.getY(),
            blockPos.getZ()
        );

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
        if (tool == null || !plugin.getItemUtils().isPickaxe(tool)) {
            return;
        }

        // Cancel the packet so the server doesn't process it normally
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
