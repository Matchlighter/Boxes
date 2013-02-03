package ml.boxes.client;

import java.util.EnumSet;

import ml.boxes.BoxData;
import ml.boxes.Boxes;
import ml.boxes.Lib;
import ml.boxes.Lib.XYPair;
import ml.boxes.item.ItemBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.inventory.Slot;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ContentTipHandler implements ITickHandler {
	
	public static ContentTip currentTip;
	
	private Slot tickerSlot;
	private long tickerTime = 0;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.RENDER)){
			if (!Boxes.neiInstalled) //NEI Provides a better place for doing this. Use it if we can
				renderContentTip((Float)tickData[0]);
		}else if (type.contains(TickType.CLIENT)){
			updateCurrentTip();
		}
	}
	
	// Do calculations for the tip
	private void updateCurrentTip(){
		Minecraft mc = FMLClientHandler.instance().getClient();
		if (mc.currentScreen instanceof GuiContainer){
			GuiContainer asGuiContainer = (GuiContainer)mc.currentScreen;
			
			int guiXSize = ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, (GuiContainer)mc.currentScreen, 1);
			int guiYSize = ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, (GuiContainer)mc.currentScreen, 2);
			
			ScaledResolution var13 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            int var14 = var13.getScaledWidth();
            int var15 = var13.getScaledHeight();
            int adjMouseX = Mouse.getX() * var14 / mc.displayWidth;
            int adjMouseY = var15 - Mouse.getY() * var15 / mc.displayHeight - 1;

            int guiLeft = (asGuiContainer.width - guiXSize) / 2;
            int guiTop = (asGuiContainer.height - guiYSize) / 2;

            if (currentTip == null || !currentTip.StillValid(adjMouseX, adjMouseY)){
            	currentTip = null;
            	boolean inASlot = false;
            	for (Object slt : asGuiContainer.inventorySlots.inventorySlots){
            		Slot asSlot = (Slot)slt;
            		
            		if (Lib.pointInRect(adjMouseX-guiLeft, adjMouseY-guiTop, asSlot.xDisplayPosition, asSlot.yDisplayPosition, 16, 16)){
            			inASlot = true;
            			if (tickerSlot != asSlot){
	            			tickerSlot=asSlot;
	            			tickerTime = mc.getSystemTime();
            			}
            			
            			if (asSlot.getHasStack() &&
                				(asSlot.getStack().getItem() instanceof ItemBox) &&
                				//!(asGuiContainer instanceof GuiBox && false) && // TODO Make sure the tip will not be for the open box
                				(!Boxes.shiftForTip || asGuiContainer.isShiftKeyDown()) &&
                				(mc.getSystemTime() - tickerTime > Boxes.tipReactionTime || Boxes.shiftForTip)
                				
                				)
                		{
            				currentTip = new ContentTip(asGuiContainer, asSlot, guiTop, guiLeft);
                		}
            		}
            	}
            	if (!inASlot)
            		tickerSlot = null;
            }
            if (currentTip != null)
            	currentTip.tick(mc, adjMouseX, adjMouseY);
		}
	}
	
	//Render the tip
	private void renderContentTip(float tickTime){
		Minecraft mc = FMLClientHandler.instance().getClient();
		if (mc.currentScreen instanceof GuiContainer){
			
			ScaledResolution var13 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            int var14 = var13.getScaledWidth();
            int var15 = var13.getScaledHeight();
            int adjMouseX = Mouse.getX() * var14 / mc.displayWidth;
            int adjMouseY = var15 - Mouse.getY() * var15 / mc.displayHeight - 1;

            if (currentTip!= null)
            	currentTip.doRender(mc, adjMouseX, adjMouseY, tickTime);
		}
	}
	
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "Boxes";
	}

}