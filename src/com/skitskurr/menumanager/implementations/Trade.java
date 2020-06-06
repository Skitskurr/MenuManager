package com.skitskurr.menumanager.implementations;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.skitskurr.menumanager.ClickEvent;
import com.skitskurr.menumanager.Menu;
import com.skitskurr.menumanager.utils.ItemUtils;
import com.skitskurr.menumanager.utils.PlayerUtils;

public class Trade {
	
	private static enum TradeStatus{
		OPEN,
		LOCKED,
		ACCEPTED,
		CANCELLED
	}
	
	private static final int[] IRON_BARS_SLOTS = new int[] {4, 13, 22, 31, 40};
	private static final int[] BLOCK_SLOTS_FIRST = new int[] {46, 47, 48};
	private static final int[] BLOCK_SLOTS_SECOND = new int[] {50, 51, 52};
	private static final int HEAD_SLOT_FIRST = 45;
	private static final int HEAD_SLOT_SECOND = 53;
	private static final int CURRENCIES_SLOT = 49;
	
	private class TradeMenu extends Menu{
		
		private final boolean isPlayer1;
		
		private TradeMenu(final boolean isPlayer1) {
			this.isPlayer1 = isPlayer1;
		}
		
		@Override
		protected Inventory getInventory(final Player player) {
			final Inventory inventory = Bukkit.createInventory(null, 54, "Trade");
			
			// dividing line
			for(final int slot: Trade.IRON_BARS_SLOTS) {
				inventory.setItem(slot, new ItemStack(Material.IRON_BARS));
			}
			
			final Player otherPlayer = getPlayer(!this.isPlayer1);
			
			// this players trade status blocks
			for(final int slot: Trade.BLOCK_SLOTS_FIRST) {
				ItemStack item;
				switch(getStatus(this.isPlayer1)) {
				case OPEN:
					item = ItemUtils.newItem(Material.IRON_BLOCK, "lock in", "§7left click to lock in");
					break;
				case LOCKED:
					item = ItemUtils.newItem(Material.EMERALD_BLOCK, "locked in", "§7left click to accept", "§7right click to unlock");
					break;
				case ACCEPTED:
					item = ItemUtils.newEnchantedItem(Material.EMERALD_BLOCK, "accepted", "§7wait for " + otherPlayer.getName() + " to accept");
					break;
				default:
					item = ItemUtils.newItem(Material.REDSTONE_BLOCK, "error", "§7this should not have happened");
					break;
				}
				inventory.setItem(slot, item);
			}
			
			// other players trade status blocks
			for(final int slot: Trade.BLOCK_SLOTS_SECOND) {
				ItemStack item;
				switch(getStatus(!this.isPlayer1)) {
				case OPEN:
					item = ItemUtils.newItem(Material.IRON_BLOCK, "not locked in");
					break;
				case LOCKED:
					item = ItemUtils.newItem(Material.EMERALD_BLOCK, "locked in");
					break;
				case ACCEPTED:
					item = ItemUtils.newEnchantedItem(Material.EMERALD_BLOCK, "accepted");
					break;
				default:
					item = ItemUtils.newItem(Material.REDSTONE_BLOCK, "error", "§7this should not have happened");
					break;
				}
				inventory.setItem(slot, item);
			}
			
			// head for this player
			final ItemStack firstSkull = new ItemStack(Material.PLAYER_HEAD);
			final SkullMeta firstMeta = (SkullMeta) firstSkull.getItemMeta();
			firstMeta.setOwningPlayer(getPlayer(this.isPlayer1));
			firstMeta.setDisplayName("your offer");
			firstSkull.setItemMeta(firstMeta);
			inventory.setItem(Trade.HEAD_SLOT_FIRST, firstSkull);
			
			// head for the other player
			final ItemStack secondSkull = new ItemStack(Material.PLAYER_HEAD);
			final SkullMeta secondMeta = (SkullMeta) secondSkull.getItemMeta();
			secondMeta.setOwningPlayer(otherPlayer);
			secondMeta.setDisplayName(otherPlayer.getName() + "'s offer");
			secondSkull.setItemMeta(secondMeta);
			inventory.setItem(Trade.HEAD_SLOT_SECOND, secondSkull);
			
			// this players currency counter
			int emeralds = 0;
			int ironNuggets = 0;
			int redstone = 0;
			int diamonds = 0;
			
			// this players trade offer
			final List<ItemStack> items = getItems(this.isPlayer1);
			for(int i = 0; i < items.size(); i++) {
				final ItemStack item = items.get(i);
				inventory.setItem((i / 4) * 9 + i % 4, item);
				
				switch(item.getType()) {
				case EMERALD:
					emeralds += item.getAmount();
					break;
				case EMERALD_BLOCK:
					emeralds += item.getAmount() * 9;
					break;
				case IRON_NUGGET:
					ironNuggets += item.getAmount();
					break;
				case IRON_INGOT:
					ironNuggets += item.getAmount() * 9;
					break;
				case IRON_BLOCK:
					ironNuggets += item.getAmount() * 81;
					break;
				case REDSTONE:
					redstone += item.getAmount();
					break;
				case REDSTONE_BLOCK:
					redstone += item.getAmount() * 9;
					break;
				case DIAMOND:
					diamonds += item.getAmount();
					break;
				case DIAMOND_BLOCK:
					diamonds += item.getAmount() * 9;
					break;
				default:
					break;
				}
			}
			
			// other players currency counter
			int otherEmeralds = 0;
			int otherIronNuggets = 0;
			int otherRedstone = 0;
			int otherDiamonds = 0;
			
			// the other players trade offer
			final List<ItemStack> otherItems = getItems(!this.isPlayer1);
			for(int i = 0; i < otherItems.size(); i++){
				final ItemStack item = otherItems.get(i);
				inventory.setItem((i / 4) * 9 + i % 4 + 5, item);
				
				switch(item.getType()) {
				case EMERALD:
					otherEmeralds += item.getAmount();
					break;
				case EMERALD_BLOCK:
					otherEmeralds += item.getAmount() * 9;
					break;
				case IRON_NUGGET:
					otherIronNuggets += item.getAmount();
					break;
				case IRON_INGOT:
					otherIronNuggets += item.getAmount() * 9;
					break;
				case IRON_BLOCK:
					otherIronNuggets += item.getAmount() * 81;
					break;
				case REDSTONE:
					otherRedstone += item.getAmount();
					break;
				case REDSTONE_BLOCK:
					otherRedstone += item.getAmount() * 9;
					break;
				case DIAMOND:
					otherDiamonds += item.getAmount();
					break;
				case DIAMOND_BLOCK:
					otherDiamonds += item.getAmount() * 9;
					break;
				default:
					break;
				}
			}
			
			final String[] currenciesLore = new String[] {
					"§7Emeralds: " + emeralds + " : " + otherEmeralds,
					"§7Iron: " + (ironNuggets / 9) + " : " + (otherIronNuggets / 9),
					"§7Redstone: " + redstone + " : " + otherRedstone,
					"§7Diamonds: " + diamonds + " : " + otherDiamonds
			};
			inventory.setItem(Trade.CURRENCIES_SLOT, ItemUtils.newItem(Material.EMERALD, "currencies", currenciesLore));
			
			return inventory;
		}
		
		@Override
		protected void onClick(final ClickEvent event) {
			final int eventSlot = event.getSlot();
			if(Arrays.stream(Trade.BLOCK_SLOTS_FIRST).anyMatch(slot -> slot == eventSlot)) {
				acceptClick(this.isPlayer1, event.getClickType());
				redraw();
				return;
			}
			
			final Player player = event.getPlayer();
			if(getStatus(this.isPlayer1) != TradeStatus.OPEN) {
				return;
			}
			
			final int column = event.getColumn();
			final int row = event.getRow();
			
			if(column > 3 || row > 4) {
				return;
			}
			
			final ItemStack item = removeItem(row * 4 + column, this.isPlayer1);
			if(item == null) {
				return;
			}
			
			PlayerUtils.addOrDropItem(player, item);
			redraw();
		}
		
		@Override
		protected void onClickBottom(final ClickEvent event) {
			final Player player = event.getPlayer();
			if(getStatus(this.isPlayer1) != TradeStatus.OPEN) {
				return;
			}
			
			final ItemStack item = event.getItem();
			if(item == null) {
				return;
			}
			
			if(!addItem(item, this.isPlayer1)) {
				return;
			}
			
			player.getInventory().setItem(event.getSlot(), null);
			redraw();
		}
		
		@Override
		protected void onClose(final Player player) {
			// to avoid recursion
			if(getStatus(this.isPlayer1) == TradeStatus.CANCELLED) {
				return;
			}
			
			cancelTrade(this.isPlayer1);
		}
		
		private void redraw() {
			super.redraw(Trade.this.player1);
			super.redraw(Trade.this.player2);
		}
	}
	
	public static void open (final Player player1, final Player player2) {
		final Trade trade = new Trade(player1, player2);
		trade.new TradeMenu(true).open(player1);
		trade.new TradeMenu(false).open(player2);
	}
	
	private final Player player1;
	private final Player player2;
	private final List<ItemStack> items1 = new LinkedList<>();
	private final List<ItemStack> items2 = new LinkedList<>();
	private TradeStatus status1 = TradeStatus.OPEN;
	private TradeStatus status2 = TradeStatus.OPEN;
	
	private Trade(final Player player1, final Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}
	
	private Player getPlayer(final boolean isPlayer1) {
		return isPlayer1 ? this.player1 : this.player2;
	}
	
	private List<ItemStack> getItems(final boolean isPlayer1){
		return isPlayer1 ? this.items1 : this.items2;
	}
	
	private TradeStatus getStatus(final boolean isPlayer1) {
		return isPlayer1 ? this.status1 : this.status2;
	}
	
	private void resetStatus() {
		if(this.status1 == TradeStatus.CANCELLED || this.status2 == TradeStatus.CANCELLED) {
			return;
		}
		this.status1 = TradeStatus.OPEN;
		this.status2 = TradeStatus.OPEN;
	}
	
	private void setStatus(final TradeStatus status, final boolean isPlayer1) {
		switch(status) {
		case OPEN:
			resetStatus();
			break;
		case LOCKED:
		case CANCELLED:
			if(isPlayer1) {
				this.status1 = status;
			} else {
				this.status2 = status;
			}
			break;
		case ACCEPTED:
			switch(getStatus(!isPlayer1)) {
			case LOCKED:
				if(isPlayer1) {
					this.status1 = status;
				} else {
					this.status2 = status;
				}
				break;
			case ACCEPTED:
				if(isPlayer1) {
					this.status1 = status;
				} else {
					this.status2 = status;
				}
				doTrade();
				break;
			case OPEN:
			case CANCELLED:
				break;
			}
			break;
		}
	}
	
	private void acceptClick(final boolean isPlayer1, final ClickType type) {
		switch(type) {
		case LEFT:
		case SHIFT_LEFT:
			switch(getStatus(isPlayer1)) {
			case OPEN:
				setStatus(TradeStatus.LOCKED, isPlayer1);
				break;
			case LOCKED:
				setStatus(TradeStatus.ACCEPTED, isPlayer1);
				break;
			default:
				break;
			}
			break;
		case RIGHT:
		case SHIFT_RIGHT:
			if(getStatus(isPlayer1) == TradeStatus.LOCKED) {
				setStatus(TradeStatus.OPEN, isPlayer1);
			}
			break;
		default:
			break;
		}
	}
	
	private boolean addItem(final ItemStack item, final boolean isPlayer1) {
		final List<ItemStack> items = isPlayer1 ? this.items1 : this.items2;
		if(items.size() == 20) {
			return false;
		}
		
		resetStatus();
		
		items.add(item);
		return true;
	}
	
	private ItemStack removeItem(final int index, final boolean isPlayer1) {
		final List<ItemStack> items = isPlayer1 ? this.items1 : this.items2;
		if(items.size() <= index) {
			return null;
		}
		
		final ItemStack item = items.get(index);
		if(item == null) {
			return null;
		}
		
		resetStatus();

		items.remove(index);
		return item;
	}
	
	private void doTrade() {
		for(final ItemStack item: this.items1) {
			PlayerUtils.addOrDropItem(this.player2, item);
		}
		this.items1.clear();
		
		for(final ItemStack item: this.items2) {
			PlayerUtils.addOrDropItem(this.player1, item);
		}
		this.items2.clear();
		
		this.status1 = TradeStatus.CANCELLED;
		this.status2 = TradeStatus.CANCELLED;
		
		this.player1.getOpenInventory().close();
		this.player2.getOpenInventory().close();
	}
	
	private void cancelTrade(final boolean isPlayer1) {
		this.status1 = TradeStatus.CANCELLED;
		this.status2 = TradeStatus.CANCELLED;
		
		for(final ItemStack item: this.items1) {
			PlayerUtils.addOrDropItem(this.player1, item);
		}
		this.items1.clear();
		
		for(final ItemStack item: this.items2) {
			PlayerUtils.addOrDropItem(this.player2, item);
		}
		this.items2.clear();
		
		if(isPlayer1) {
			this.player2.getOpenInventory().close();
			this.player2.sendMessage(this.player1.getName() + " has cancelled the trade.");
		} else {
			this.player1.getOpenInventory().close();
			this.player1.sendMessage(this.player2.getName() + " has cancelled the trade.");
		}
	}

}
