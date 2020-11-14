package com.versuchdrei.menumanager.implementations;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import com.versuchdrei.menumanager.utils.ItemUtils;

/**
 * an example implementation of a NavigationMenu which displays the players current worlds slime chunks
 * @author VersuchDrei
 * @version 1.0
 */
public class SlimeMap extends NavigationMenu{

	public SlimeMap() {
		super("Slime Map");
	}

	@Override
	protected int getMinX(final Player player) {
		final WorldBorder border = player.getWorld().getWorldBorder();
		final int minX = (int) (border.getCenter().getX() - border.getSize() / 2);
		return (minX < 0 ? minX -15 : minX) / 16;
	}

	@Override
	protected int getMaxX(final Player player) {
		final WorldBorder border = player.getWorld().getWorldBorder();
		final int maxX = (int) (border.getCenter().getX() + border.getSize() / 2);
		return (maxX < 0 ? maxX -15 : maxX) / 16;
	}

	@Override
	protected int getStartX(final Player player) {
		final int startX = player.getLocation().getBlockX();
		return (startX < 0 ? startX -15 : startX) / 16;
	}

	@Override
	protected int getMinY(final Player player) {
		final WorldBorder border = player.getWorld().getWorldBorder();
		final int minZ = (int) (border.getCenter().getZ() - border.getSize() / 2);
		return (minZ < 0 ? minZ -15 : minZ) / 16;
	}

	@Override
	protected int getMaxY(final Player player) {
		final WorldBorder border = player.getWorld().getWorldBorder();
		final int maxZ = (int) (border.getCenter().getZ() + border.getSize() / 2);
		return (maxZ < 0 ? maxZ -15 : maxZ) / 16;
	}

	@Override
	protected int getStartY(final Player player) {
		final int startZ = player.getLocation().getBlockZ();
		return (startZ < 0 ? startZ -15 : startZ) / 16;
	}

	@Override
	protected MenuItem getItem(final Player player, final int x, final int y) {
		final Chunk chunk = player.getWorld().getChunkAt(x, y);
		final Material type = chunk.isSlimeChunk() ? Material.SLIME_BLOCK : Material.STONE;
		final String name = "§fX: " + x + " Z: " + y;
		final String loreX = "§7Block X: " + x * 16 + " to " + (x * 16 + 15);
		final String loreZ = "§7Block Z: " + y * 16 + " to " + (y * 16 + 15);
		if(chunk.equals(player.getLocation().getChunk())){
			return new FixedMenuItem(ItemUtils.newEnchantedItem(type, name, loreX, loreZ, "§7you are here"));
		} else {
			return new FixedMenuItem(ItemUtils.newItem(type, name, loreX, loreZ));
		}
	}

}
