package tux2.MonsterBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.getspout.spout.Spout;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * MonsterBox for Bukkit
 *
 * @author tux2
 */
public class MonsterBox extends JavaPlugin {
    //private final MonsterBoxPlayerListener playerListener = new MonsterBoxPlayerListener(this);
    //private final MonsterBoxBlockListener blockListener = new MonsterBoxBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final ConcurrentHashMap<String, Double> mobprice = new ConcurrentHashMap<String, Double>();
    private final ConcurrentHashMap<String, Double> mobeggprice = new ConcurrentHashMap<String, Double>();
    private final ConcurrentHashMap<String, LinkedList<EntityType>> disabledspawners = new ConcurrentHashMap<String, LinkedList<EntityType>>();
    ConcurrentHashMap<String, Integer> disabledspawnerlocs = new ConcurrentHashMap<String, Integer>();
    private static PermissionHandler Permissions;
    public MonsterBoxBlockListener bl;
    public Economy iConomy = null;
	boolean useiconomy = false;
	public double iconomyprice = 0.0;
	public Spout usespout = null;
	public boolean separateprices = false;
	public int tool = Material.GOLD_SWORD.getId();
	public int buttonwidth = 80;
	public String version = "0.8";
	public SpoutStuff ss = null;
    public HashSet<Byte> transparentBlocks = new HashSet<Byte>();
    private ConcurrentHashMap<String, String> mobcase = new ConcurrentHashMap<String, String>();
	public String eggthrowmessage = "I'm sorry, but you can't spawn that mob.";
	public boolean needssilktouch = false;
	public double eggprice = 0.0;
	public boolean separateeggprices = false;
	public String blocksavefile = "plugins/MonsterBox/disabledspawners.list";
    public MonsterBox() {
        super();
        loadconfig();
        loadprices();
        loadeggprices();
        loadDisabledSpawners();
        
      //Setting transparent blocks.
        transparentBlocks.add((byte) 0); // Air
        transparentBlocks.add((byte) 8); // Water
        transparentBlocks.add((byte) 9); // Stationary Water
        transparentBlocks.add((byte) 20); // Glass
        transparentBlocks.add((byte) 30); // Cobweb
        transparentBlocks.add((byte) 65); // Ladder
        transparentBlocks.add((byte) 66); // Rail
        transparentBlocks.add((byte) 78); // Snow
        transparentBlocks.add((byte) 83); // Sugar Cane
        transparentBlocks.add((byte) 101); // Iron Bars
        transparentBlocks.add((byte) 102); // Glass Pane
        transparentBlocks.add((byte) 106); // Vines

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }
    
    private void loadprices() {

		File folder = new File("plugins/MonsterBox");

		// check for existing file
		File configFile = new File("plugins/MonsterBox/prices.ini");
		
		//if it exists, let's read it, if it doesn't, let's create it.
		if (configFile.exists()) {
			try {
				mobprice.clear();
				Properties theprices = new Properties();
				theprices.load(new FileInputStream(configFile));
				Iterator<Entry<Object, Object>> iprices = theprices.entrySet().iterator();
				while(iprices.hasNext()) {
					Entry<Object, Object> price = iprices.next();
					try {
						mobprice.put(price.getKey().toString().toLowerCase(), new Double(price.getValue().toString()));
					}catch (NumberFormatException ex) {
						System.out.println("[MonsterBox] Unable to parse the value for " + price.getKey().toString());
					}
				}
			} catch (IOException e) {
				
			}
			//A quick and dirty way to see if there are any new mobs we need to add to the list
			if(mobprice.size() < CreatureTypes.values().length) {
				System.out.println("[MonsterBox] - New mobs found! Updating prices.ini");
				createprices();
			}
		}else {
			System.out.println("[MonsterBox] Price file not found");
			folder.mkdir();

			System.out.println("[MonsterBox] - creating file prices.ini");
			createprices();
		}
		
	}
    
    private void loadeggprices() {

		File folder = new File("plugins/MonsterBox");

		// check for existing file
		File configFile = new File("plugins/MonsterBox/eggprices.ini");
		
		//if it exists, let's read it, if it doesn't, let's create it.
		if (configFile.exists()) {
			try {
				mobeggprice.clear();
				Properties theprices = new Properties();
				theprices.load(new FileInputStream(configFile));
				Iterator<Entry<Object, Object>> iprices = theprices.entrySet().iterator();
				while(iprices.hasNext()) {
					Entry<Object, Object> price = iprices.next();
					try {
						mobeggprice.put(price.getKey().toString().toLowerCase(), new Double(price.getValue().toString()));
					}catch (NumberFormatException ex) {
						System.out.println("[MonsterBox] Unable to parse the value for " + price.getKey().toString() + "in the eggprices.ini file.");
					}
				}
			} catch (IOException e) {
				
			}
			//A quick and dirty way to see if there are any new mobs we need to add to the list
			if(mobeggprice.size() < CreatureTypes.values().length) {
				System.out.println("[MonsterBox] - New mobs found! Updating eggprices.ini");
				createeggprices();
			}
		}else {
			System.out.println("[MonsterBox] Egg price file not found");
			folder.mkdir();

			System.out.println("[MonsterBox] - creating file eggprices.ini");
			createeggprices();
		}
		
	}
    
	private void createprices() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/prices.ini"));
			outChannel.write("#This config file contains all the separate prices for all the mobs\n" +
					"# if the option separateprices is true\n" +
					"\n" +
					"\n");
			CreatureTypes[] mobs = CreatureTypes.values();
			for(CreatureTypes mob : mobs) {
				outChannel.write(mob.toString() + " = " + String.valueOf(getMobPrice(mob.toString())) + "\n");
			}
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - Prices file creation failed, using defaults.");
		}
		
	}
    
	private void createeggprices() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/eggprices.ini"));
			outChannel.write("#This config file contains all the separate prices for all the mobs\n" +
					"# for eggs if the option separateeggprices is true\n" +
					"\n" +
					"\n");
			CreatureTypes[] mobs = CreatureTypes.values();
			for(CreatureTypes mob : mobs) {
				outChannel.write(mob.toString() + " = " + String.valueOf(getEggMobPrice(mob.toString())) + "\n");
			}
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - Egg prices file creation failed, using defaults.");
		}
		
	}
	
	public void onEnable() {
    	setupPermissions();
    	setupSpout();
    	setupMobCase();
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        bl = new MonsterBoxBlockListener(this);
        MonsterBoxPlayerListener pl = new MonsterBoxPlayerListener(this);
        if(useiconomy ) {
            setupEconomy();
        }
        pm.registerEvents(bl, this);
        pm.registerEvents(pl, this);
        if(usespout != null) {
        	pm.registerEvents(new MonsterBoxScreenListener(this), this);
        	ss = new SpoutStuff(this);
        }
        MonsterBoxCommands commandL = new MonsterBoxCommands(this);
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
    
    private void setupSpout() {
    	Plugin p = getServer().getPluginManager().getPlugin("Spout");
		if(p == null){
			usespout = null;
			System.out.println("[MonsterBox] Spout not detected. Disabling spout support.");
		} else {
			try {
				usespout = (Spout)p;
			}catch (Exception e) {
				System.out.println("[MonsterBox] Error hooking into spout. Disabling spout support.");
			}
			System.out.println("[MonsterBox] Spout detected. Spout support enabled.");
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
		        String eggsprice = themapSettings.getProperty("eggprice", "0.0");
		        String sprices = themapSettings.getProperty("separateprices", "false");
		        String seggprices = themapSettings.getProperty("separateeggprices", "false");
		        String swidth = themapSettings.getProperty("buttonwidth", "100");
		        String stool = themapSettings.getProperty("changetool", String.valueOf(Material.GOLD_SWORD.getId()));
		        //If the version isn't set, the file must be at 0.2
		        String theversion = themapSettings.getProperty("version", "0.1");
		        eggthrowmessage = themapSettings.getProperty("eggdenymessage", eggthrowmessage);
		        String silktouch = themapSettings.getProperty("needssilktouch", "false");
		        
		        needssilktouch = stringToBool(silktouch);
			    
			    useiconomy = stringToBool(iconomy);
			    separateprices = stringToBool(sprices);
			    separateeggprices = stringToBool(seggprices);
			    try {
			    	tool = Integer.parseInt(stool.trim());
			    } catch (Exception ex) {
			    	
			    }
			    try {
			    	buttonwidth = Integer.parseInt(swidth.trim());
			    } catch (Exception ex) {
			    	
			    }
			    try {
			    	iconomyprice = Double.parseDouble(price.trim());
			    } catch (Exception ex) {
			    	
			    }
			    try {
			    	eggprice = Double.parseDouble(eggsprice.trim());
			    } catch (Exception ex) {
			    	
			    }
			    //Let's see if we need to upgrade the config file
			    double dbversion = 0.1;
			    try {
			    	dbversion = Double.parseDouble(theversion.trim());
			    } catch (Exception ex) {
			    	
			    }
			    if(dbversion < 0.8) {
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
			outChannel.write("#This is the main MonsterBox config file\n" +
					"#\n" +
					"# useiConomy: Charge to change monster spawner type using your economy system\n" +
					"useEconomy = " + useiconomy + "\n" +
					"# price: The price to change monster spawner type\n" +
					"price = " + iconomyprice + "\n\n" +
					"# eggprice: The price to change monster spawner type using eggs\n" +
					"eggprice = " + eggprice + "\n\n" +
					"# separateprices: If you want separate prices for all the different types of mobs\n" +
					"# set this to true.\n" +
					"separateprices = " + separateprices + "\n" +
					"# separateeggprices: If you want separate prices for all the different types of mobs\n" +
					"# set this to true.\n" +
					"separateeggprices = " + separateeggprices + "\n" +
					"# changetool is the tool that opens up the spout gui for changing the monster spawner.\n" +
					"changetool = " + tool + "\n" +
					"# needssilktouch Does the player need a silk touch enchanted tool to get a spawner?.\n" +
					"needssilktouch = " + needssilktouch + "\n" +
					"# buttonwidth changes the width of the buttons in the spoutcraft gui, just in case the\n" +
					"# text doesn't fit for you.\n" +
					"buttonwidth = " + buttonwidth + "\n\n" +
					"# eggdenymessage sets the message displayed to players when they are denied egg spawning\n" +
					"# if they have the monsterbox.eggthrowmessage permission node.\n" +
					"eggdenymessage = " + eggthrowmessage + "\n\n" +
					"#Do not change anything below this line unless you know what you are doing!\n" +
					"version = " + version );
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - file creation failed, using defaults.");
		}
		
	}
	
	public boolean hasEconomy() {
		if(iConomy != null) {
			return iConomy.isEnabled();
		}else {
			return false;
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



	boolean setSpawner(Block targetBlock, String type) {
		try {
			CreatureSpawner theSpawner = (CreatureSpawner) targetBlock.getState();
			if (mobcase.containsKey(type.toLowerCase().trim())) {
	    		type = mobcase.get(type.toLowerCase().trim());
	    	}else {
	    		type = this.capitalCase(type);
	    	}
	    	EntityType ct = EntityType.fromName(type);
	        if (ct == null) {
	        	//It seems there's a typo with the ocelot and iron golem in the beta builds...
	        	//If I don't do a quick hack I'm going to get all the noobs wondering why
	        	//it doesn't work right...
	        	if(type.equalsIgnoreCase("ocelot")) {
	        		theSpawner.setSpawnedType(EntityType.OCELOT);
					if(disabledspawnerlocs.containsKey(locationBuilder(targetBlock.getLocation()))) {
	    				removeDisabledSpawner(targetBlock);
	    			}
	        		return true;
	        	}else if(type.equalsIgnoreCase("IronGolem")) {
	        		theSpawner.setSpawnedType(EntityType.IRON_GOLEM);
					if(disabledspawnerlocs.containsKey(locationBuilder(targetBlock.getLocation()))) {
	    				removeDisabledSpawner(targetBlock);
	    			}
	        		return true;
	        	}
	            return false;
	        }
	        theSpawner.setSpawnedType(ct);
	        if(disabledspawnerlocs.containsKey(locationBuilder(targetBlock.getLocation()))) {
				removeDisabledSpawner(targetBlock);
			}
	        return true;
		}catch (Exception e) {
			return false;
		}
	}

	String capitalCase(String s)
	{
	    return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
	}
	
	public double getMobPrice(String name) {
		if(separateprices && mobprice.containsKey(name.toLowerCase())) {
			return mobprice.get(name.toLowerCase()).doubleValue();
		}else {
			return iconomyprice;
		}
	}
	
	public double getEggMobPrice(String name) {
		if(separateeggprices && mobeggprice.containsKey(name.toLowerCase())) {
			return mobeggprice.get(name.toLowerCase()).doubleValue();
		}else {
			return eggprice;
		}
	}
	
	private void setupMobCase() {
		CreatureTypes[] mobs = CreatureTypes.values();
		for(CreatureTypes mob : mobs) {
			String mobname = mob.toString().trim();
			mobcase.put(mobname.toLowerCase(), mobname);
		}
	}
	
	private void setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            iConomy = economyProvider.getProvider();
        }
    }
	
	public boolean canSpawnMob(Location loc, EntityType type) {
		String locname = locationBuilder(loc);
		if(disabledspawners.containsKey(locname)) {
			return !disabledspawners.get(locname).contains(type);
		}
		return true;
	}
	
	public String locationBuilder(Location loc) {
		return loc.getBlockX() + "." + loc.getBlockY() + "." + loc.getBlockZ() + "." + loc.getWorld().getName();
	}
	
	public void addDisabledSpawner(Block spawner) {
		if(spawner.getType() == Material.MOB_SPAWNER) {
			CreatureSpawner theSpawner = (CreatureSpawner) spawner.getState();
			EntityType mobname = theSpawner.getSpawnedType();
			addDisabledSpawner(spawner.getLocation(), mobname);
		}
	}
	
	public void addDisabledSpawner(Location spawner, EntityType mobname) {
		int startx = spawner.getBlockX() - 4;
		int endx = startx + 8;
		int starty = spawner.getBlockY() - 1;
		int endy = starty + 8;
		int startz = spawner.getBlockZ() - 4;
		int endz = startz + 8;
		disabledspawnerlocs.put(locationBuilder(spawner), new Integer(mobname.getTypeId()));
		for(int x = startx; x < endx; x++) {
			for(int y = starty; y < endy; y++) {
				for(int z = startz; z < endz; z++) {
					String location = x + "." + y + "." + z + "." + spawner.getWorld().getName();
					if(disabledspawners.containsKey(location)) {
						disabledspawners.get(location).add(mobname);
					}else {
						LinkedList<EntityType> tlist = new LinkedList<EntityType>();
						tlist.add(mobname);
						disabledspawners.put(location, tlist);
					}
				}
			}
		}
		saveDisabledSpawners();
	}
	
	public void removeDisabledSpawner(Block spawner) {
		if(spawner.getType() == Material.MOB_SPAWNER) {
			CreatureSpawner theSpawner = (CreatureSpawner) spawner.getState();
			EntityType mobname = theSpawner.getSpawnedType();
			removeDisabledSpawner(spawner.getLocation(), mobname);
		}
	}
	
	public void removeDisabledSpawner(Location spawner, EntityType mobname) {
		int startx = spawner.getBlockX() - 4;
		int endx = startx + 8;
		int starty = spawner.getBlockY() - 1;
		int endy = starty + 8;
		int startz = spawner.getBlockZ() - 4;
		int endz = startz + 8;
		disabledspawnerlocs.remove(locationBuilder(spawner));
		for(int x = startx; x < endx; x++) {
			for(int y = starty; y < endy; y++) {
				for(int z = startz; z < endz; z++) {
					String location = x + "." + y + "." + z + "." + spawner.getWorld().getName();
					if(disabledspawners.containsKey(location)) {
						disabledspawners.get(location).remove(mobname);
					}
				}
			}
		}
		saveDisabledSpawners();
	}
	
	public void saveDisabledSpawners() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(blocksavefile)));
			out.writeObject(disabledspawnerlocs);
			out.flush();
			out.close();
		}catch (Exception e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadDisabledSpawners() {
		try {
			ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(blocksavefile)));
			disabledspawnerlocs = (ConcurrentHashMap<String, Integer>) out.readObject();
			Set<String> keys = disabledspawnerlocs.keySet();
			for(String key : keys) {
				try {
					int mobtype = disabledspawnerlocs.get(key).intValue();
					EntityType type = EntityType.fromId(mobtype);
					String[] location = key.split("\\.");
					String destworld = location[3];
					int x = Integer.parseInt(location[0]);
					int y = Integer.parseInt(location[1]);
					int z = Integer.parseInt(location[2]);
					int startx = x - 4;
					int endx = startx + 8;
					int starty = y - 1;
					int endy = starty + 8;
					int startz = z - 4;
					int endz = startz + 8;
					for(int x1 = startx; x1 < endx; x1++) {
						for(int y1 = starty; y1 < endy; y1++) {
							for(int z1 = startz; z1 < endz; z1++) {
								String slocation = x1 + "." + y1 + "." + z1 + "." + destworld;
								if(disabledspawners.containsKey(slocation)) {
									disabledspawners.get(slocation).add(type);
								}else {
									LinkedList<EntityType> tlist = new LinkedList<EntityType>();
									tlist.add(type);
									disabledspawners.put(slocation, tlist);
								}
							}
						}
					}
				}catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}catch (Exception e) {
			// If it doesn't work, no great loss!
			//e.printStackTrace();
		}
	}
}

