package ml.boxes;

import net.minecraftforge.common.Configuration;
import ml.core.Config;

public class BoxesConfig extends Config {

	public @prop(category=Configuration.CATEGORY_BLOCK) Integer boxBlockID = 540;
	public @prop(category=Configuration.CATEGORY_ITEM) Integer cardboardItemID = 3000;
	
	public @prop(comment="Set to true to require the use of the Shift key to show the content tip") boolean shiftForTip = false;
	public @prop(comment="The number of Milliseconds that you need to hover over a box item befor it shows its contents tip") Integer tipReactionTime = 200;

	@Override
	public String getFailMsg() {
		return "Boxes config load error";
	}
}
