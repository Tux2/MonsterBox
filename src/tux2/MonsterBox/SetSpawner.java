package tux2.MonsterBox;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class SetSpawner implements Runnable {
	
	CreatureSpawner ts;
	CreatureTypes ct;

	public SetSpawner(CreatureSpawner theSpawner, CreatureTypes ct) {
		ts = theSpawner;
		this.ct = ct;
	}

	@Override
	public void run() {
        ts.setSpawnedType(EntityType.valueOf(ct.toString()));
		
	}

}
