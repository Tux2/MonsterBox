package tux2.MonsterBox;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class MonsterBoxBlockListener extends BlockListener {
	
	MonsterBox plugin;
	public ConcurrentHashMap<Integer, String> intmobs = new ConcurrentHashMap<Integer, String>();
	public ConcurrentHashMap<String, Integer> stringmobs = new ConcurrentHashMap<String, Integer>();
	
	public MonsterBoxBlockListener(MonsterBox plugin) {
		this.plugin = plugin;
		intmobs.put(new Integer(0), "Pig");
		intmobs.put(new Integer(1), "Chicken");
		intmobs.put(new Integer(2), "Cow");
		intmobs.put(new Integer(3), "Sheep");
		intmobs.put(new Integer(4), "Squid");
		intmobs.put(new Integer(5), "Creeper");
		intmobs.put(new Integer(6), "Ghast");
		intmobs.put(new Integer(7), "PigZombie");
		intmobs.put(new Integer(8), "Skeleton");
		intmobs.put(new Integer(9), "Spider");
		intmobs.put(new Integer(10), "Zombie");
		intmobs.put(new Integer(11), "Slime");
		intmobs.put(new Integer(12), "Monster");
		intmobs.put(new Integer(13), "Giant");
		intmobs.put(new Integer(14), "Wolf");
		intmobs.put(new Integer(15), "CaveSpider");
		intmobs.put(new Integer(16), "Enderman");
		intmobs.put(new Integer(17), "Silverfish");
		intmobs.put(new Integer(90), "Pig");
		intmobs.put(new Integer(93), "Chicken");
		intmobs.put(new Integer(92), "Cow");
		intmobs.put(new Integer(91), "Sheep");
		intmobs.put(new Integer(94), "Squid");
		intmobs.put(new Integer(50), "Creeper");
		intmobs.put(new Integer(53), "Ghast");
		intmobs.put(new Integer(57), "PigZombie");
		intmobs.put(new Integer(51), "Skeleton");
		intmobs.put(new Integer(52), "Spider");
		intmobs.put(new Integer(54), "Zombie");
		intmobs.put(new Integer(55), "Slime");
		intmobs.put(new Integer(49), "Monster");
		intmobs.put(new Integer(53), "Giant");
		intmobs.put(new Integer(95), "Wolf");
		intmobs.put(new Integer(59), "CaveSpider");
		intmobs.put(new Integer(58), "Enderman");
		intmobs.put(new Integer(60), "Silverfish");
		intmobs.put(new Integer(63), "EnderDragon");
		intmobs.put(new Integer(120), "Villager");
		intmobs.put(new Integer(61), "Blaze");
		intmobs.put(new Integer(96), "MushroomCow");
		intmobs.put(new Integer(62), "MagmaCube");
		intmobs.put(new Integer(97), "Snowman");
		stringmobs.put("Pig", new Integer(90));
		stringmobs.put("Chicken", new Integer(93));
		stringmobs.put("Cow", new Integer(92));
		stringmobs.put("Sheep", new Integer(91));
		stringmobs.put("Squid", new Integer(94));
		stringmobs.put("Creeper", new Integer(50));
		stringmobs.put("Ghast", new Integer(53));
		stringmobs.put("PigZombie", new Integer(57));
		stringmobs.put("Skeleton", new Integer(51));
		stringmobs.put("Spider", new Integer(52));
		stringmobs.put("Zombie", new Integer(54));
		stringmobs.put("Slime", new Integer(55));
		stringmobs.put("Monster", new Integer(49));
		stringmobs.put("Giant", new Integer(53));
		stringmobs.put("Wolf", new Integer(95));
		stringmobs.put("CaveSpider", new Integer(59));
		stringmobs.put("Enderman", new Integer(58));
		stringmobs.put("Silverfish", new Integer(60));
		stringmobs.put("EnderDragon", new Integer(63));
		stringmobs.put("Villager", new Integer(120));
		stringmobs.put("Blaze", new Integer(61));
		stringmobs.put("MushroomCow", new Integer(96));
		stringmobs.put("MagmaCube", new Integer(62));
		stringmobs.put("Snowman", new Integer(97));
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER &&
				plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")) {
			try {
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlock().getState();
				String monster = theSpawner.getCreatureTypeId();
				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
				if(stringmobs.containsKey(monster)) {
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), 
							new ItemStack(Material.MOB_SPAWNER, 1, stringmobs.get(monster).shortValue()));
				}else {
					//If we don't know the mob type, let's just set it to a pig spawner.
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), 
							new ItemStack(Material.MOB_SPAWNER, 1, (short) 90));
				}
			}catch (Exception e) {
			}
			
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled() && event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			if(plugin.hasPermissions(event.getPlayer(), "monsterbox.place")) {
				String type = intmobs.get(plugin.playermonsterspawner.get(event.getPlayer().getName()));
				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just placed a " + ChatColor.RED + type.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlockPlaced().getState();
		    	CreatureType ct = CreatureType.fromName(type);
		        if (ct == null) {
		            return;
		        }
		        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SetSpawner(theSpawner, ct));
			}else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to place a monster spawner.");
			}
		}
	}
}
