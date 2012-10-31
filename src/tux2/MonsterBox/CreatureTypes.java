package tux2.MonsterBox;


public enum CreatureTypes {
	Bat(65),
	Blaze(61),
	CaveSpider(59),
	Chicken(93),
	Cow(92),
	Creeper(50),
	EnderDragon(63),
	Enderman(58),
	Ghast(56),
	Giant(53),
	IronGolem(99),
	MagmaCube(62),
	MushroomCow(96),
	Ocelot(98),
	Pig(90),
	PigZombie(57),
	Sheep(91),
	Silverfish(60),
	Skeleton(51),
	Slime(55),
	Snowman(97),
	Spider(52),
	Squid(94),
	Villager(120),
	Witch(66),
	Wither(64),
	Wolf(95),
	Zombie(54);
    
    public static CreatureTypes fromString(String text) {
		for (CreatureTypes m : CreatureTypes.values()) {
			if (text.equalsIgnoreCase(m.name())) {
				return m;
			}
		}
		return null;
	}
    
    public final byte id;
	CreatureTypes(int i) {
		id = (byte) i;
	}

}
