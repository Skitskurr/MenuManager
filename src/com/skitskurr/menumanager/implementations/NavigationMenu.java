package com.skitskurr.menumanager.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.skitskurr.menumanager.ClickEvent;
import com.skitskurr.menumanager.Menu;
import com.skitskurr.menumanager.utils.ItemUtils;
import com.skitskurr.menumanager.utils.MHFHead;

public abstract class NavigationMenu extends Menu{
	
	private static class Coordinates{
		private final int x;
		private final int y;
		
		private Coordinates(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(final Object object) {
			if(object == null) {
				return false;
			}
			if(object.getClass() != getClass()) {
				return false;
			}
			final Coordinates coordinates = (Coordinates) object;
			return this.x == coordinates.x && this.y == coordinates.y;
		}
		
		@Override
		public int hashCode() {
			return 31 * x + y;
		}
	}
	
	private static final String PROPERTY_KEY_ITEM_MAP = "itemMap";
	private static final String PROPERTY_KEY_MIN_X = "minX";
	private static final String PROPERTY_KEY_MAX_X = "maxX";
	private static final String PROPERTY_KEY_CURRENT_X = "currentX";
	private static final String PROPERTY_KEY_MIN_Y = "minY";
	private static final String PROPERTY_KEY_MAX_Y = "maxY";
	private static final String PROPERTY_KEY_CURRENT_Y = "currentY";
	
	private final String title;
	
	public NavigationMenu(final String title) {
		this.title = title;
	}
	
	/**
	 * gets the lowest X coordinate the player is allowed to navigate to
	 * @param player the player to get the coordinate for
	 * @return the minimum X coordinate for the players menu
	 */
	protected abstract int getMinX(Player player);
	
	/**
	 * gets the highest X coordinate the player is allowed to navigate to
	 * @param player the player to get the coordinate for
	 * @return the maximum X coordinate for the players menu
	 */
	protected abstract int getMaxX(Player player);
	
	/**
	 * gets the X corrdinate the map is initially centered around,
	 * note that this will be the fourth column
	 * @param player
	 * @return
	 */
	protected abstract int getStartX(Player player);
	
	/**
	 * gets the lowest Y coordinate the player is allowed to navigate to
	 * @param player the player to get the coordinate for
	 * @return the minimum Y coordinate for the players menu
	 */
	protected abstract int getMinY(Player player);
	
	/**
	 * gets the highest Y coordinate the player is allowed to navigate to
	 * @param player the player to get the coordinate for
	 * @return the maximum Y coordinate for the players menu
	 */
	protected abstract int getMaxY(Player player);
	
	/**
	 * gets the Y coordinate the map is initially centered around,
	 * note that this will be the third column
	 * @param player
	 * @return
	 */
	protected abstract int getStartY(Player player);
	
	/**
	 * gets the item to display at the given coordinates for the given player
	 * @param player the player to display the item to
	 * @param x the X coordinate of the item
	 * @param y the Y coordinate of the item
	 * @return the item to display in the players menu
	 */
	protected abstract MenuItem getItem(Player player, int x, int y);
	
	/**
	 * sets minX, maxX, minY, maxY and item map and the currentX and currentY to their default values
	 */
	@Override
	protected Map<String, Object> getInitialProperties(final Player player){
		final Map<Coordinates, MenuItem> items = new HashMap<>();
		final Map<String, Object> properties = new HashMap<>();
		properties.put(NavigationMenu.PROPERTY_KEY_ITEM_MAP, items);
		
		int minX = getMinX(player);
		int maxX = getMaxX(player);
		int currentX = getStartX(player) - 3;
		if(minX > maxX) {
			final int temp = minX;
			minX = maxX;
			maxX = temp;
		}
	    currentX = Math.min(currentX, maxX - 6);
	    currentX = Math.max(currentX, minX);
		properties.put(NavigationMenu.PROPERTY_KEY_MIN_X, minX);
		properties.put(NavigationMenu.PROPERTY_KEY_MAX_X, maxX);
		properties.put(NavigationMenu.PROPERTY_KEY_CURRENT_X, currentX);
		
		int minY = getMinY(player);
		int maxY = getMaxY(player);
		int currentY = getStartY(player) + 2;
		if(minY > maxY) {
			final int temp = minY;
			minY = maxY;
			maxY = temp;
		}
	    currentY = Math.max(currentY, minY + 6);
	    currentY = Math.min(currentY, maxY);
		properties.put(NavigationMenu.PROPERTY_KEY_MIN_Y, minY);
		properties.put(NavigationMenu.PROPERTY_KEY_MAX_Y, maxY);
		properties.put(NavigationMenu.PROPERTY_KEY_CURRENT_Y, currentY);
		
		return properties;
	}

	/**
	 * creates the inventory at the current coordiantes
	 */
	@Override
	protected Inventory getInventory(final Player player) {
		final Inventory inventory = Bukkit.createInventory(null, 54, title);
		
		// set the return to previous menu item
		if(super.hasPrevious(player)) {
			inventory.setItem(53, ItemUtils.newItem(Material.OAK_DOOR, "go back", "§7return to the previous menu"));
		}

		final Optional<Integer> optionalMinX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_X, Integer.class);
		final Optional<Integer> optionalMaxX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_X, Integer.class);
		final Optional<Integer> optionalCurrentX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_X, Integer.class);
		final Optional<Integer> optionalMinY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_Y, Integer.class);
		final Optional<Integer> optionalMaxY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_Y, Integer.class);
		final Optional<Integer> optionalCurrentY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_Y, Integer.class);
		
		if(!(optionalMinX.isPresent() && optionalMaxX.isPresent() && optionalCurrentX.isPresent()
				&& optionalMinY.isPresent() && optionalMaxY.isPresent() && optionalCurrentY.isPresent())) {
			return inventory;
		}
		
		final Map<Coordinates, MenuItem> playerItems = getPlayerItems(player);
		
		final int minX = optionalMinX.get();
		final int maxX = optionalMaxX.get();
		final int currentX = optionalCurrentX.get();
		final int minY = optionalMinY.get();
		final int maxY = optionalMaxY.get();
		final int currentY = optionalCurrentY.get();
		
		// set the navigation items
		if(currentX != minX) {
			final List<String> lore = new ArrayList<String>();
			lore.add("§7double click to move left " + Math.min(currentX - minX, 6));
			if(minX != Integer.MIN_VALUE) {
				lore.add("§7right click to move to the left border");
			}
			inventory.setItem(15, ItemUtils.setNameAndLore(MHFHead.ARROW_LEFT.item(), "move left", lore));
		}
		if(maxX > currentX + 5) {
			final List<String> lore = new ArrayList<String>();
			lore.add("§7double click to move right " + Math.min(maxX - currentX - 5, 6));
			if(maxX != Integer.MAX_VALUE) {
				lore.add("§7right click to move to the right border");
			}
			inventory.setItem(17, ItemUtils.setNameAndLore(MHFHead.ARROW_RIGHT.item(), "move right", lore));
		}
		if(currentY != maxY) {
			final List<String> lore = new ArrayList<String>();
			lore.add("§7double click to move up " + Math.min(maxY - currentY, 6));
			if(maxY != Integer.MAX_VALUE) {
				lore.add("§7right click to move to the top");
			}
			inventory.setItem(7, ItemUtils.setNameAndLore(MHFHead.ARROW_UP.item(), "move up", lore));
		}
		if(minY < currentY - 5) {
			final List<String> lore = new ArrayList<String>();
			lore.add("§7double click to move down " + Math.min(currentY - minY - 5, 6));
			if(minY != Integer.MIN_VALUE) {
				lore.add("§7right click to move to the bottom");
			}
			inventory.setItem(25, ItemUtils.setNameAndLore(MHFHead.ARROW_DOWN.item(), "move down", lore));
		}
		
		final int maxColumn = Math.min(maxX - minX + 1, 6);
		final int maxRow = Math.min(maxY - minY + 1, 6);
		
		for(int x = 0; x < maxColumn; x++) {
			for(int y = 0; y < maxRow; y++) {
				final Coordinates coordinates = new Coordinates(currentX + x, currentY - y);
				if(!playerItems.containsKey(coordinates)) {
					playerItems.put(coordinates, getItem(player, coordinates.x, coordinates.y));
				}
				inventory.setItem(x + y * 9, playerItems.get(coordinates).getItem(player));
			}
		}
		
		return inventory;
	}
	
	@Override
	protected void onClick(final ClickEvent event) {
		final Player player = event.getPlayer();
		switch(event.getSlot()) {
		case 7: // move up
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				// the first click of a double click already moved up by 1, so we only need 5 more to move up a total of 6
				alterCurrentY(player, event.isDoubleClick() ? 5 : 1);
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				final Optional<Integer> optionalMaxY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_Y, Integer.class);
				int maxY;
				if(!optionalMaxY.isPresent() || (maxY = optionalMaxY.get()) == Integer.MAX_VALUE) {
					break;
				}
				super.setProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_Y, maxY);
				super.redraw(player);
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		case 15: // move left
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				// the first click of a double click already moved up by 1, so we only need 5 more to move up a total of 6
				alterCurrentX(player, event.isDoubleClick() ? -5 : -1);
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				final Optional<Integer> optionalMinX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_X, Integer.class);
				int minX;
				if(!optionalMinX.isPresent() || (minX = optionalMinX.get()) == Integer.MIN_VALUE) {
					break;
				}
				super.setProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_X, minX);
				super.redraw(player);
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		case 17: // move right
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				// the first click of a double click already moved down by 1, so we only need 5 more to move down a total of 6
				alterCurrentX(player, event.isDoubleClick() ? 5 : 1);
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				final Optional<Integer> optionalMinX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_X, Integer.class);
				final Optional<Integer> optionalMaxX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_X, Integer.class);
				final int minX;
				int maxX;
				if(!optionalMinX.isPresent() || !optionalMaxX.isPresent()
						|| (minX = optionalMinX.get()) == Integer.MIN_VALUE|| (maxX = optionalMaxX.get()) == Integer.MAX_VALUE) {
					break;
				}
				super.setProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_X, Math.max(maxX - 5, minX));
				super.redraw(player);
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		case 25: // move down
			switch(event.getClickType()) {
			case LEFT:
			case SHIFT_LEFT:
				// the first click of a double click already moved down by 1, so we only need 5 more to move down a total of 6
				alterCurrentY(player, event.isDoubleClick() ? -5 : -1);
				break;
			case RIGHT:
			case SHIFT_RIGHT:
				final Optional<Integer> optionalMinY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_Y, Integer.class);
				final Optional<Integer> optionalMaxY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_Y, Integer.class);
				int minY;
				int maxY;
				if(!optionalMinY.isPresent() || !optionalMaxY.isPresent()
						|| (minY = optionalMinY.get()) == Integer.MIN_VALUE || (maxY = optionalMaxY.get()) == Integer.MAX_VALUE) {
					break;
				}
				super.setProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_Y, Math.min(minY + 5, maxY));
				super.redraw(player);
				break;
			default:
				// nothing to do here
				break;
			}
			break;
		case 53: // back
			// the back method checks if a previous menu is present just like the hasPrevious method
			// so an additional check here is redundant
			back(player);
			break;
		default:
			// there are no MenuItems in the last three columns
			// all cases of special items came before this
			// so if the click was not in the first six columns there is nothing to do here
			if(event.getColumn() > 5) {
				break;
			}
			
			final Optional<Integer> optionalCurrentX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_X, Integer.class);
			final Optional<Integer> optionalCurrentY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_Y, Integer.class);
			
			// this should never happen, so we do a safety abort here
			if(!(optionalCurrentX.isPresent() && optionalCurrentY.isPresent())) {
				break;
			}
			
			final Map<Coordinates, MenuItem> playerItems = getPlayerItems(player);
			
			final MenuItemClickEvent itemEvent = new MenuItemClickEvent(player, event.getClickType(), event.isDoubleClick());
			playerItems.get(new Coordinates(optionalCurrentX.get() + event.getColumn(), optionalCurrentY.get() - event.getRow())).onClick(itemEvent);
			if(itemEvent.getRedraw()) {
				super.redraw(player);
			}
			break;
		}
	}
	
	/**
	 * returns the map of items for the player
	 * @param player the player to get the map of items for
	 * @return the map of items that was generated for the player
	 */
	private Map<Coordinates, MenuItem> getPlayerItems(final Player player){
		Map<Coordinates, MenuItem> playerItems;
		// there is no way to do an instanceof for a Map<Coordinates, MenuItem> because generic types are erased at runtime
		// so the only option left here is a try/catch
		// this however should never fail unless someone really tried to break the class by extending it
		try {
			@SuppressWarnings("rawtypes")
			final Optional<Map> optionalMap = super.getProperty(player, NavigationMenu.PROPERTY_KEY_ITEM_MAP, Map.class);
			@SuppressWarnings("unchecked")
			final Map<Coordinates, MenuItem> map = optionalMap.orElseGet(() -> new HashMap<>());
			playerItems = map;
		} catch (final ClassCastException ex) {
			playerItems = new HashMap<>();
		}
		return playerItems;
	}
	
	/**
	 * alters currentX by the given offset, while staying within the range of minX and maxX
	 * @param player the player to alter currentX for
	 * @param offset the offset to alter currentX by
	 */
	public void alterCurrentX(final Player player, final int offset) {
		final Optional<Integer> optionalMinX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_X, Integer.class);
		final Optional<Integer> optionalMaxX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_X, Integer.class);
		final Optional<Integer> optionalCurrentX = super.getProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_X, Integer.class);
		
		if(!(optionalMinX.isPresent() && optionalMaxX.isPresent() && optionalCurrentX.isPresent())) {
			return;
		}
		
		final int minX = optionalMinX.get();
		final int maxX = optionalMaxX.get();
		final int currentX = optionalCurrentX.get();
		
		int newX = currentX + offset;
		if(offset < 0) {
			newX = Math.max(newX, minX);
		}
		if(offset > 0) {
			// if there are less than 6 columns maxX - 5 will be less than minX, hence the Math.max
			newX = Math.min(newX, Math.max(maxX - 5, minX));
		}
		
		super.setProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_X, newX);
		super.redraw(player);
	}
	
	/**
	 * alters currentY by the given offset, while staying within the range of minY and maxY
	 * @param player the player to alter currentY for
	 * @param offset the offset to alter currentY by
	 */
	public void alterCurrentY(final Player player, final int offset) {
		final Optional<Integer> optionalMinY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MIN_Y, Integer.class);
		final Optional<Integer> optionalMaxY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_MAX_Y, Integer.class);
		final Optional<Integer> optionalCurrentY = super.getProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_Y, Integer.class);
		
		if(!(optionalMinY.isPresent() && optionalMaxY.isPresent() && optionalCurrentY.isPresent())) {
			return;
		}
		
		final int minY = optionalMinY.get();
		final int maxY = optionalMaxY.get();
		final int currentY = optionalCurrentY.get();
		
		int newY = currentY + offset;
		if(offset < 0) {
			// if there are less than 6 rows minY + 5 will be more than maxY, hence the Math.min
			newY = Math.max(newY, Math.min(minY + 5, maxY));
		}
		if(offset > 0) {
			newY = Math.min(newY, maxY);
		}
		
		super.setProperty(player, NavigationMenu.PROPERTY_KEY_CURRENT_Y, newY);
		super.redraw(player);
	}

}
