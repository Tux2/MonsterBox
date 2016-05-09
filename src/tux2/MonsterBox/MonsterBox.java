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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.getspout.spout.Spout;

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
	public MonsterBoxBlockListener bl;
	public Economy iConomy = null;
	boolean useiconomy = false;
	public double iconomyprice = 0.0;
	public Spout usespout = null;
	public boolean separateprices = false;
	public Material tool = Material.GOLD_SWORD;
	public int buttonwidth = 80;
	public String version = "0.8";
	public SpoutStuff ss = null;
	public HashSet<Material> transparentBlocks = new HashSet<Material>();
	private ConcurrentHashMap<String, String> mobcase = new ConcurrentHashMap<String, String>();
	public String eggthrowmessage = "I'm sorry, but you can't spawn that mob.";
	public boolean needssilktouch = false;
	public double eggprice = 0.0;
	public boolean separateeggprices = false;
	public boolean isneweggs = false;
	public boolean usetuxtwolib = false;
	public String blocksavefile = "plugins/MonsterBox/disabledspawners.list";
	public MonsterBox() {
		super();
		loadconfig();
		loadprices();
		loadeggprices();
		loadDisabledSpawners();

		//Setting transparent blocks.
		transparentBlocks.add(Material.AIR); // Air
		transparentBlocks.add(Material.WATER); // Water
		transparentBlocks.add(Material.STATIONARY_WATER); // Stationary Water
		transparentBlocks.add(Material.GLASS); // Glass
		transparentBlocks.add(Material.STAINED_GLASS); // Glass
		transparentBlocks.add(Material.WEB); // Cobweb
		transparentBlocks.add(Material.LADDER); // Ladder
		transparentBlocks.add(Material.RAILS); // Rail
		transparentBlocks.add(Material.ACTIVATOR_RAIL); // Rail
		transparentBlocks.add(Material.DETECTOR_RAIL); // Rail
		transparentBlocks.add(Material.POWERED_RAIL); // Rail
		transparentBlocks.add(Material.SNOW); // Snow
		transparentBlocks.add(Material.SUGAR_CANE); // Sugar Cane
		transparentBlocks.add(Material.IRON_FENCE); // Iron Bars
		transparentBlocks.add(Material.THIN_GLASS); // Glass Pane
		transparentBlocks.add(Material.STAINED_GLASS_PANE); // Glass Pane
		transparentBlocks.add(Material.VINE); // Vines

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
			getLogger().warning("Egg prices file creation failed, using defaults.");
		}

	}

	public void onEnable() {
		setupSpout();
		setupMobCase();

		String[] cbversionstring = getServer().getVersion().split(":");
		Pattern pmcversion = Pattern.compile("(\\d+)\\.(\\d+)\\.?(\\d*)");
		Matcher mcmatch = pmcversion.matcher(cbversionstring[1]);
		if(mcmatch.find()) {
			try{
				int majorversion = Integer.parseInt(mcmatch.group(1));
				int minorversion = Integer.parseInt(mcmatch.group(2));
				if(majorversion == 1) {
					if(minorversion > 9) {
						isneweggs = true;
						getLogger().info("[MonsterBox] MC 1.9 or above found, enabling version 2 egg handling.");
					}
				}else if(majorversion > 1) {
					isneweggs = true;
					getLogger().info("[MonsterBox] MC 1.9 or above found, enabling version 2 egg handling.");
				}
			}catch (Exception e) {
				getLogger().severe("[MonsterBox] Unable to get server version! Inaccurate spawn egg handling may occurr!");
				getLogger().severe("[MonsterBox] Server Version String: " + getServer().getVersion());
			}
		}else {
			getLogger().severe("[MonsterBox] Unable to get server version! Inaccurate spawn egg handling may occurr!");
			getLogger().severe("[MonsterBox] Server Version String: " + getServer().getVersion());
		}
		if(isneweggs) {
			setupTuxTwoLib();
		}else {
			getLogger().severe("This version of MonsterBox only works on Minecraft versions 1.9 or above!");
		}
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
	}
	public void onDisable() {
		
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

	private void setupSpout() {
		Plugin p = getServer().getPluginManager().getPlugin("Spout");
		if(p == null){
			usespout = null;
			getLogger().info("Spout not detected. Disabling spout support.");
		} else {
			try {
				usespout = (Spout)p;
			}catch (Exception e) {
				getLogger().warning("Error hooking into spout. Disabling spout support.");
			}
			getLogger().info("Spout detected. Spout support enabled.");
		}
	}
	
	public void setupTuxTwoLib() {
		Plugin p = getServer().getPluginManager().getPlugin("TuxTwoLib");
		if(p == null){
			usetuxtwolib = false;
			getLogger().severe("TuxTwoLib not detected! Mob eggs will not work in 1.9 or above!");
		} else {
			usetuxtwolib = true;
		}
		
	}

	public boolean hasPermissions(Player player, String node) {
		return player.hasPermission(node);
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
				String stool = themapSettings.getProperty("changetool", Material.GOLD_SWORD.toString());
				//If the version isn't set, the file must be at 0.2
				String theversion = themapSettings.getProperty("version", "0.1");
				eggthrowmessage = themapSettings.getProperty("eggdenymessage", eggthrowmessage);
				String silktouch = themapSettings.getProperty("needssilktouch", "false");

				needssilktouch = stringToBool(silktouch);

				useiconomy = stringToBool(iconomy);
				separateprices = stringToBool(sprices);
				separateeggprices = stringToBool(seggprices);
				try {
					int itool = Integer.parseInt(stool.trim());
					tool = Material.getMaterial(itool);
					updateIni();
				} catch (Exception ex) {
					tool = Material.getMaterial(stool.trim().toUpperCase());
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
			getLogger().info("Configuration file not found");

			getLogger().info("+ creating folder plugins/MonsterBox");
			folder.mkdir();

			getLogger().info("- creating file settings.ini");
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
					"changetool = " + tool.toString() + "\n" +
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
			getLogger().warning("- file creation failed, using defaults.");
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
				type = capitalCase(type);
			}
			EntityType ct = null;
			if(type.equalsIgnoreCase("bat")) {
				ct = EntityType.BAT;
			}else if(type.equalsIgnoreCase("blaze")) {
				ct = EntityType.BLAZE;
			}else if(type.equalsIgnoreCase("cavespider")) {
				ct = EntityType.CAVE_SPIDER;
			}else if(type.equalsIgnoreCase("chicken")) {
				ct = EntityType.CHICKEN;
			}else if(type.equalsIgnoreCase("cow")) {
				ct = EntityType.COW;
			}else if(type.equalsIgnoreCase("creeper")) {
				ct = EntityType.CREEPER;
			}else if(type.equalsIgnoreCase("enderdragon")) {
				ct = EntityType.ENDER_DRAGON;
			}else if(type.equalsIgnoreCase("enderman")) {
				ct = EntityType.ENDERMAN;
			}else if(type.equalsIgnoreCase("ghast")) {
				ct = EntityType.GHAST;
			}else if(type.equalsIgnoreCase("giant")) {
				ct = EntityType.GIANT;
			}else if(type.equalsIgnoreCase("horse")) {
				ct = EntityType.HORSE;
			}else if(type.equalsIgnoreCase("irongolem")) {
				ct = EntityType.IRON_GOLEM;
			}else if(type.equalsIgnoreCase("magmacube")) {
				ct = EntityType.MAGMA_CUBE;
			}else if(type.equalsIgnoreCase("mushroomcow")) {
				ct = EntityType.MUSHROOM_COW;
			}else if(type.equalsIgnoreCase("ocelot")) {
				ct = EntityType.OCELOT;
			}else if(type.equalsIgnoreCase("pig")) {
				ct = EntityType.PIG;
			}else if(type.equalsIgnoreCase("sheep")) {
				ct = EntityType.SHEEP;
			}else if(type.equalsIgnoreCase("silverfish")) {
				ct = EntityType.SILVERFISH;
			}else if(type.equalsIgnoreCase("skeleton")) {
				ct = EntityType.SKELETON;
			}else if(type.equalsIgnoreCase("slime")) {
				ct = EntityType.SLIME;
			}else if(type.equalsIgnoreCase("snowman")) {
				ct = EntityType.SNOWMAN;
			}else if(type.equalsIgnoreCase("spider")) {
				ct = EntityType.SPIDER;
			}else if(type.equalsIgnoreCase("squid")) {
				ct = EntityType.SQUID;
			}else if(type.equalsIgnoreCase("villager")) {
				ct = EntityType.VILLAGER;
			}else if(type.equalsIgnoreCase("witch")) {
				ct = EntityType.WITCH;
			}else if(type.equalsIgnoreCase("wither")) {
				ct = EntityType.WITHER;
			}else if(type.equalsIgnoreCase("wolf")) {
				ct = EntityType.WOLF;
			}else if(type.equalsIgnoreCase("zombie")) {
				ct = EntityType.ZOMBIE;
			}else if(type.equalsIgnoreCase("endermite")) {
				ct = EntityType.ENDERMITE;
			}else if(type.equalsIgnoreCase("guardian")) {
				ct = EntityType.GUARDIAN;
			}else if(type.equalsIgnoreCase("rabbit")) {
				ct = EntityType.RABBIT;
			}else if(type.equalsIgnoreCase("bunny")) {
				ct = EntityType.RABBIT;
			}
			if(ct == null) {
				try {
					ct = EntityType.valueOf(type.toUpperCase());
				}catch (Exception e) {
					
				}
			}
			if (ct == null) {
				return false;
			}
			theSpawner.setSpawnedType(ct);
			if(disabledspawnerlocs.containsKey(locationBuilder(targetBlock.getLocation()))) {
				removeDisabledSpawner(targetBlock);
			}
			return true;
		}catch (Exception e) {
			e.printStackTrace();
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
			out.close();
		}catch (Exception e) {
			// If it doesn't work, no great loss!
			//e.printStackTrace();
		}
	}
}

