package com.versuchdrei.menumanager.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.versuchdrei.menumanager.implementations.events.InventoryOverflowEvent;

/**
 * a util class for methods regarding players
 * @author VersuchDrei
 * @version 1.0
 */
public class PlayerUtils {
	
	public static void addOrDropItem(final Player player, final ItemStack... items) {
		final Map<Integer, ItemStack> overhead = player.getInventory().addItem(items);
		
		final List<ItemStack> itemList = overhead.values().stream().collect(Collectors.toList());
		final InventoryOverflowEvent event = new InventoryOverflowEvent(player, itemList);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			return;
		}
		
		final List<ItemStack> newItemList = event.getItems();
		for(final ItemStack item: newItemList) {
			player.getWorld().dropItem(player.getLocation(), item);
		}
	}

}
