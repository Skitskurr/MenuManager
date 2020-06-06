package com.skitskurr.menumanager.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public enum MHFHead {
	BLAZE("MHF_Blaze"),
	CHICKEN("MHF_Chicken"),
	COW("MHF_Cow"),
	ENDERMAN("MHF_Enderman"),
	GHAST("MHF_Ghast"),
	HEROBRINE("MHF_Herobrine"),
	LAVA_SLIME("MHF_LavaSlime"),
	MUSHROOOM_COW("MHF_MushroomCow"),
	PIG_ZOMBIE("MHF_PigZombie"),
	SHEEP("MHF_Sheep"),
	SLIME("MHF_Slime"),
	SPIDER("MHF_Spider"),
	CAVE_SPIDER("MHF_CaveSpider"),
	SQUID("MHF_Squid"),
	VILLAGER("MHF_Villager"),
	GOLEM("MHF_Golem"),
	OCELOT("MHF_Ocelot"),
	PIG("MHF_Pig"),
	WITHER("MHF_Wither"),
	
	CACTUS("MHF_Cactus"),
	CHEST("MHF_Chest"),
	MELON("MHF_Melon"),
	OAK_LOG("MHF_OakLog"),
	PUMPKIN("MHF_Pumpkin"),
	TNT("MHF_TNT"),
	TNT2("MHF_TNT2"),
	CAKE("MHF_Cake"),
	APPLE("MHF_Apple"),
	
	ARROW_DOWN("MHF_ArrowDown"),
	ARROW_UP("MHF_ArrowUp"),
	ARROW_LEFT("MHF_ArrowLeft"),
	ARROW_RIGHT("MHF_ArrowRight"),
	EXCLAMATION("MHF_Exclamation"),
	QUESTION("MHF_Question");
	
	private final String name;
	
	private MHFHead(final String name) {
		this.name = name;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack item() {
		final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		// setOwningPlayer(uuid) only works for players who have joined your server at least once
		// setOwner(name) works for any player
		meta.setOwner(this.name);
		item.setItemMeta(meta);
		
		return item;
	}
}
