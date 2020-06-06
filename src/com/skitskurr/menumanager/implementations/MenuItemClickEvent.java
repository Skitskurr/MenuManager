package com.skitskurr.menumanager.implementations;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * a wrapper class for the parameters of a click event
 * @author Skitskurr
 * @version 1.0
 */
public class MenuItemClickEvent {
	
	private final Player player;
	private final ClickType type;
	private final boolean isDoubleClick;
	private boolean redraw = false;
	
	/**
	 * constructor for a menu item click event
	 * @param player the player who executed the click
	 * @param type the type of the click executed
	 * @param isDoubleClick if the click was a double click
	 */
	MenuItemClickEvent(final Player player, final ClickType type, final boolean isDoubleClick){
		this.player = player;
		this.type = type;
		this.isDoubleClick = isDoubleClick;
	}
	
	/**
	 * returns the player who executed the click
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * returns the type of the click executed
	 * @return the type of the click
	 */
	public ClickType getType() {
		return this.type;
	}
	
	/**
	 * returns true if, and only if, the click is the second of a double click, 
	 * note that the first click of the double click has also caused a click event
	 * @return true if it was a double click, otherwise false
	 */
	public boolean isDoubleClick() {
		return this.isDoubleClick;
	}
	
	/**
	 * sets if the menu should be redrawn after this click event was handled
	 * @param redraw if the menu should be redrawn
	 */
	public void setRedraw(final boolean redraw) {
		this.redraw = redraw;
	}
	
	/**
	 * returns if the menu should be redrawn
	 * @return true if the menu should be redrawn, otherwise false
	 */
	boolean getRedraw() {
		return this.redraw;
	}

}
