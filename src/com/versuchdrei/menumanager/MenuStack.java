package com.versuchdrei.menumanager;

import java.util.Optional;

import org.bukkit.entity.Player;

/**
 * a stack for all the previous menus a player has opened and their properties 
 * @author VersuchDrei
 * @version 1.0
 */
public class MenuStack {
	
	/**
	 * a node holding one menu, its properties and the previous node
	 * @author Skitskurr
	 * @version 1.0
	 */
	private static class Node{
		private final Menu menu;
		private final MenuProperties properties;
		private final Node previous;
		
		/**
		 * constructor for a node
		 * @param menu the menu to save in the node
		 * @param properties the respective properties of the node
		 * @param previous the previous node
		 */
		private Node(final Menu menu, final MenuProperties properties, final Node previous) {
			this.menu = menu;
			this.properties = properties;
			this.previous = previous;
		}
	}
	
	private boolean closePersistent = false;
	private long lastTick = 0;
	private Node current = null;
	
	/**
	 * checks if the new click was only one tick after the previous and saves the new as the last
	 * @param newTick the tick of the new click
	 * @return true if the current and previous click where only two ticks apart, otherwise false
	 */
	boolean checkDoubleClick(final long newTick, final int ticksForDoubleClick) {
		final long previousTick = lastTick;
		
		return (lastTick = newTick) <= previousTick + ticksForDoubleClick;
	}
	
	/**
	 * sets if the stack persists inventory closing
	 * @param closePersistent if the stack is close persistent
	 */
	void setClosePersistent(final boolean closePersistent) {
		this.closePersistent = closePersistent;
	}
	
	/**
	 * returns if the stack persists inventory closing
	 * @return true if the stack is close persistent, otherwise false
	 */
	boolean isClosePersistent() {
		return this.closePersistent;
	}
	
	/**
	 * returns true if, and only if, there is no menu on the stack
	 * @return true if there is no menu on the stack, otherwise false
	 */
	public boolean isEmpty() {
		return this.current == null;
	}
	
	/**
	 * returns true if there are at least two menus on the stack
	 * @return true if there are at least two menus on the stack, otherwise false
	 */
	public boolean hasPrevious() {
		return !(isEmpty() || this.current.previous == null);
	}
	
	/**
	 * adds the given menu with the given properties on top of the stack
	 * @param menu the menu to add to the stack
	 * @param properties the given menus respective properties
	 */
	public void push(final Menu menu, final MenuProperties properties) {
		this.current = new Node(menu, properties, current);
	}
	
	/**
	 * removes the uppermost menu of the stack
	 */
	public void pop() {
		if(isEmpty()) {
			return;
		}
		this.current = this.current.previous;
	}
	
	/**
	 * gets the menu currently on top of the stack
	 * @return an {@link Optional} describing the property
	 */
	public Optional<Menu> getCurrentMenu(){
		if(isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(this.current.menu);
	}
	
	/**
	 * gets the properties object for the menu currently on top of the stack
	 * @return an {@link Optional} describing the properties
	 */
	public Optional<MenuProperties> getCurrentProperties(){
		if(isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(this.current.properties);
	}
	
	/**
	 * calls the onClose method on all menus on the stack and empties it
	 * @param player the player who opened the menus
	 */
	public void closeAll(final Player player) {
		while(this.current != null) {
			current.menu.onClose(player);
			pop();
		}
	}

}
