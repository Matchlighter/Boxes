package ml.boxes.tile.safe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ml.boxes.api.safe.ISafe;
import ml.boxes.api.safe.SafeMechanism;
import ml.core.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;

/**
 * A fall-back mechanism that will be loaded if a safe fails to save or load properly.
 * Shouldn't ever happen, but neither should NPEs
 * 
 * @author Matchlighter
 */
public class MechFallback extends SafeMechanism {
	
	@Override
	public String getMechId() {
		return "fallback";
	}
	
	@Override
	public String getUnlocalizedMechName() {
		return "item.mechanism.invalid";
	}
	
	@Override
	public String getLocalizedName() {
		return ChatUtils.color_darkRed + ChatUtils.italic + super.getLocalizedName();
	}

	@Override
	public void beginUnlock(ISafe safe, EntityPlayer epl) {
		epl.sendChatToPlayer(ChatMessageComponent.createFromText("\u00A77\u00A7oWarning: The safe has been corrupted and can no longer be locked properly!"));
		safe.doUnlock();
	}

	@Override
	public boolean canConnectWith(ISafe self, ISafe remote) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(ISafe safe, RenderPass pass, boolean stacked) {
		
	}

	@Override
	public NBTTagCompound writeNBTPacket(ISafe safe) {
		// TODO Auto-generated method stub
		return null;
	}
}
