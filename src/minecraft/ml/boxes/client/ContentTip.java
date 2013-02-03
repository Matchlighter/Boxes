package ml.boxes.client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ml.boxes.BoxData;
import ml.boxes.Boxes;
import ml.boxes.Lib;
import ml.boxes.Lib.XYPair;
import ml.boxes.item.ItemBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ContentTip extends Gui {
		
	public final GuiContainer gui;
	public final Slot slot;
	public final int guiTop;
	public final int guiLeft;

	private XYPair position = new XYPair(0, 0);
	private XYPair currentSize = new XYPair(0, 0);
	private XYPair gridSize = new XYPair(0,0);
	private int slotSize = 16;
	private List<ItemStack> activeStacks = new ArrayList<ItemStack>();
	private boolean resizing = false;
	private boolean inInteractMode = false; //Better for keeping things in sync than calling interactable() from doRender() (doRender() may come before tick(), so it would render the background slot before resizing is set)
	
	public ContentTip(GuiContainer gui, Slot slot, int gTop, int gLeft) {
		this.gui = gui;
		this.slot = slot;
		this.guiTop = gTop;
		this.guiLeft = gLeft;
	}
		
	public void doRender(Minecraft mc, int mX, int mY, float tickTime){
		RenderEngine re = mc.renderEngine;
		
		int tex = re.getTexture("/ml/boxes/gfx/contentTipGui2.png");
		re.bindTexture(tex);
		
		int amX = mX - position.X;
		int amY = mY - position.Y;
		
		zLevel = 300F;
		//itemRenderer.zLevel = 300F;
		
		GL11.glPushMatrix();
		GL11.glTranslatef(position.X, position.Y, 0);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		drawTexturedModalRect(0, 0, 0, 0, currentSize.X-9, currentSize.Y-7);
		drawTexturedModalRect(7, 0, 178-(currentSize.X-7), 0, currentSize.X-7, currentSize.Y-7);
		drawTexturedModalRect(7, 9, 178-(currentSize.X-7), 106-(currentSize.Y-9), currentSize.X-7, currentSize.Y-9);
		drawTexturedModalRect(0, 9, 0, 106-(currentSize.Y-9), currentSize.X-9, currentSize.Y-9);
		
		if (!resizing){
			int numStacks = activeStacks.size();
			for (int i=0; i< activeStacks.size(); i++){
				int col = i%gridSize.X;
				int row = i/gridSize.X;
				
				int slotX = 8+col*18;
				int slotY = 10+row*18;
				
				re.bindTexture(tex);
				if (inInteractMode)
					drawTexturedModalRect(slotX-1, slotY-1, 0, 106, 18, 18);
				
				if (activeStacks.get(i) != null){
					RenderUtils.drawStackAt(mc, slotX, slotY, activeStacks.get(i));
				}
				
				GL11.glDisable(GL11.GL_LIGHTING); //Will affect the next iteration as well (this is good)
				if (inInteractMode && Lib.pointInRect(amX, amY, slotX, slotY, 16, 16)){
					this.drawGradientRect(slotX, slotY, slotX + 16, slotY + 16, -2130706433, -2130706433);
				}
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		
		//gui.manager.renderToolTips(mX+guiLeft, mY+guiTop);
		
		//itemRenderer.zLevel = 0F;
		zLevel = 0F;
	}
	
	public void tick(Minecraft mc, int mX, int mY){
		BoxData box = ItemBox.getDataFromIS(slot.getStack());
		
		activeStacks.clear();
		if (!interactable()){
			inInteractMode = false;
			activeStacks.addAll(box.getContainedItemStacks());
		} else {
			inInteractMode = true;
			for (int i=0; i<box.getSizeInventory(); i++){
				activeStacks.add(i, box.getStackInSlot(i));
			}
		}
		
		gridSize = Lib.determineBestGrid(activeStacks.size());
		resizing = false;
		int targX = gridSize.X*(slotSize+2) +16;
		int targY = gridSize.Y*(slotSize+2) +16;
		
		if (targX != currentSize.X || targY != currentSize.Y){
			resizing = true;
			if (targX > currentSize.X){
				currentSize.X += 16;
				if (targX < currentSize.X)
					currentSize.X = targX;
			} else if (targX < currentSize.X) {
				currentSize.X -= 16;
				if (targX > currentSize.X)
					currentSize.X = targX;
			}
			
			if (targY > currentSize.Y){
				currentSize.Y += 16;
				if (targY < currentSize.Y)
					currentSize.Y = targY;
			} else if (targY < currentSize.Y) {
				currentSize.Y -= 16;
				if (targY > currentSize.Y)
					currentSize.Y = targY;
			}
		}
				
		position.X = guiLeft + slot.xDisplayPosition + (slotSize-currentSize.X)/2;
		position.Y = guiTop + slot.yDisplayPosition - currentSize.Y;
		
	}
	
	private boolean interactable(){
		return (Boxes.neiInstalled && gui.isShiftKeyDown() && slot.inventory instanceof InventoryPlayer);
	}
	
	public boolean pointInTip(int px, int py){
		return Lib.pointInRect(px, py, position.X, position.Y, currentSize.X, currentSize.Y);
	}
	
	public boolean StillValid(int mX, int mY){
		return ((Lib.pointInRect(mX-guiLeft, mY-guiTop, slot.xDisplayPosition, slot.yDisplayPosition, 16, 16) || 
				(interactable() && pointInTip(mX, mY))) &&
				slot.getHasStack() // TODO Add better checking to ensure that it is the same box.
				);
	}
	
	public int getSlotAtPosition(int pX, int pY){
		if (inInteractMode  && !resizing){
			int numStacks = activeStacks.size();
			for (int i=0; i< activeStacks.size(); i++){
				int col = i%gridSize.X;
				int row = i/gridSize.X;
				
				int slotX = 8+col*18 + position.X;
				int slotY = 10+row*18 + position.Y;
				
				if (Lib.pointInRect(pX, pY, slotX, slotY, 16, 16)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public ItemStack getStackAtPosition(int pX, int pY){
		int slNum = getSlotAtPosition(pX, pY);
		if (slNum>=0 && slNum< activeStacks.size()){
			return activeStacks.get(slNum);
		}
		return null;
	}
}