package tux2.MonsterBox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.nijikokun.register.payment.Method.MethodAccount;

public class MonsterBoxScreenListener extends ScreenListener {

	MonsterBox plugin;

	public MonsterBoxScreenListener(MonsterBox plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		// See if we are the owners of this button...
		if (plugin == event.getButton().getPlugin()) {
			Button eventbutton = event.getButton();
			String completebutton = eventbutton.getText();
			String[] buttonsplit = completebutton.split(" ");
			String mobname = buttonsplit[buttonsplit.length - 1];
			SpoutPlayer player = event.getPlayer();
			if (mobname.equalsIgnoreCase("close")) {
				player.closeActiveWindow();
			} else {

				if (plugin.hasPermissions(player, "monsterbox.set")) {
					if (plugin.hasPermissions(player, "monsterbox.spawn."
							+ mobname.toLowerCase())) {
						if (plugin.useiconomy && plugin.hasEconomy()) {
							if (plugin.hasPermissions(player, "monsterbox.free")) {
								if (plugin.setSpawner(player.getTargetBlock(
										plugin.transparentBlocks, 40), mobname)) {
									player.sendNotification("Mob Spawner changed!", plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
									player.closeActiveWindow();
								} else {
									player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
								}
							} else if (plugin.getEconomy().hasAccount(
									player.getName())) {
								MethodAccount balance = plugin.getEconomy()
										.getAccount(player.getName());
								if (balance.hasEnough(plugin.getMobPrice(mobname))) {
									if (plugin.setSpawner(player.getTargetBlock(plugin.transparentBlocks, 40), mobname)) {
										balance.subtract(plugin.getMobPrice(mobname));
										player.sendNotification("Mob Spawner changed!", plugin.capitalCase(mobname) + "s galore!",
												Material.MOB_SPAWNER);
										player.closeActiveWindow();
									} else {
										player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
									}
								} else {
									player.sendNotification("Insufficient Funds!", "You need " + plugin.getEconomy().format(plugin.getMobPrice(mobname)) + "!", Material.MOB_SPAWNER);
								}
							} else {
								player.sendNotification("No Bank account!", 
										"You need a bank account and " + plugin.getEconomy().format(plugin.getMobPrice(mobname)) + "!", Material.MOB_SPAWNER);
							}
						} else {
							if (plugin.setSpawner(player.getTargetBlock(
									plugin.transparentBlocks, 40), mobname)) {
								player.sendNotification("Mob Spawner changed!", plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
								player.closeActiveWindow();
							} else {
								player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
							}
						}
					} else {
						player.sendNotification("Mob Unavailable", "Permission denied.", Material.FIRE);
					}
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permission to change spawner types!");
					player.closeActiveWindow();
				}
			}
		}
	}

}
