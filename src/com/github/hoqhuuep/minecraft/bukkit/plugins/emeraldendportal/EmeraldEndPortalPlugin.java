package com.github.hoqhuuep.minecraft.bukkit.plugins.emeraldendportal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EmeraldEndPortalPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if (event.hasItem() && event.getMaterial() == Material.EYE_OF_ENDER && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock()) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.EMERALD_BLOCK) {
                // Cycle emerald block to empty ender portal frame
                block.setType(Material.ENDER_PORTAL_FRAME);

                // Calculate direction
                block.setData(getEndPortalFrameData(event.getBlockFace()));

                // Use up 1 item (if not in creative)
                ItemStack item = event.getItem();
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    item.setAmount(item.getAmount() - 1);
                }

                // Prevent default behavior
                event.setCancelled(true);
            } else if (block.getType() == Material.ENDER_PORTAL_FRAME && (block.getData() & 0x4) != 0) {
                // Cycle filled ender portal frame to emerald block
                block.setType(Material.EMERALD_BLOCK);

                // Destroy portal if present (flood fill portal with air)
                // NOTE: Can only be connected to 2 portals (theoretically)
                int count = 18;
                Collection<Block> toCheck = new ArrayList<Block>();
                toCheck.add(block);
                while (!toCheck.isEmpty() && count > 0) {
                    Iterator<Block> iterator = toCheck.iterator();
                    Block b = iterator.next();
                    iterator.remove();
                    for (BlockFace face : FACES) {
                        Block check = b.getRelative(face);
                        if (check.getType() == Material.ENDER_PORTAL) {
                            check.setType(Material.AIR);
                            toCheck.add(check);
                            --count;
                        }
                    }
                }

                // Use up 1 item (if not in creative)
                ItemStack item = event.getItem();
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    item.setAmount(item.getAmount() - 1);
                }

                // Prevent default behavior
                event.setCancelled(true);
            }
        }
    }

    private static final BlockFace[] FACES = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST };

    private static byte getEndPortalFrameData(BlockFace blockFace) {
        switch (blockFace) {
        case SOUTH:
            return 0;
        case WEST:
            return 1;
        case NORTH:
            return 2;
        case EAST:
            return 3;
        default:
            return 0;
        }
    }

}
