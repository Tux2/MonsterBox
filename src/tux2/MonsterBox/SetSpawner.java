package tux2.MonsterBox;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

public class SetSpawner implements Runnable {
	
	CreatureSpawner ts;
	CreatureType ct;

	public SetSpawner(CreatureSpawner theSpawner, CreatureType ct) {
		ts = theSpawner;
		this.ct = ct;
	}

	@Override
	public void run() {
        ts.setCreatureType(ct);
		
	}

}
