package tux2.MonsterBox;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class MonsterBoxBlockListener implements Listener {
	
	MonsterBox plugin;
	public ConcurrentHashMap<Integer, String> intmobs = new ConcurrentHashMap<Integer, String>();
	public ConcurrentHashMap<String, Integer> stringmobs = new ConcurrentHashMap<String, Integer>();
	
	public MonsterBoxBlockListener(MonsterBox plugin) {
		this.plugin = plugin;
		CreatureTypes[] mobs = CreatureTypes.values();
		for(CreatureTypes mob : mobs) {
			intmobs.put(new Integer(mob.id), mob.toString());
			stringmobs.put(mob.toString(), new Integer(mob.id));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().getItemInHand();
		if(!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {
			ItemStack is = event.getPlayer().getItemInHand();
			//Check to see if the tool needs to be enchanted.
			if(plugin.needssilktouch && !itemHasSilkTouch(is)) {
				return;
			}
			try {
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlock().getState();
				String monster = theSpawner.getCreatureTypeName();
				if(plugin.hasPermissions(event.getPlayer(), "monsterbox.drops") || plugin.hasPermissions(event.getPlayer(), "monsterbox.dropegg")) {
					event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
				}
				if(plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")) {
					ItemStack mobstack = new ItemStack(Material.MOB_SPAWNER, 1);
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), mobstack);
				}
				if(stringmobs.containsKey(monster) && plugin.hasPermissions(event.getPlayer(), "monsterbox.dropegg." + monster.toLowerCase())) {
					ItemStack eggstack = new ItemStack(383, 1, stringmobs.get(monster).shortValue());
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), eggstack);
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
}
