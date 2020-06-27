package com.skitskurr.menumanager.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	public static ItemStack newItem(final Material type, final String name, final String... lore) {
		return setNameAndLore(new ItemStack(type), name, lore);
	}
	
	public static ItemStack newEnchantedItem(final Material type, final String name, final String... lore) {
		return enchantAndSetNameAndLore(new ItemStack(type), name, lore);
	}

	public static ItemStack setName(final ItemStack item, final String name){
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack setLore(final ItemStack item, final List<String> lore) {
		final ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack setLore(final ItemStack item, final String... lore) {
		return setLore(item, Arrays.asList(lore));
	}

	public static ItemStack setNameAndLore(final ItemStack item, final String name, final String... lore){
		return setNameAndLore(item, name, Arrays.asList(lore));
	}
	
	public static ItemStack setNameAndLore(final ItemStack item, final String name, final List<String> lore) {
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack enchant(final ItemStack item) {
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack enchantAndSetLore(final ItemStack item, final List<String> lore) {
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack enchantAndSetLore(final ItemStack item, final String... lore) {
		return enchantAndSetLore(item, Arrays.asList(lore));
	}

	public static ItemStack enchantAndSetNameAndLore(final ItemStack item, final String name, final String... lore){
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static String getItemName(final Material type) {
		return CraftItemStack.asNMSCopy(new ItemStack(type)).getName().toString();
	}
	
}
