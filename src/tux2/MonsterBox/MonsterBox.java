package tux2.MonsterBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.Register;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;

/**
 * MonsterBox for Bukkit
 *
 * @author tux2
 */
public class MonsterBox extends JavaPlugin {
    //private final MonsterBoxPlayerListener playerListener = new MonsterBoxPlayerListener(this);
    //private final MonsterBoxBlockListener blockListener = new MonsterBoxBlockListener(this);
    private final MonsterBoxServerListener serverListener = new MonsterBoxServerListener(this);
    private final MonsterBoxCommands commandL = new MonsterBoxCommands(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private static PermissionHandler Permissions;
    public MonsterBoxBlockListener bl;
    public Register iConomy = null;
	boolean useiconomy = false;
	public double iconomyprice = 0.0;
	public String version = "0.3";
    public HashSet<Byte> transparentBlocks = new HashSet<Byte>();
    ConcurrentHashMap<String, Integer> playermonsterspawner = new ConcurrentHashMap<String, Integer>();

    public MonsterBox() {
        super();
        loadconfig();
        
      //Setting transparent blocks.
        transparentBlocks.add((byte) 0); // Air
        transparentBlocks.add((byte) 8); // Water
        transparentBlocks.add((byte) 9); // Stationary Water
        transparentBlocks.add((byte) 20); // Glass
        transparentBlocks.add((byte) 65); // Ladder
        transparentBlocks.add((byte) 66); // Rail
        transparentBlocks.add((byte) 78); // Snow

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

   

    public void onEnable() {
    	setupPermissions();
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        bl = new MonsterBoxBlockListener(this);
        MonsterBoxPlayerListener pl = new MonsterBoxPlayerListener(this);
        if(useiconomy ) {
        	pm.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Monitor, this);
            pm.registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Monitor, this);
        }
        pm.registerEvent(Type.BLOCK_BREAK, bl, Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_PLACE, bl, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_INTERACT, pl, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_ITEM_HELD, pl, Priority.Monitor, this);
        PluginCommand batchcommand = this.getCommand("mbox");
		batchcommand.setExecutor(commandL);
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("MonsterBox disabled!");
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
    private void setupPermissions() {
        Plugin permissions = this.getServer().getPluginManager().getPlugin("Permissions");

        if (Permissions == null) {
            if (permissions != null) {
                Permissions = ((Permissions)permissions).getHandler();
            } else {
            }
        }
    }
    
    public boolean hasPermissions(Player player, String node) {
        if (Permissions != null) {
            return Permissions.has(player, node);
        } else {
            return player.hasPermission(node);
        }
    }
    
    private void loadconfig() {
		File folder = new File("plugins/MonsterBox");

		// check for existing file
		File configFile = new File("plugins/MonsterBox/settings.ini");
		
		//if it exists, let's read it, if it doesn't, let's create it.
		if (configFile.exists()) {
			try {
				Properties themapSettings = new Properties();
				themapSettings.load(new FileInputStream(configFile));
		        
		        String iconomy = themapSettings.getProperty("useEconomy", "false");
		        String price = themapSettings.getProperty("price", "0.0");
		        //If the version isn't set, the file must be at 0.2
		        String theversion = themapSettings.getProperty("version", "0.1");
			    
			    useiconomy = stringToBool(iconomy);
			    try {
			    	iconomyprice = Double.parseDouble(price.trim());
			    } catch (Exception ex) {
			    	
			    }
			    //Let's see if we need to upgrade the config file
			    double dbversion = 0.1;
			    try {
			    	dbversion = Double.parseDouble(theversion.trim());
			    } catch (Exception ex) {
			    	
			    }
			    if(dbversion < 0.3) {
			    	//If we are using the old config file let's convert that variable... otherwise we won't want to do that...
			    	if(dbversion == 0.1) {
				        String sconomy = themapSettings.getProperty("useiConomy", "false");
					    useiconomy = stringToBool(sconomy);
			    	}
			    	updateIni();
			    }
			} catch (IOException e) {
				
			}
		}else {
			System.out.println("[MonsterBox] Configuration file not found");

			System.out.println("[MonsterBox] + creating folder plugins/MonsterBox");
			folder.mkdir();

			System.out.println("[MonsterBox] - creating file settings.ini");
			updateIni();
		}
	}

	private void updateIni() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/settings.ini"));
			outChannel.write("#This is the main MonsterBos config file\n" +
					"#\n" +
					"# useiConomy: Charge to change monster spawner type using your economy system\n" +
					"useEconomy = " + useiconomy + "\n" +
					"# price: The price to change monster spawner type\n" +
					"price = " + iconomyprice + "\n\n" +
					"#Do not change anything below this line unless you know what you are doing!\n" +
					"version = " + version );
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - file creation failed, using defaults.");
		}
		
	}
	
	public boolean hasEconomy() {
		if(iConomy != null) {
			return Methods.hasMethod();
		}else {
			return false;
		}
	}
	
	public Method getEconomy() {
		if(iConomy != null) {
			return Methods.getMethod();
		}else {
			return null;
		}
	}
	
	private synchronized boolean stringToBool(String thebool) {
		boolean result;
		if (thebool.trim().equalsIgnoreCase("true") || thebool.trim().equalsIgnoreCase("yes")) {
	    	result = true;
	    } else {
	    	result = false;
	    }
		return result;
	}
}

