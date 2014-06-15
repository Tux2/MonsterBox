package tux2.MonsterBox;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxPlayerListener implements Listener {
	
	MonsterBox plugin;
	
	public MonsterBoxPlayerListener(MonsterBox plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)  {
		if(!event.isCancelled()) {
			ItemStack is = event.getPlayer().getItemInHand();
			Player player = event.getPlayer();
			if(is.getType() == Material.MONSTER_EGG && event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
				if(plugin.hasPermissions(player, "monsterbox.eggset") && plugin.bl.intmobs.containsKey(new Integer(is.getDurability()))) {
					String type = plugin.bl.intmobs.get(new Integer(is.getDurability()));
					Block theSpawner = event.getClickedBlock();
			        if (plugin.hasPermissions(player, "monsterbox.eggspawn." + type.toLowerCase())) {
			        	if(plugin.useiconomy && plugin.getEggMobPrice(type) > 0) {
							if(!player.hasPermission("monsterbox.freeegg")) {
								if(plugin.iConomy.getBalance(player.getName()) < plugin.getEggMobPrice(type)) {
									player.sendMessage(ChatColor.DARK_GREEN + "Oops! You need " + plugin.iConomy.format(plugin.getEggMobPrice(type)) + " to make a "  + ChatColor.RED + type.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
									event.setCancelled(true);
									return;
								}
							}
						}
			        	if(plugin.setSpawner(theSpawner, type)) {
							player.sendMessage(ChatColor.DARK_GREEN + "KERPOW! That is now a " + ChatColor.RED + type.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
				        	//Now that we set the spawner type let's remove the egg, but only if the player is in survival mode...
				        	if(player.getGameMode() == GameMode.SURVIVAL) {
				        		if(is.getAmount() == 1) {
				        			player.setItemInHand(new ItemStack(Material.AIR));
				        		}else {
						        	is.setAmount(is.getAmount() - 1);
				        		}
				        	}
				        	if(plugin.useiconomy && plugin.getEggMobPrice(type) > 0) {
				        		if(!player.hasPermission("monsterbox.freeegg")) {
				        			plugin.iConomy.withdrawPlayer(player.getName(), plugin.getEggMobPrice(type));
				        			player.sendMessage(ChatColor.DARK_GREEN + "You just spent " + plugin.iConomy.format(plugin.getEggMobPrice(type)) + " setting that spawner.");
				        		}
							}
			        	}else {
			        		player.sendMessage(ChatColor.DARK_RED + "Oops, something went wrong while setting the spawner to a " + type.toLowerCase() + ".");
			        	}
			        	event.setCancelled(true);
			        }else {
						if(type != null && !plugin.hasPermissions(player, "monsterbox.eggthrow." + type.toLowerCase())) {
							if(plugin.hasPermissions(player, "monsterbox.eggthrowmessage")) {
								player.sendMessage(plugin.eggthrowmessage);
							}
							event.setCancelled(true);
						}else if(type == null && !plugin.hasPermissions(player, "monsterbox.eggthrow.other")) {
							if(plugin.hasPermissions(player, "monsterbox.eggthrowmessage")) {
								player.sendMessage(plugin.eggthrowmessage);
							}
							event.setCancelled(true);
						}
					}
				}
			}else if(is.getType() == Material.MONSTER_EGG && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				String type = plugin.bl.intmobs.get(new Integer(is.getDurability()));
				if(type != null && !plugin.hasPermissions(player, "monsterbox.eggthrow." + type.toLowerCase())) {
					if(plugin.hasPermissions(player, "monsterbox.eggthrowmessage")) {
						player.sendMessage(plugin.eggthrowmessage);
					}
					event.setCancelled(true);
				}else if(type == null && !plugin.hasPermissions(player, "monsterbox.eggthrow.other")) {
					event.setCancelled(true);
				}
			}else if(plugin.usespout != null && is.getType() == plugin.tool && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
				SpoutPlayer splayer = SpoutManager.getPlayer(player);
				if(splayer.isSpoutCraftEnabled() && plugin.hasPermissions(player, "monsterbox.set")) {
					CreatureSpawner theSpawner = (CreatureSpawner) event.getClickedBlock().getState();
					String monster = theSpawner.getCreatureTypeName().toLowerCase();
					splayer.getMainScreen().closePopup();
					plugin.ss.createMonsterGUI("This is currently a " + monster + " spawner.", !plugin.hasPermissions(splayer, "monsterbox.free"), splayer);
				}
			}
		}
		
	}
}
