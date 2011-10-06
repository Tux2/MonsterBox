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
		stringmobs.put("Pig", new Integer(0));
		stringmobs.put("Chicken", new Integer(1));
		stringmobs.put("Cow", new Integer(2));
		stringmobs.put("Sheep", new Integer(3));
		stringmobs.put("Squid", new Integer(4));
		stringmobs.put("Creeper", new Integer(5));
		stringmobs.put("Ghast", new Integer(6));
		stringmobs.put("PigZombie", new Integer(7));
		stringmobs.put("Skeleton", new Integer(8));
		stringmobs.put("Spider", new Integer(9));
		stringmobs.put("Zombie", new Integer(10));
		stringmobs.put("Slime", new Integer(11));
		stringmobs.put("Monster", new Integer(12));
		stringmobs.put("Giant", new Integer(13));
		stringmobs.put("Wolf", new Integer(14));
		stringmobs.put("CaveSpider", new Integer(15));
		stringmobs.put("Enderman", new Integer(16));
		stringmobs.put("Silverfish", new Integer(17));
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER &&
				plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")) {
			try {
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlock().getState();
				String monster = theSpawner.getCreatureTypeId();
				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), 
						new ItemStack(Material.MOB_SPAWNER, 1, stringmobs.get(monster).shortValue()));
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
