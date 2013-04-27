package ml.boxes.client.render.tile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ml.boxes.tile.TileEntitySafe;
import ml.core.lib.BlockLib;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.ForgeDirection;

@SideOnly(Side.CLIENT)
public class SafeTESR extends TileEntitySpecialRenderer {

	public IModelCustom sModel = AdvancedModelLoader.loadModel("/mods/Boxes/models/safe.obj");
	public static SafeTESR instance = new SafeTESR();

	@Override
	public void renderTileEntityAt(TileEntity te, double d0, double d1,
			double d2, float tickTime) {

		TileEntitySafe tes = (TileEntitySafe)te;
		if (tes.linkedDir != ForgeDirection.DOWN){
			boolean renderTall = tes.linkedDir == ForgeDirection.UP;

			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glTranslatef((float)d0, (float)d1, (float)d2);

			BlockLib.glRotateForFaceDir(tes.facing);

			GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
			bindTextureByName("/mods/Boxes/textures/models/safe.png");
			sModel.renderPart("Safe_Base");
			sModel.renderPart(renderTall ? "Safe_Tall":"Safe_Small");
			if (renderTall) sModel.renderPart("Safe_Shelf");
			
			float doorAng1 = tes.prevDoorAng + (tes.doorAng-tes.prevDoorAng)*tickTime;
			doorAng1 = (float)Math.sin(doorAng1*3.14/2);
			int doorAng = (int)(120 * doorAng1);
			
			GL11.glTranslatef(14.5F, 0F, 1.5F);
			GL11.glRotatef(-doorAng, 0F, 1F, 0F);
			GL11.glTranslatef(-14.5F, 0F, -1.5F);

			sModel.renderPart(renderTall ? "Door_Tall":"Door_Small");

			GL11.glTranslatef(5F, renderTall ? 16F:8F, 1F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			bindTextureByName("/mods/Boxes/textures/models/dials.png");
			sModel.renderPart("ComboBack");
			for (int i=0; i<3; i++){
				GL11.glPushMatrix();
				GL11.glTranslatef(-0.75F*(float)i, 0F, 0F);
				GL11.glRotatef(-36*(i+1), 1F, 0, 0);
				sModel.renderPart("Wheel_Sides");
				sModel.renderPart("Wheel_Num");
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
		}
	}

	public void renderAsItem(){
		GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
		bindTextureByName("/mods/Boxes/textures/models/safe.png");
		sModel.renderPart("Safe_Base");
		sModel.renderPart("Safe_Small");
		sModel.renderPart("Door_Small");

		GL11.glTranslatef(15F, 8F, 15F);
		bindTextureByName("/mods/Boxes/textures/models/dials.png");
		sModel.renderPart("ComboBack");
		for (int i=0; i<3; i++){
			GL11.glPushMatrix();
			GL11.glTranslatef(0.75F*(float)i, 0F, 0F);
			sModel.renderPart("Wheel_Sides");
			sModel.renderPart("Wheel_Num");
			GL11.glPopMatrix();
		}
	}

}