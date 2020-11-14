package com.versuchdrei.menumanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * holder class for properties managed by the plugin
 * @author VersuchDrei
 * @version 1.0
 */
public class MenuProperties {
	
	private final Map<String, Object> properties = new HashMap<>();
	
	/**
	 * constructor for an empty set of properties
	 */
	public MenuProperties() {
	}
	
	/**
	 * constructor with the given properties already set
	 * @param initialProperties a map of properties to set
	 */
	public MenuProperties(final Map<String, Object> initialProperties) {
		// HashMap::putAll uses the same internal code as HashMap::copy, but doesn't require casting from an object
		this.properties.putAll(initialProperties);
	}
	
	/**
	 * checks if a property of the given key is present
	 * @param key the key to identify the property
	 * @return true if the property present, otherwise false
	 */
	public boolean hasProperty(final String key) {
		return this.properties.containsKey(key);
	}
	
	/**
	 * checks if a property of the given key and is present with a value of the given class
	 * @param key the key to identify the property
	 * @param type the desired class of the property
	 * @return true if the property is present and of the given class, otherwise false
	 */
	public boolean hasProperty(final String key, final Class<?> type) {
		return this.properties.containsKey(key) && type.isInstance(this.properties.get(key));
	}
	
	/**
	 * sets the property of the given key to the given value
	 * @param key the key to identify the property
	 * @param value the value to set the property to
	 */
	public void set(final String key, final Object value) {
		this.properties.put(key, value);
	}
	
	/**
	 * gets the property of the given key cast into the given class
	 * @param <T> the desired return type
	 * @param key the key to identify the property
	 * @param type the class of the return type
	 * @return an {@link Optional} describing the value of the property
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(final String key, final Class<T> type){
		final Object value = properties.get(key);
		
		if(type.isInstance(value)) {
			return Optional.of((T) value);
		}
		
		return Optional.empty();
	}

}
