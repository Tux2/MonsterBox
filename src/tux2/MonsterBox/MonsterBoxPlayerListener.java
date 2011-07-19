package tux2.MonsterBox;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MonsterBoxPlayerListener extends PlayerListener {
	
	MonsterBox plugin;
	
	public MonsterBoxPlayerListener(MonsterBox plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event)  {
		ItemStack is = event.getPlayer().getItemInHand();
		if(is.getType() == Material.MOB_SPAWNER) {
			plugin.playermonsterspawner.put(event.getPlayer().getName(), new Integer(is.getDurability()));
		}
		
	}
	
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		ItemStack is = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if(is.getType() == Material.MOB_SPAWNER) {
			event.getPlayer().sendMessage(ChatColor.GOLD + "You are now holding a " + ChatColor.DARK_RED + plugin.bl.intmobs.get(new Integer(is.getDurability())) + ChatColor.GOLD + " spawner.");
		}
		
	}

}
