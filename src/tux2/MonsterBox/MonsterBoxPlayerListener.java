package tux2.MonsterBox;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxPlayerListener extends PlayerListener {
	
	MonsterBox plugin;
	
	public MonsterBoxPlayerListener(MonsterBox plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event)  {
		ItemStack is = event.getPlayer().getItemInHand();
		if(is.getType() == Material.MOB_SPAWNER) {
			plugin.playermonsterspawner.put(event.getPlayer().getName(), new Integer(is.getDurability()));
		}else if(plugin.usespout != null && is.getType().getId() == plugin.tool && event.getClickedBlock() != null && event.getClickedBlock().getTypeId() == 52) {
			Player player = event.getPlayer();
			SpoutPlayer splayer = SpoutManager.getPlayer(player);
			if(splayer.isSpoutCraftEnabled() && plugin.hasPermissions(player, "monsterbox.set")) {
				CreatureSpawner theSpawner = (CreatureSpawner) event.getClickedBlock().getState();
				String monster = theSpawner.getCreatureTypeId().toLowerCase();
				splayer.closeActiveWindow();
				plugin.ss.createMonsterGUI("This is currently a " + monster + " spawner.", !plugin.hasPermissions(splayer, "monsterbox.free"), splayer);
			}
		}
		
	}
	
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		ItemStack is = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if(is.getType() == Material.MOB_SPAWNER) {
			event.getPlayer().sendMessage(ChatColor.GOLD + "You are now holding a " + ChatColor.DARK_RED + plugin.bl.intmobs.get(new Integer(is.getDurability())) + ChatColor.GOLD + " spawner.");
		}
		
	}

}
