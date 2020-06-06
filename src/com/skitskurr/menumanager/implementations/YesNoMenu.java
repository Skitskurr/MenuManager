package com.skitskurr.menumanager.implementations;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.skitskurr.menumanager.ClickEvent;
import com.skitskurr.menumanager.Menu;
import com.skitskurr.menumanager.utils.ItemUtils;

/**
 * an example implementation of a menu that gives the player the options of yes and no
 * @author Skitskurr
 * @version 1.0
 */
public abstract class YesNoMenu extends Menu{
	
	private static final int[] yesSlots = {0, 1, 2, 9, 10, 11, 18, 19, 20};
	private static final int[] noSlots = {6, 7, 8, 15, 16, 17, 24, 25, 26};
	
	private static final ItemStack yesItem = ItemUtils.newItem(Material.GREEN_STAINED_GLASS_PANE, "§ayes");
	private static final ItemStack noItem = ItemUtils.newItem(Material.RED_STAINED_GLASS_PANE, "§cno");
	
	private final String title;
	private final ItemStack item;
	
	/**
	 * the constructor of a yes/no menu without an item to display
	 * @param title the title of the menu
	 */
	public YesNoMenu(final String title) {
		this.title = title;
		this.item = null;
	}
	
	/**
	 * the constructor of a yes/no menu with an item to display in the middle
	 * @param title the title of the menu
	 * @param item the item to display in the middle of the menu
	 */
	public YesNoMenu(final String title, final ItemStack item) {
		this.title = title;
		this.item = item;
	}

	/**
	 * creates an inventory with yes and no buttons on each side
	 */
	@Override
	protected Inventory getInventory(final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 27, this.title);
		
		for(final int slot: yesSlots) {
			inventory.setItem(slot, YesNoMenu.yesItem);
		}
		
		for(final int slot: noSlots) {
			inventory.setItem(slot, YesNoMenu.noItem);
		}
		
		if(this.item != null) {
			inventory.setItem(13, this.item);
		}
		
		return inventory;
	}
	
	/**
	 * handles clicks to the yes or no buttons, then returns to the previous menu
	 */
	@Override
	protected void onClick(final ClickEvent event) {
		final int slot = event.getSlot();
		if(Arrays.stream(YesNoMenu.yesSlots).anyMatch(yesSlot -> yesSlot == slot)) {
			final Player player = event.getPlayer();
			onYes(player);
			super.back(player);
		} else if(Arrays.stream(YesNoMenu.noSlots).anyMatch(noSlot -> noSlot == slot)) {
			final Player player = event.getPlayer();
			onNo(player);
			super.back(player);
		}
	}
	
	/**
	 * handles a player clicking yes
	 * @param player the player who clicked yes
	 */
	protected abstract void onYes(Player player);
	
	/**
	 * handles a player clicking no
	 * @param player the player who clicked no
	 */
	protected abstract void onNo(Player player);

}
