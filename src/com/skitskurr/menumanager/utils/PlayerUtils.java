package com.skitskurr.menumanager.utils;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
	
	public static void addOrDropItem(final Player player, final ItemStack... items) {
		final Map<Integer, ItemStack> overhead = player.getInventory().addItem(items);
		for(final ItemStack item: overhead.values()) {
			player.getWorld().dropItem(player.getLocation(), item);
		}
	}

}
