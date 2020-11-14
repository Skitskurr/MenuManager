package com.versuchdrei.menumanager.implementations;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * a class describing an item in a menu
 * @author VersuchDrei
 * @version 1.0
 */
public abstract class MenuItem {
	
	/**
	 * returns the ItemStack representing the MenuItem
	 * @param player the player to show the item to
	 * @return the ItemStack to display
	 */
	protected abstract ItemStack getItem(final Player player);
	
	/**
	 * returns true if, and only if, the item matches a given filter text
	 * @param filter the text to filter for
	 * @return true if the item approves the filter text, otherwise false
	 */
	protected abstract boolean filter(final String filter);
	
	/**
	 * overwrite this method to handle clicks to the item
	 * @param event the event holding the respective data
	 */
	protected void onClick(final MenuItemClickEvent event) {
	}

}
