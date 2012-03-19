package tux2.MonsterBox;

import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutStuff {
	
	MonsterBox plugin;
	
	public SpoutStuff(MonsterBox plugin) {
		this.plugin = plugin;
	}
	
	public void createMonsterGUI(String title, boolean showprices, SpoutPlayer splayer) {
		if(plugin.usespout != null) {
			GenericPopup monsters = new GenericPopup();
			CreatureTypes[] mobs = CreatureTypes.values();
			int x = 5;
			int y = 20;
			GenericLabel label = new GenericLabel(title);
			label.setWidth(200).setHeight(20);
			label.setTextColor(new Color(0, 200, 0)); //This makes the label green.
			label.setAlign(WidgetAnchor.TOP_CENTER).setAnchor(WidgetAnchor.TOP_CENTER); //This puts the label at top center and align the text correctly.
			label.shiftYPos(5);
			monsters.attachWidget(plugin, label);
			for(CreatureTypes mob : mobs) {
				GenericButton tbutton = new GenericButton(mob.toString());
				if(showprices && plugin.hasEconomy()) {
					tbutton.setTooltip(plugin.iConomy.format(plugin.getMobPrice(mob.toString())));
				}
				tbutton.setX(x).setY(y);
				tbutton.setWidth(plugin.buttonwidth).setHeight(20);
				monsters.attachWidget(plugin, tbutton);
				y += 30;
				if(y > 180) {
					y = 20;
					x += plugin.buttonwidth + 5;
				}
			}
			GenericButton tbutton = new GenericButton("Close");
			tbutton.setX(200).setY(210);
			tbutton.setWidth(80).setHeight(20);
			monsters.attachWidget(plugin, tbutton);
			splayer.getMainScreen().attachPopupScreen(monsters);
		}
	}

}
