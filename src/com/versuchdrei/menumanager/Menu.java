package com.versuchdrei.menumanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import com.versuchdrei.menumanager.utils.MetadataUtils;

/**
 * abstract superclass for all menus managed by the plugin
 * @author VersuchDrei
 * @version 1.0
 */
public abstract class Menu {
	
	static final String CONFIG_KEY_ALLOW_TEXT_REQUESTS = "allowTextRequests";
	
	static final String METADATA_KEY_MENU_STACK = "menuStack";
	static final String METADATA_KEY_TEXT_REQUEST = "textRequest";
	
	/**
	 * gives the inventory to portray the menu to the player, 
	 * is called on initial creation, on redraw and when return from another menu
	 * @param player the player the menu is shown to
	 * @return the inventory representing the menu
	 */
	protected abstract Inventory getInventory(final Player player);
	
	/**
	 * overwrite this to set initial properties for a menu, 
	 * happens before calling Menu::getInventory, 
	 * so properties will already be set when the menu is generated
	 * @param player the player the menu will be opened to
	 * @return a map of the initial properties
	 */
	protected Map<String, Object> getInitialProperties(final Player player){
		return new HashMap<>();
	}
	
	/**
	 * overwrite this method to handle clicks in the chest inventory (menu)
	 * @param event the event holding the respective data
	 */
	protected void onClick(final ClickEvent event) {
	}
	
	/**
	 * overwrite this method to handle clicks in the player inventory
	 * @param event the event holding the respective data
	 */
	protected void onClickBottom(final ClickEvent event) {
	}
	
	/**
	 * overwrite this method to handle the player closing the menu
	 * @param player the player who closed the menu
	 */
	protected void onClose(final Player player) {
	}
	
	/**
	 * overwrite this method to handle receiving the answer to a text request
	 * @param event the event holding the respective data
	 */
	protected void onTextReceive(final TextEvent event) {
	}
	
	/**
	 * opens the menu to the player
	 * @param player the player to show the menu to
	 */
	public final void open(final Player player) {
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		Main.getCurrent().ifPresent(plugin -> open(player, plugin));
	}
	
	/**
	 * opens a new menu to a player, but without the possibility to go back to the previous, 
	 * therefore calls the onClose method for all previous menus
	 * @param player the player to open the new menu to
	 */
	public final void openOneWay(final Player player) {
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		final Optional<Main> optionalPlugin = Main.getCurrent();
		if(!optionalPlugin.isPresent()) {
			return;
		}
		final Main plugin = optionalPlugin.get();
		MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class).ifPresent(stack -> stack.closeAll(player));
		openWithNewStack(player, plugin);
	}
	
	/**
	 * opens the menu to the player and adds it to the stack of previous menus, or creates a new one if none is present, 
	 * the same menu object can be on the stack multiple times and will have a different property object every time
	 * @param player the player to open the menu to
	 * @param plugin the main plugin, required to access the metadata
	 */
	private final void open(final Player player, final Main plugin) {
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		if(optionalStack.isPresent()) {
			openMenu(player, optionalStack.get());
		} else {
			openWithNewStack(player, plugin);
		}
	}
	
	/**
	 * opens the menu to the player and creates a new (empty) stack of previous menus
	 * @param player the player to open the menu to
	 * @param plugin the main plugin, required to access the metadata
	 */
	private final void openWithNewStack(final Player player, final Main plugin) {
		final MenuStack stack = new MenuStack();
		player.setMetadata(Menu.METADATA_KEY_MENU_STACK, new FixedMetadataValue(plugin, stack));
		openMenu(player, stack);
	}
	
	/**
	 * adds the menu to the stack and shows it to the player
	 * @param player the player to show the menu to
	 * @param stack the stack to add the menu to
	 */
	private final void openMenu(final Player player, final MenuStack stack) {
		stack.push(this, new MenuProperties(getInitialProperties(player)));
		openInventory(player, stack);
	}
	
	/**
	 * opens the inventory representing the menu to the player
	 * @param player the player to open the inventory to
	 * @param plugin the main plugin, required to access the metadata
	 */
	private final void openInventory(final Player player, final MenuStack stack) {
		// this causes an InventoryClosedEvent if the player already had an inventory open
		// but in this specific case we need the stack to stay on the player
		// so we set the stack to be close persistent
		stack.setClosePersistent(true);
		player.openInventory(getInventory(player));
		stack.setClosePersistent(false);
	}
	
	/**
	 * requests a text input of the given player and when given calls onTextReceive 
	 * and redraws in that order, if the player refuses to input text calls onClose instead, 
	 * fails if the config setting "allowtextrequests" is set to false
	 * @param player the player to sent the request to
	 * @param key the key to identify the request
	 * @param message the message to display to the player while waiting for the text
	 * @return true if text request was sent, otherwise false
	 */
	protected static boolean requestText(final Player player, final String key, final String message) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires config access and MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return false;
		}
		
		final Main plugin = optionalPlugin.get();
		// if the config does not allow text requests we abort
		if(!plugin.getConfig().getBoolean(Menu.CONFIG_KEY_ALLOW_TEXT_REQUESTS)) {
			return false;
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(plugin, player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if there are no menus on the stack we have nothing to give the answer to
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return false;
		}
		
		final TextRequest request = new TextRequest(plugin, optionalStack.get(), key);
		player.setMetadata(Menu.METADATA_KEY_TEXT_REQUEST, new FixedMetadataValue(plugin, request));
		player.removeMetadata(Menu.METADATA_KEY_MENU_STACK, plugin);
		
		player.closeInventory();
		player.sendTitle("", message, 10, 70, 10);
		return true;
	}
	
	/**
	 * goes one menu back in the chain, fails if there is no previous menu
	 * @param player the player to show the previous menu to
	 * @return true if the previous menu was opened, otherwise false
	 */
	protected static boolean back(final Player player) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return false;
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(optionalPlugin.get(), player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if there are no menus on the stack we have nothing to go back to
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return false;
		}
		
		final MenuStack stack = optionalStack.get();
		// if the menu is the last menu on the stack there is no previous to go to
		if(!stack.hasPrevious()) {
			return false;
		}
		
		stack.getCurrentMenu().get().onClose(player);
		
		stack.pop();
		stack.getCurrentMenu().get().openInventory(player, stack);
		return true;
	}
	
	/**
	 * goes one menu back in the chain, or on fail closes the menu
	 * @param player the player to show the previous menu to
	 */
	protected static void backOrClose(final Player player) {
		if (!back(player)) {
			player.getOpenInventory().close();
		}
	}
	
	/**
	 * generates the menu of the current player anew without resetting the properties or adding to the stack, 
	 * fails if allowRedraw on the current menu is set to false
	 * @param player the player to redraw the menu to
	 * @return true if the menu was redrawn, otherwise false
	 */
	protected static boolean redraw(final Player player) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return false;
		}

		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(optionalPlugin.get(), player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no stack is present there also is no current menu
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return false;
		}
		
		final MenuStack stack = optionalStack.get();

		final Optional<Menu> optionalMenu = stack.getCurrentMenu();
		// if there is no menu in the stack we cannot act
		// this case however should only occur if the player is not currently in a menu
		if(!optionalMenu.isPresent()) {
			return false;
		}
		
		optionalMenu.get().openInventory(player, stack);
		return true;
	}
	
	/**
	 * returns if text requests are allowed
	 * @return true if text requests are allowed, otherwise false
	 */
	protected static boolean isTextRequestingAllowed() {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires config access it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return false;
		}
		
		return optionalPlugin.get().getConfig().getBoolean(Menu.CONFIG_KEY_ALLOW_TEXT_REQUESTS);
	}
	
	/**
	 * checks if the given player has a previous menu to return to with the Menu::back method
	 * @param player the player to check for a previous menu
	 * @return true if a previous menu is present, otherwise false
	 */
	protected static boolean hasPrevious(final Player player) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return false;
		}

		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(optionalPlugin.get(), player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no stack is present there also cannot be a previous menu on it
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return false;
		}
		
		return optionalStack.get().hasPrevious();
	}
	
	/**
	 * sets the given property for the given player their current menu under the given key
	 * @param player the player to set the property for
	 * @param key the key to identify the property
	 * @param value the value to set the property to
	 * @return true if the property was set, otherwise false
	 */
	protected static boolean setProperty(final Player player, final String key, final Object value) {
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return false;
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(optionalPlugin.get(), player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no stack is present there also cannot be MenuProperties
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return false;
		}
		
		final Optional<MenuProperties> optionalProperties = optionalStack.get().getCurrentProperties();
		// if the stack is empty we cannot get any properties
		// this case however should only occur if the player is not currently in a menu
		if(!optionalProperties.isPresent()) {
			return false;
		}
		
		optionalProperties.get().set(key, value);
		return true;
	}
	
	/**
	 * gets the property for the given player and their current menu under the given key cast into the given class
	 * @param <T> the desired return type
	 * @param player the player to get the property for
	 * @param key the key to identify the property
	 * @param type the class of the return type
	 * @return an {@link Optional} describing the value of property
	 */
	protected static <T> Optional<T> getProperty(final Player player, final String key, final Class<T> type){
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return Optional.empty();
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(optionalPlugin.get(), player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no stack is present there also cannot be MenuProperties
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return Optional.empty();
		}
		
		final Optional<MenuProperties> optionalProperties = optionalStack.get().getCurrentProperties();
		// if the stack is empty we cannot get any properties
		// this case however should only occur if the player is not currently in a menu
		if(!optionalProperties.isPresent()) {
			return Optional.empty();
		}
		
		return optionalProperties.get().get(key, type);
	}
	
	/**
	 * gets the properties object for the given player and their current menu, containing all properties for the current menu
	 * @param player the player to get the properties for
	 * @return the an {@link Optional} describing the properties
	 */
	protected static Optional<MenuProperties> getProperties(final Player player){
		final Optional<Main> optionalPlugin = Main.getCurrent();
		// since this process requires MetaData it can only be done with a plugin instance
		// so if none is there we cannot act
		// this case however should never occur unless something went wrong
		if(!optionalPlugin.isPresent()) {
			return Optional.empty();
		}
		
		final Optional<MenuStack> optionalStack = MetadataUtils.getMetadata(optionalPlugin.get(), player, Menu.METADATA_KEY_MENU_STACK, MenuStack.class);
		// if no stack is present there also cannot be MenuProperties
		// this case however should only occur if the player is not currently in a menu
		if(!optionalStack.isPresent()) {
			return Optional.empty();
		}
		
		return optionalStack.get().getCurrentProperties();
	}

}
