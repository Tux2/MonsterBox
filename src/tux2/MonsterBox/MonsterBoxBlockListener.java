package tux2.MonsterBox;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import Tux2.TuxTwoLib.SpawnEggs;

public class MonsterBoxBlockListener implements Listener {
	
	MonsterBox plugin;
	public ConcurrentHashMap<EntityType, String> entitymobs = new ConcurrentHashMap<EntityType, String>();
	public ConcurrentHashMap<String, EntityType> stringmobs = new ConcurrentHashMap<String, EntityType>();
	
	public MonsterBoxBlockListener(MonsterBox plugin) {
		this.plugin = plugin;
		CreatureTypes[] mobs = CreatureTypes.values();
		for(CreatureTypes mob : mobs) {
			entitymobs.put(mob.entity, mob.toString());
			stringmobs.put(mob.toString(), mob.entity);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().getInventory().getItemInMainHand();
		if(!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {
			ItemStack is = event.getPlayer().getInventory().getItemInMainHand();
			//Check to see if the tool needs to be enchanted.
			boolean nodrops = false;
			if(plugin.disabledspawnerlocs.containsKey(plugin.locationBuilder(event.getBlock().getLocation()))) {
				plugin.removeDisabledSpawner(event.getBlock());
				nodrops = true;
			}
			if(plugin.needssilktouch && !itemHasSilkTouch(is)) {
				return;
			}
			try {
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlock().getState();
				String monster = entitymobs.get(theSpawner.getSpawnedType());
				if(plugin.hasPermissions(event.getPlayer(), "monsterbox.drops") || plugin.hasPermissions(event.getPlayer(), "monsterbox.dropegg")) {
					if(nodrops) {
						event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke an " + ChatColor.RED + "unset" + ChatColor.DARK_GREEN + " spawner.");
					}else {
						event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
					}
				}
				//fix the beserk thingy that some players have been reporting.
				boolean mcmmofix = false;
				if(plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")) {
					ItemStack mobstack = new ItemStack(Material.MOB_SPAWNER, 1);
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), mobstack);
					mcmmofix = true;
				}
				if(!nodrops && stringmobs.containsKey(monster) && plugin.hasPermissions(event.getPlayer(), "monsterbox.dropegg." + monster.toLowerCase())) {
					ItemStack eggstack = SpawnEggs.getSpawnEgg(stringmobs.get(monster), 1);
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), eggstack);
					mcmmofix = true;
				}
				//If we dropped something, let's break the spawner and don't let any XP drop.
				if(mcmmofix) {
					event.getBlock().setType(Material.AIR);
					event.setExpToDrop(0);
				}
			}catch (Exception e) {
			}
		}
	}
	
	private boolean itemHasSilkTouch(ItemStack is) {
		if(is != null && is.containsEnchantment(Enchantment.SILK_TOUCH)) {
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled() && event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			if(plugin.hasPermissions(event.getPlayer(), "monsterbox.place")) {
				plugin.addDisabledSpawner(event.getBlockPlaced());
				//This code doesn't work from 1.0.1 on, so why include it...
				/*
				String type = intmobs.get(plugin.playermonsterspawner.get(event.getPlayer().getName()));
				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just placed a " + ChatColor.RED + type.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlockPlaced().getState();
		    	CreatureType ct = CreatureType.fromName(type);
		        if (ct == null) {
		            return;
		        }
		        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SetSpawner(theSpawner, ct));*/
			}else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to place a monster spawner.");
			}
		}
	}
	
	//Make sure we remove mob spawners that get exploded
	@EventHandler(priority = EventPriority.MONITOR)
	public void explosion(EntityExplodeEvent event) {
		if(event.isCancelled()) {
			return;
		}
		for(Block block : event.blockList()) {
			if(block.getType() == Material.MOB_SPAWNER) {
				if(plugin.disabledspawnerlocs.containsKey(plugin.locationBuilder(block.getLocation()))) {
					plugin.removeDisabledSpawner(block);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void mobSpawn(CreatureSpawnEvent event) {
		if(!event.isCancelled()) {
			if(event.getSpawnReason() == SpawnReason.SPAWNER) {
				if(!plugin.canSpawnMob(event.getLocation(), event.getEntityType())) {
					event.setCancelled(true);
				}
			}
		}
	}
}
