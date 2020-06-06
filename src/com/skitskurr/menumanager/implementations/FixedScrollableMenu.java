package com.skitskurr.menumanager.implementations;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * a ScrollableMenu with a fixed set of items
 * @author Skitskurr
 * @version 1.0
 */
public class FixedScrollableMenu extends ScrollableMenu{
	
	private final List<? extends MenuItem> items;
	
	/**
	 * the constructor of a fixed scrollable menu
	 * @param title the title of the menu
	 * @param items the list of items the menu should display
	 */
	public FixedScrollableMenu(final String title, final List<? extends MenuItem> items) {
		super(title);
		this.items = items;
	}
	
	/**
	 * returns the fixed set of menu items for every player
	 */
	@Override
	protected List<? extends MenuItem> getItems(final Player player){
		return this.items;
	}

}
