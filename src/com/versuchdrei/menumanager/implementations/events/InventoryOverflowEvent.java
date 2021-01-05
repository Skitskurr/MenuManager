package com.versuchdrei.menumanager.implementations.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryOverflowEvent extends PlayerEvent implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();

	private List<ItemStack> items;
	private boolean cancelled = false;
	
	public InventoryOverflowEvent(final Player player, final List<ItemStack> items) {
		super(player);
		
		this.items = items;
	}
	
	public List<ItemStack> getItems(){
		return this.items;
	}
	
	public void setItems(final List<ItemStack> items) {
		this.items = items;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(final boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return InventoryOverflowEvent.handlers;
	}
	
	public static HandlerList getHandlerList() {
		return InventoryOverflowEvent.handlers;
	}

}
