package tux2.MonsterBox;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import com.nijikokun.register.Register;

public class MonsterBoxServerListener implements Listener {
	
	MonsterBox plugin;
	
	public MonsterBoxServerListener(MonsterBox plugin) {
        this.plugin = plugin;
    }

	@EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.iConomy != null) {
            if (event.getPlugin().getDescription().getName().equals("Register")) {
                plugin.iConomy = null;
                System.out.println("[MonsterBox] un-hooked from Register.");
            }
        }
    }

	@EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.iConomy == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("Register");

            if (iConomy != null) {
                if (iConomy.isEnabled() && iConomy.getClass().getName().equals("com.nijikokun.register.Register")) {
                    plugin.iConomy = (Register)iConomy;
                    System.out.println("[MonsterBox] hooked into Register.");
                }
            }
        }
    }
}
