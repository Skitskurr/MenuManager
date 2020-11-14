package com.versuchdrei.menumanager;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * a class for all information involved by a text request
 * @author VersuchDrei
 * @version 1.0
 */
public class TextRequest {
	
	private final Main plugin;
	private final MenuStack stack;
	private final String key;
	
	private boolean answered = false;
	
	/**
	 * constructor for a text request
	 * @param plugin the instance of the main plugin
	 * @param stack the stack of the current and previous menus
	 * @param key the key to identify the request
	 */
	TextRequest(final Main plugin, final MenuStack stack, final String key){
		this.plugin = plugin;
		this.stack = stack;
		this.key = key;
	}
	
	/**
	 * answers the text request of the menu
	 * @param player the player the request was sent to
	 * @param text the text that got answered
	 */
	synchronized void answer(final Player player, final String text) {
		// text messages are handled in another thread
		// so there can be a race condition that leads to the request being answered twice
		// so in case of a second answer we ignore it
		if(answered) {
			return;
		}
		
		answered = true;
		player.removeMetadata(Menu.METADATA_KEY_TEXT_REQUEST, plugin);
		
		final Optional<Menu> optionalMenu = stack.getCurrentMenu();
		// if there is no menu in the stack we cannot act
		// this case however should only occur if the player hasn't been in a menu when the text was requested
		if(!optionalMenu.isPresent()){
			return;
		}
		
		player.setMetadata(Menu.METADATA_KEY_MENU_STACK, new FixedMetadataValue(plugin, this.stack));
		// inventories can only be opened in the main thread
		// so we have to do that in a synchronous runnable
		new BukkitRunnable() {
			@Override
			public void run() {
				optionalMenu.get().onTextReceive(new TextEvent(player, key, text));
				Menu.redraw(player);
			}
		}.runTask(plugin);
	}
	
	/**
	 * cancels a text request
	 * @param player the player the request was sent to
	 */
	void cancel(final Player player) {
		player.removeMetadata(Menu.METADATA_KEY_TEXT_REQUEST, plugin);
		// the menu might need to access properties in the close handling, so we have to reapply the stack first
		player.setMetadata(Menu.METADATA_KEY_MENU_STACK, new FixedMetadataValue(plugin, this.stack));
		stack.closeAll(player);
		player.removeMetadata(Menu.METADATA_KEY_MENU_STACK, plugin);
	}

}
