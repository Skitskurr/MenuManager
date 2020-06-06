package com.skitskurr.menumanager;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * a wrapper class for the parameters of a click event
 * @author Skitskurr
 * @version 1.0
 */
public class ClickEvent {
	
	private final Player player;
	private final ItemStack item;
	private final int slot;
	private final ClickType clickType;
	private final boolean isDoubleClick;
	
	/**
	 * constructor for a click event
	 * @param player the player who executed the click
	 * @param item the item the player clicked on
	 * @param slot the inventory slot the clicked item was in
	 * @param clickType the type of the click executed
	 * @param isDoubleClick if the click was a double click
	 */
	ClickEvent(final Player player, final ItemStack item, final int slot, final ClickType clickType, final boolean isDoubleClick){
		this.player = player;
		this.item = item;
		this.slot = slot;
		this.clickType = clickType;
		this.isDoubleClick = isDoubleClick;
	}
	
	/**
	 * returns the player who executed the click
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * returns the item the player clicked on
	 * @return the ItemStack representing the item
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * returns the inventory slot the clicked item is in
	 * @return the index of the slot
	 */
	public int getSlot() {
		return slot;
	}
	
	/**
	 * returns the inventory row the clicked item is in, 
	 * note that lower inventories row 0 is the hotbar
	 * @return the index of the row
	 */
	public int getRow() {
		return slot / 9;
	}
	
	/**
	 * returns the inventory column the clicked item is in
	 * @return the index of the column
	 */
	public int getColumn() {
		return slot % 9;
	}
	
	/**
	 * returns the type of the click executed
	 * @return the type of the click
	 */
	public ClickType getClickType() {
		return clickType;
	}
	
	/**
	 * returns true if, and only if, the click is the second of a double click, 
	 * note that the first click of the double click has also caused a click event
	 * @return true if it was a double click, otherwise false
	 */
	public boolean isDoubleClick() {
		return isDoubleClick;
	}

}
