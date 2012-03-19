package tux2.MonsterBox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxScreenListener implements Listener {

	MonsterBox plugin;

	public MonsterBoxScreenListener(MonsterBox plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onButtonClick(ButtonClickEvent event) {
		// See if we are the owners of this button...
		if (plugin == event.getButton().getPlugin()) {
			Button eventbutton = event.getButton();
			String completebutton = eventbutton.getText();
			String[] buttonsplit = completebutton.split(" ");
			String mobname = buttonsplit[buttonsplit.length - 1];
			SpoutPlayer player = event.getPlayer();
			if (mobname.equalsIgnoreCase("close")) {
				player.getMainScreen().closePopup();
				//player.closeActiveWindow();
			} else {

				if (plugin.hasPermissions(player, "monsterbox.set")) {
					if (plugin.hasPermissions(player, "monsterbox.spawn."
							+ mobname.toLowerCase())) {
						if (plugin.useiconomy && plugin.hasEconomy()) {
							if (plugin.hasPermissions(player, "monsterbox.free")) {
								if (plugin.setSpawner(player.getTargetBlock(plugin.transparentBlocks, 40), mobname)) {
									player.sendNotification("Mob Spawner changed!", plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
									player.getMainScreen().closePopup();
									//player.closeActiveWindow();
								} else {
									player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
								}
							} else if (plugin.iConomy.hasAccount(player.getName())) {
								double balance = plugin.iConomy.getBalance(player.getName());
								if (balance >= plugin.getMobPrice(mobname)) {
									if (plugin.setSpawner(player.getTargetBlock(plugin.transparentBlocks, 40), mobname)) {
										plugin.iConomy.withdrawPlayer(player.getName(), plugin.getMobPrice(mobname));
										player.sendNotification("Mob Spawner changed!", plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
										player.getMainScreen().closePopup();
										//player.closeActiveWindow();
									} else {
										player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
									}
								} else {
									player.sendNotification("Insufficient Funds!", "You need " + plugin.iConomy.format(plugin.getMobPrice(mobname)) + "!", Material.MOB_SPAWNER);
								}
							} else {
								player.sendNotification("No Bank account!", 
										"You need a bank account and " + plugin.iConomy.format(plugin.getMobPrice(mobname)) + "!", Material.MOB_SPAWNER);
							}
						} else {
							if (plugin.setSpawner(player.getTargetBlock(
									plugin.transparentBlocks, 40), mobname)) {
								player.sendNotification("Mob Spawner changed!", plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
								player.getMainScreen().closePopup();
								//player.closeActiveWindow();
							} else {
								player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
							}
						}
					} else {
						player.sendNotification("Mob Unavailable", "Permission denied.", Material.FIRE);
					}
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permission to change spawner types!");
					player.getMainScreen().closePopup();
					//player.closeActiveWindow();
				}
			}
		}
	}

}
