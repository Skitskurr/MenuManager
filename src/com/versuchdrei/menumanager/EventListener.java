package com.versuchdrei.menumanager;

import java.util.Optional;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.versuchdrei.menumanager.utils.MetadataUtils;

import net.minecraft.server.v1_16_R3.MinecraftServer;

/**
 * the event listener of the menu manager, 
 * handles inventory and chat events and informs the players current menu
 * @author VersuchDrei
 * @version 1.0
 */
public class EventListener implements Listener{
	
	private static final String CONFIG_KEY_TICKS_FOR_DOUBLE_CLICK = "ticksForDoubleClick";
	
	private final Main plugin;
	private final int ticksForDoubleClick;
	
	/**
	 * constructor for a event listener
	 * @param plugin the main plugin
	 */
	public EventListener(final Main plugin) {
		this.plugin = plugin;
		ticksForDoubleClick = plugin.getConfig().getInt(EventListener.CONFIG_KEY_TICKS_FOR_DOUBLE_CLICK);
	}
	
	/**
	 * checks if a quitting player had a menu open and if so calls the onClose method for all menus on the stack
	 * @param event
	 */
	public void onQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		
		MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class)
				.ifPresent(stack -> stack.closeAll(player));
		
		MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_TEXT_REQUEST, TextRequest.class)
				.ifPresent(request -> request.cancel(player));
	}
	
	/**
	 * checks for a pending text request and cancels it when the player opens another inventory
	 * @param event the event to be handled
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onOpen(final InventoryOpenEvent event) {
		final HumanEntity entity = event.getPlayer();
		
		// if the event was not caused by a player there is nothing to do here
		if(!(entity instanceof Player)) {
			return;
		}
		
		MetadataUtils.getMetadata(plugin, entity, Menu.METADATA_KEY_TEXT_REQUEST, TextRequest.class)
				.ifPresent(request -> request.cancel((Player) entity));
	}
	
	/**
	 * checks if the closed inventory was a menu and if so calls the onClose method of all menus on the stack
	 * @param event the event to be handled
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClose(final InventoryCloseEvent event) {
		final HumanEntity entity = event.getPlayer();
		
		// if the event was not caused by a player there is nothing to do here
		if(!(entity instanceof Player)) {
			return;
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(plugin, entity, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no metadata is present there is no menu to give the close event to
		if(!optionalStack.isPresent()) {
			return;
		}
		
		final MenuStack stack = optionalStack.get();
		// if the stack was set to persist inventory closing we do not remove it
		if(stack.isClosePersistent()) {
			return;
		}
		
		stack.closeAll((Player) entity);
		entity.removeMetadata(Menu.METADATA_KEY_MENU_STACK, plugin);
	}
	
	/**
	 * checks if the inventory is a menu and if so calls its onClick or onClickBottom method
	 * @param event the event to be handled
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(final InventoryClickEvent event){
		// if the click was outside of the menu there is nothing to do here
		if(event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
			return;
		}
		
		final HumanEntity entity = event.getWhoClicked();
		// if the event was not caused by a player there is nothing to do here
		if(!(entity instanceof Player)) {
			return;
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(plugin, entity, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no metadata is present there is no menu to give the click event to
		if(!optionalStack.isPresent()) {
			return;
		}
		
		final MenuStack stack = optionalStack.get();
		
		final Optional<Menu> optionalMenu = stack.getCurrentMenu();
		// if there is no current menu we cannot give the click event to it
		if(!optionalMenu.isPresent()) {
			return;
		}
		
		final ClickEvent clickEvent = new ClickEvent((Player) entity, event.getCurrentItem(), event.getSlot(), event.getClick(), stack.checkDoubleClick(MinecraftServer.currentTick, this.ticksForDoubleClick));
		if(event.getSlot() != event.getRawSlot()) {
			optionalMenu.get().onClickBottom(clickEvent);
		} else {
			optionalMenu.get().onClick(clickEvent);
		}
		
		event.setCancelled(true);
	}
	
	/**
	 * checks for a pending text request and answers it when the player writes a message
	 * @param event the event to be handled
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(final AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		
		final Optional<TextRequest> optionalRequest = MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_TEXT_REQUEST, TextRequest.class);
		// if no metadata is present there is no request to answer to
		if(!optionalRequest.isPresent()) {
			return;
		}
		
		optionalRequest.get().answer(player, event.getMessage());
		event.setCancelled(true);	
	}
	
	/**
	 * checks for a pending text request and cancels it when the player uses a command
	 * @param event
	 */
	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		
		MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_TEXT_REQUEST, TextRequest.class)
				.ifPresent(request -> request.cancel(player));
	}

}
