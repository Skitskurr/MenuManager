package com.skitskurr.menumanager.implementations;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * a YesNoMenu using Consumers for the yes/no clicks
 * @author Skitskurr
 * @version 1.0
 */
public class LambdaYesNoMenu extends YesNoMenu{
	
	private final Consumer<Player> onYes;
	private final Consumer<Player> onNo;
	
	/**
	 * the constructor of a lambda yes/no menu with a title and an action on clicking yes
	 * @param title the title of the menu
	 * @param onYes the action for a player who clicked yes
	 */
	public LambdaYesNoMenu(final String title, final Consumer<Player> onYes) {
		super(title);
		
		this.onYes = onYes;
		this.onNo = null;
	}
	
	/**
	 * the constructor of a lambda yes/no menu with a title and an action on clicking yes or no
	 * @param title the title of the menu
	 * @param onYes the action for a player who clicked yes
	 * @param onNo the action for a player who clicked no
	 */
	public LambdaYesNoMenu(final String title, final Consumer<Player> onYes, final Consumer<Player> onNo) {
		super(title);
		
		this.onYes = onYes;
		this.onNo = onNo;
	}
	
	/**
	 * the constructor of a lambda yes/no menu with a title, an item to display and an action on clicking yes
	 * @param title the title of the menu
	 * @param item the item to display in the middle of the menu
	 * @param onYes the action for a player who clicked yes
	 */
	public LambdaYesNoMenu(final String title, final ItemStack item, final Consumer<Player> onYes) {
		super(title, item);
		
		this.onYes = onYes;
		this.onNo = null;
	}
	
	/**
	 * the constructor of a lambda yes/no menu with a title, an item to display and an action on clicking yes or no
	 * @param title the title of the menu
	 * @param item the item to display in the middle of the menu
	 * @param onYes the action for a player who clicked yes
	 * @param onNo the action for a player who clicked no
	 */
	public LambdaYesNoMenu(final String title, final ItemStack item, final Consumer<Player> onYes, final Consumer<Player> onNo) {
		super(title, item);
		
		this.onYes = onYes;
		this.onNo = onNo;
	}
	
	/**
	 * handles a player clicking yes by calling the respective consumer
	 */
	@Override
	protected void onYes(final Player player) {
		if(this.onYes != null) {
			this.onYes.accept(player);
		}
	}
	
	/**
	 * handles a player clicking no by calling the respective consumer
	 */
	@Override
	protected void onNo(final Player player) {
		if(this.onNo != null) {
			this.onNo.accept(player);
		}
	}

}
