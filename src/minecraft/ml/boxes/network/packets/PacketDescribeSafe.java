package ml.boxes.network.packets;

import java.io.IOException;

import ml.boxes.Boxes;
import ml.boxes.tile.TileEntitySafe;
import ml.boxes.tile.safe.MechRegistry;
import ml.core.network.MLPacket;
import ml.core.vec.BlockCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;

public class PacketDescribeSafe extends MLPacket {
	
	public @data BlockCoord teCoord;
	public @data ForgeDirection facing;
	public @data ForgeDirection linkDir;
	
	public @data boolean sUnlocked;
	public @data String mech_id;
	public @data NBTTagCompound mechData;
	
	public PacketDescribeSafe(TileEntitySafe tes) {
		super(Boxes.netChannel);
		
		this.teCoord = new BlockCoord(tes);
		this.facing = tes.facing;
		this.linkDir = tes.linkedDir;
		
		sUnlocked = tes.unlocked;
		mech_id = tes.mech_id;
		mechData = tes.mech.writeNBTPacket(tes);
	}
	
	public PacketDescribeSafe(EntityPlayer pl, ByteArrayDataInput data) throws IOException {
		super(pl, data);
	}
	
	@Override
	public void handleClientSide(EntityPlayer epl) throws IOException {
		TileEntitySafe tes = (TileEntitySafe)teCoord.getTileEntity(epl.worldObj);
		tes.facing = facing;
		tes.linkedDir = linkDir;
		
		tes.unlocked = sUnlocked;
		tes.mech_id = mech_id;
		tes.mech = MechRegistry.getMechForId(mech_id);
		tes.mechTag = mechData;
	}
	
	public static class PacketLockSafe extends MLPacket {
		public @data BlockCoord teCoord;
		
		public PacketLockSafe(TileEntitySafe tes) {
			super("Boxes");
			this.teCoord = new BlockCoord(tes);
		}
		
		public PacketLockSafe(EntityPlayer pl, ByteArrayDataInput data) throws IOException {
			super(pl, data);
		}
		
		@Override
		public void handleServerSide(EntityPlayer epl) throws IOException {
			((TileEntitySafe)teCoord.getTileEntity(epl.worldObj)).lock();
		}
	}
}
