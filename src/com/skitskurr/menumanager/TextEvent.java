package com.skitskurr.menumanager;

import org.bukkit.entity.Player;

/**
 * a wrapper class for parameters of a text event
 * @author Skitskurr
 * @version 1.0
 */
public class TextEvent {
	
	private final Player player;
	private final String key;
	private final String text;
	
	/**
	 * constructor for a text event
	 * @param player the player the request was sent to
	 * @param key the key to identify the request
	 * @param text the text that was answered
	 */
	TextEvent(final Player player, final String key, final String text){
		this.player = player;
		this.key = key;
		this.text = text;
	}
	
	/**
	 * returns the player the request was sent to
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * returns the key to identify the request
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * returns the text that was answered
	 * @return the answered text
	 */
	public String getText() {
		return this.text;
	}

}
