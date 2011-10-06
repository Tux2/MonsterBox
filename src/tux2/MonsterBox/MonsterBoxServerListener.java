package tux2.MonsterBox;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import com.nijikokun.register.Register;

public class MonsterBoxServerListener extends ServerListener {
	
	MonsterBox plugin;
	
	public MonsterBoxServerListener(MonsterBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.iConomy != null) {
            if (event.getPlugin().getDescription().getName().equals("Register")) {
                plugin.iConomy = null;
                System.out.println("[MonsterBox] un-hooked from Register.");
            }
        }
    }

    @Override
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
