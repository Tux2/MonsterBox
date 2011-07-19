package tux2.MonsterBox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.block.*;
import org.bukkit.inventory.ItemStack;

import com.iConomy.system.Holdings;

public class MonsterBoxCommands implements CommandExecutor {
	
	MonsterBox plugin;
	
	public MonsterBoxCommands(MonsterBox plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
			String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if(commandLabel.equalsIgnoreCase("mbox")){
				if(args.length > 1) {
					if(args[0].trim().equalsIgnoreCase("set") && player.getTargetBlock(plugin.transparentBlocks, 40).getTypeId() == 52) {
						if(plugin.hasPermissions(player, "monsterbox.set")) {
							if(plugin.hasPermissions(player, "monsterbox.spawn." + args[1].toLowerCase())) {
								if(plugin.useiconomy && plugin.iConomy != null) {
									if(plugin.hasPermissions(player, "monsterbox.free")) {
										if(setSpawner(player.getTargetBlock(plugin.transparentBlocks, 40), args[1])) {
											player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
											return true;
										}else {
											player.sendMessage(ChatColor.RED + "Invalid mob type.");
										}
									}else if(plugin.iConomy.hasAccount(player.getName())) {
										Holdings balance = plugin.iConomy.getAccount(player.getName()).getHoldings();
										if(balance.hasEnough(plugin.iconomyprice)) {
											if(setSpawner(player.getTargetBlock(plugin.transparentBlocks, 40), args[1])) {
												balance.subtract(plugin.iconomyprice);		
												player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
												return true;
											}else {
												player.sendMessage(ChatColor.RED + "Invalid mob type.");
											}
										}else {
											player.sendMessage(ChatColor.RED + "You need " + plugin.iConomy.format(plugin.iconomyprice) + " to set the type of monster spawner!");
										}
								    } else {
								    	player.sendMessage(ChatColor.RED + "You need a bank account and " + plugin.iConomy.format(plugin.iconomyprice) + " to set the type of monster spawner!");
								    }
								}else {
									if(setSpawner(player.getTargetBlock(plugin.transparentBlocks, 40), args[1])) {
										player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
										return true;
									}else {
										player.sendMessage(ChatColor.RED + "Invalid mob type.");
									}
								}
							}else {
								player.sendMessage(ChatColor.RED + "You don't have permission to create a " + args[1].toLowerCase() + " spawner.");
								return true;
							}
						} else {
							player.sendMessage(ChatColor.RED + "You don't have permission to change spawner types!");
						}
					} else {
						return false;
					}
				} else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("get")) {
						if(plugin.hasPermissions(player, "monsterbox.view")) {
							Block targetblock = player.getTargetBlock(plugin.transparentBlocks, 40);
							if(targetblock.getType() == Material.MOB_SPAWNER) {
								try {
									CreatureSpawner theSpawner = (CreatureSpawner) targetblock.getState();
									String monster = theSpawner.getCreatureTypeId().toLowerCase();
									player.sendMessage(ChatColor.GREEN + "That is a " + ChatColor.RED + monster + ChatColor.GREEN + " spawner.");
							        return true;
								}catch (Exception e) {
									return false;
								}
							}else {
								player.sendMessage(ChatColor.RED + "You must target a MobSpawner first!");
								return true;
							}
						}else {
							player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
							return true;
						}
					} else {
						player.sendMessage(ChatColor.GREEN + "To get the Spawner type: /mbox get");
					}
				} else {
					player.sendMessage(ChatColor.GREEN + "To set the Spawner type: /mbox set <mobname>");
				}
				
			}
		}
		return false;
	}
	
	private boolean setSpawner(Block targetBlock, String type) {
		try {
			CreatureSpawner theSpawner = (CreatureSpawner) targetBlock.getState();
			if (type.equalsIgnoreCase("PigZombie")) {
	    		type = "PigZombie";
	    	}else {
	    		type = this.capitalCase(type);
	    	}
	    	CreatureType ct = CreatureType.fromName(type);
	        if (ct == null) {
	            return false;
	        }
	        theSpawner.setCreatureType(ct);
	        return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	private String capitalCase(String s)
    {
        return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
    }

}
