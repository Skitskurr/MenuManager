package com.versuchdrei.menumanager;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.versuchdrei.menumanager.utils.MetadataUtils;

/**
 * the main plugin of the menu manager
 * @author VersuchDrei
 * @version 1.0
 */
public class Main extends JavaPlugin{
	
	private static Main current;
	
	/**
	 * returns the current instance of the main plugin
	 * @return the instance of the main plugin
	 */
	public static Optional<Main> getCurrent(){
		if(current == null) {
			return Optional.empty();
		}
		return Optional.of(current);
	}
	
	/**
	 * registers the event listener and sets the current instance
	 */
	@Override
	public void onEnable() {
		super.saveDefaultConfig();
		Main.current = this;
		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
	}
	
	/**
	 * unsets the current instance
	 */
	@Override
	public void onDisable() {
		// in case of a reload we have to close all menus so that players cannot loot them
		for(final Player player: Bukkit.getOnlinePlayers()) {
			if(MetadataUtils.getMetadata(this, player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class).isPresent()) {
				player.closeInventory();
			}
		}
		Main.current = null;
	}

}
