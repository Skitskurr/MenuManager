package com.versuchdrei.menumanager.implementations;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.versuchdrei.menumanager.utils.ItemUtils;

/**
 * a menu item with a fixed display ItemStack
 * @author VersuchDrei
 * @version 1.0
 */
public class FixedMenuItem extends MenuItem{
	
	private final ItemStack item;
	private final String filterText;
	
	/**
	 * the constructor of a fixed menu item
	 * @param item the ItemStack to display in menus
	 */
	public FixedMenuItem(final ItemStack item) {
		this.item = item;
		this.filterText = getFilterText(item);
	}

	/**
	 * returns the ItemStack representing the MenuItem
	 * @param player the player to show the item to
	 * @return the ItemStack to display
	 */
	@Override
	protected ItemStack getItem(final Player player) {
		return this.item;
	}
	
	/**
	 * returns true if, and only if, the display name of the item matches a given filter text
	 * @param filter the text to filter for
	 * @return true if the item approves the filter text, otherwise false
	 */
	@Override
	protected boolean filter(final String filter) {
		return this.filterText.contains(filter);
	}
	
	/**
	 * returns the display name, or if it has none the material name, of the given item
	 * @param item the item to determine the name of
	 * @return the name of the item
	 */
	private String getFilterText(final ItemStack item) {
		final ItemMeta meta = item.getItemMeta();
		final String displayName = meta.getDisplayName();
		if(displayName != null) {
			return displayName;
		}
		
		return ItemUtils.getItemName(item.getType());
	}

}
