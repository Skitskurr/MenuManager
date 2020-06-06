package com.skitskurr.menumanager.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.skitskurr.menumanager.ClickEvent;
import com.skitskurr.menumanager.Menu;
import com.skitskurr.menumanager.TextEvent;
import com.skitskurr.menumanager.utils.ItemUtils;
import com.skitskurr.menumanager.utils.MHFHead;

/**
 * an example implementation of a menu which allows a player to scroll through a list of menu items
 * @author Skitskurr
 * @version 1.0
 */
public abstract class ScrollableMenu extends Menu{

	private static final String PROPERTY_KEY_ITEM_LIST = "itemList";
	private static final String PROPERTY_KEY_CURRENT_ROW = "currentRow";
	private static final String PROPERTY_KEY_FILTERED_LIST = "fitleredList";
	private static final String PROPERTY_KEY_FILTER_TEXT = "filterText";
	
	private static final String TEXT_REQUEST_KEY_FILTER_TEXT = "filterText";
	
	private final String title;
	
	/**
	 * the constructor of a scrollable menu
	 * @param title the title of the menu
	 * @param items the list of items the menu should display
	 */
	public ScrollableMenu(final String title) {
		this.title = title;
	}
	
	/**
	 * gets the list of items to display to the given player
	 * @param player the player to get the list of items for
	 * @return the list of items to display in the menu for the player
	 */
	protected abstract List<? extends MenuItem> getItems(Player player);

	/**
	 * sets the current row, item list and filtertext to their default values
	 */
	@Override
	protected Map<String, Object> getInitialProperties(final Player player){
		final List<? extends MenuItem> items = getItems(player);
		final Map<String, Object> properties = new HashMap<>();
		properties.put(ScrollableMenu.PROPERTY_KEY_ITEM_LIST, items);
		properties.put(ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, 0);
		properties.put(ScrollableMenu.PROPERTY_KEY_FILTERED_LIST, items);
		properties.put(ScrollableMenu.PROPERTY_KEY_FILTER_TEXT, "");
		return properties;
	}
	
	/**
	 * creates an inventory at the current row filtered with the currently set filtertext
	 */
	@Override
	protected Inventory getInventory(final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 54, title);
		
		final List<? extends MenuItem> playerItems = getFilteredPlayerItems(player);
		
		final int currentRow = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, Integer.class).orElse(0);
		final int rowCount = (playerItems.size() + 6) / 7;
		
		// set the scroll up and down items
		if(currentRow != 0) {
			inventory.setItem(8, ItemUtils.setNameAndLore(MHFHead.ARROW_UP.item(), "move up", "§7double click to move up " + Math.min(currentRow, 6), "§7right click to move to the top"));
		}
		if(rowCount > currentRow + 6) {
			inventory.setItem(53, ItemUtils.setNameAndLore(MHFHead.ARROW_DOWN.item(), "move down", "§7double click to move down " + Math.min(rowCount - currentRow - 6, 6), "§7right click to move to the bottom"));
		}
		
		// set the filter and filter item
		if(super.isTextRequestingAllowed()) {
			final Optional<String> optionalFilter = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_FILTER_TEXT, String.class);
			String filterText;
			if(optionalFilter.isPresent() && !(filterText = optionalFilter.get()).equals("")) {
				inventory.setItem(26, ItemUtils.newEnchantedItem(Material.HOPPER, "text filter", "§7the current filter is: §8" + filterText, "§7click to remove the filter", "§7right click to set a new filter"));
			} else {
				inventory.setItem(26, ItemUtils.newItem(Material.HOPPER, "text filter", "§7click to set the filter", "§7the filter must be typed into the chat"));
			}
		}

		// set the return to previous menu item
		if(super.hasPrevious(player)) {
			inventory.setItem(35, ItemUtils.newItem(Material.OAK_DOOR, "go back", "§7return to the previous menu"));
		}
		
		// set all the menu items
		final int startIndex = currentRow * 7;
		final int max = Math.min(42, playerItems.size() - startIndex);
		
		for(int i = 0; i < max; i++) {
			inventory.setItem(i + (i / 7) * 2, playerItems.get(startIndex + i).getItem(player));
		}
		
		return inventory;
	}
	
	/**
	 * handles clicks to the scrollable menus default items or gives the event to the clicked menu item
	 */
	@Override
	protected void onClick(final ClickEvent event) {
		final Player player = event.getPlayer();
		switch(event.getSlot()) {
		case 8: // move up
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				// the first click of a double click already moved up by 1, so we only need 5 more to move up a total of 6
				alterCurrentRow(player, event.isDoubleClick() ? -5 : -1);
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				super.setProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, 0);
				super.redraw(player);
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		case 26: // filter
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				final Optional<String> optionalFilter = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_FILTER_TEXT, String.class);
				if(optionalFilter.isPresent() && !(optionalFilter.get().equals(""))) {
					removeFilter(player);
					super.redraw(player);
				} else {
					super.requestText(player, ScrollableMenu.TEXT_REQUEST_KEY_FILTER_TEXT, "type the filter in the chat");
				}
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				super.requestText(player, ScrollableMenu.TEXT_REQUEST_KEY_FILTER_TEXT, "type the filter in the chat");
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		case 35: // back
			// the back method checks if a previous menu is present just like the hasPrevious method
			// so an additional check here is redundant
			back(player);
			break;
		case 53: // move down
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				// the first click of a double click already moved down by 1, so we only need 5 more to move down a total of 6
				alterCurrentRow(player, event.isDoubleClick() ? 5 : 1);
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				super.setProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, Math.max(getRowCount(player) - 6, 0));
				super.redraw(player);
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		default:
			// there are no MenuItems in the last two columns
			// all cases of special items came before this
			// so if the click was not in the first seven columns there is nothing to do here
			if(event.getColumn() > 6) {
				break;
			}
			
			final int currentRow = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, Integer.class).orElse(0);
			final int index = (event.getRow() + currentRow) * 7 + event.getColumn();
			
			final List<? extends MenuItem> playerItems = getFilteredPlayerItems(player);
			
			// some of the last slots may be empty
			// if one of those was clicked there is nothing to do here
			if(playerItems.size() <= index) {
				break;
			}
			
			final MenuItemClickEvent itemEvent = new MenuItemClickEvent(player, event.getClickType(), event.isDoubleClick());
			playerItems.get(index).onClick(itemEvent);
			if(itemEvent.getRedraw()) {
				super.redraw(player);
			}
			break;
		}
	}
	
	/**
	 * filters the list of items with the filter text given by the player
	 */
	@Override
	protected void onTextReceive(final TextEvent event) {
		switch(event.getKey()) {
		case ScrollableMenu.TEXT_REQUEST_KEY_FILTER_TEXT:
			final String filter = event.getText();
			if(filter.isEmpty()) {
				removeFilter(event.getPlayer());
			} else {
				setFilter(event.getPlayer(), filter);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * returns the list of items for a player
	 * @param player the player to get the list of items for
	 * @return the list of items that was generated for the player
	 */
	private List<? extends MenuItem> getPlayerItems(final Player player){
		List<? extends MenuItem> playerItems;
		// there is no way to do an instanceof for a List<MenuItem> because generic types are erased at runtime
		// so the only option left here is a try/catch
		// this however should never fail unless someone really tried to break the class by extending it
		try {
			@SuppressWarnings("rawtypes")
			final Optional<List> optionalList = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_ITEM_LIST, List.class);
			@SuppressWarnings("unchecked")
			final List<? extends MenuItem> list = optionalList.orElseGet(() -> new ArrayList<>());
			playerItems = list;
		} catch(final ClassCastException ex) {
			playerItems = new ArrayList<>();
		}
		
		return playerItems;
	}
	
	/**
	 * returns the current list of items the player should see, 
	 * this list only contains items which match the current filter, if one is set
	 * @param player the player to get the filtered list of items for
	 * @return the list of items that was generated for the player, filtered by the filter, if any was set
	 */
	private List<? extends MenuItem> getFilteredPlayerItems(final Player player){
		List<? extends MenuItem> playerItems;
		// there is no way to do an instanceof for a List<MenuItem> because generic types are erased at runtime
		// so the only option left here is a try/catch
		// this however should never fail unless someone really tried to break the class by extending it
		try {
			@SuppressWarnings("rawtypes")
			final Optional<List> optionalList = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_FILTERED_LIST, List.class);
			@SuppressWarnings("unchecked")
			final List<? extends MenuItem> list = optionalList.orElseGet(() -> new ArrayList<>());
			playerItems = list;
		} catch(final ClassCastException ex) {
			playerItems = new ArrayList<>();
		}
		
		return playerItems;
	}
	
	/**
	 * returns the total count of rows of the current items the player should see
	 * @param player the player to get the row count for
	 * @return the amount of rows
	 */
	private int getRowCount(final Player player) {
		return (getFilteredPlayerItems(player).size() + 6) / 7;
	}
	
	/**
	 * alters the current first row by the given offset, while staying within the range of possible rows
	 * @param player the player to alter the current row for
	 * @param offset the offset to alter the current row by
	 */
	private void alterCurrentRow(final Player player, final int offset) {
		final int currentRow = super.getProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, Integer.class).orElseGet(() -> 0);
		int newRow = currentRow + offset;
		if(offset < 0) {
			newRow = Math.max(newRow, 0);
		}
		if(offset > 0) {
			// if there are less than 6 rows rowCount - 6 will be negative, hence the Math.max(X, 0)
			newRow = Math.min(newRow, Math.max(getRowCount(player) - 6, 0));
		}
		
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, newRow);
		super.redraw(player);
	}
	
	/**
	 * sets the given filter for the given player
	 * @param player the player to set the filter for
	 * @param filter the filter to set
	 */
	private void setFilter(final Player player, final String filter) {
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, 0);
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_FILTER_TEXT, filter);
		// we need to access the filtered list at several points
		// so performance wise it is better to set it as property instead of calculating it every time
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_FILTERED_LIST, getPlayerItems(player).stream().filter(item -> item.filter(filter)).collect(Collectors.toList()));
	}
	
	/**
	 * removes the filter set for the given player
	 * @param player the player to remove the filter for
	 */
	private void removeFilter(final Player player) {
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_CURRENT_ROW, 0);
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_FILTER_TEXT, "");
		super.setProperty(player, ScrollableMenu.PROPERTY_KEY_FILTERED_LIST, getPlayerItems(player));
	}

}
