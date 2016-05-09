package tux2.MonsterBox;

import org.bukkit.entity.EntityType;

public enum CreatureTypes {
	Bat(65, "Bat", EntityType.BAT),
	Blaze(61, "Blaze", EntityType.BLAZE),
	CaveSpider(59, "CaveSpider", EntityType.CAVE_SPIDER),
	Chicken(93, "Chicken", EntityType.CHICKEN),
	Cow(92, "Cow", EntityType.COW),
	Creeper(50, "Creeper", EntityType.CREEPER),
	EnderDragon(63, "EnderDragon", EntityType.ENDER_DRAGON),
	Enderman(58, "Enderman", EntityType.ENDERMAN),
	Ghast(56, "Ghast", EntityType.GHAST),
	Giant(53, "Giant", EntityType.GIANT),
	IronGolem(99, "VillagerGolem", EntityType.IRON_GOLEM),
	MagmaCube(62, "LavaSlime", EntityType.MAGMA_CUBE),
	MushroomCow(96, "MushroomCow", EntityType.MUSHROOM_COW),
	Ocelot(98, "Ozelot", EntityType.OCELOT),
	Pig(90, "Pig", EntityType.PIG),
	PigZombie(57, "PigZombie", EntityType.PIG_ZOMBIE),
	Sheep(91, "Sheep", EntityType.SHEEP),
	Silverfish(60, "Silverfish", EntityType.SILVERFISH),
	Skeleton(51, "Skeleton", EntityType.SKELETON),
	Slime(55, "Slime", EntityType.SLIME),
	Snowman(97, "SnowMan", EntityType.SNOWMAN),
	Spider(52, "Spider", EntityType.SPIDER),
	Squid(94, "Squid", EntityType.SQUID),
	Villager(120, "Villager", EntityType.VILLAGER),
	Witch(66, "Witch", EntityType.WITCH),
	Wither(64, "WitherBoss", EntityType.WITHER),
	Wolf(95, "Wolf", EntityType.WOLF),
	Zombie(54, "Zombie", EntityType.ZOMBIE),
	Horse(100, "EntityHorse", EntityType.HORSE),
	Endermite(67, "Endermite", EntityType.ENDERMITE),
	Guardian(68, "Guardian", EntityType.GUARDIAN),
	Rabbit(101, "Rabbit", EntityType.RABBIT),
	Shulker(69, "Shulker", EntityType.SHULKER);
    
    public static CreatureTypes fromString(String text) {
		for (CreatureTypes m : CreatureTypes.values()) {
			if (text.equalsIgnoreCase(m.name())) {
				return m;
			}
		}
		return null;
	}
    
    public final byte id;
    public final String entityName;
    public final EntityType entity;
	CreatureTypes(int i, String entityName, EntityType entity) {
		id = (byte) i;
		this.entityName = entityName;
		this.entity = entity;
	}

}
