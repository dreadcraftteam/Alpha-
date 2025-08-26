package net.minecraft.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiIngame extends Gui {
	private static RenderItem itemRenderer = new RenderItem();
	private List chatMessageList = new ArrayList();
	private Random rand = new Random();
	private Minecraft mc;
	public String testMessage = null;
	private int updateCounter = 0;
	private String recordPlaying = "";
	private int recordPlayingUpFor = 0;
	public float damageGuiPartialTime;
	float prevVignetteBrightness = 1.0F;

	public GuiIngame(Minecraft minecraft) {
		this.mc = minecraft;
	}

	public void renderGameOverlay(float renderPartialTick, boolean hasScreen, int width, int height) {
		ScaledResolution scaledResolution5 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int i6 = scaledResolution5.getScaledWidth();
		int i7 = scaledResolution5.getScaledHeight();
		FontRenderer fontRenderer8 = this.mc.fontRenderer;
		this.mc.entityRenderer.setupOverlayRendering();
		GL11.glEnable(GL11.GL_BLEND);
		if(this.mc.options.fancyGraphics) {
			this.renderVignette(this.mc.thePlayer.getBrightness(renderPartialTick), i6, i7);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/gui.png"));
		InventoryPlayer inventoryPlayer9 = this.mc.thePlayer.inventory;
		this.zLevel = -90.0F;
		this.drawTexturedModalRect(i6 / 2 - 91, i7 - 22, 0, 0, 182, 22);
		this.drawTexturedModalRect(i6 / 2 - 91 - 1 + inventoryPlayer9.currentItem * 20, i7 - 22 - 1, 0, 22, 24, 22);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
		this.drawTexturedModalRect(i6 / 2 - 7, i7 / 2 - 7, 0, 0, 16, 16);
		GL11.glDisable(GL11.GL_BLEND);
		boolean z10 = this.mc.thePlayer.heartsLife / 3 % 2 == 1;
		if(this.mc.thePlayer.heartsLife < 10) {
			z10 = false;
		}

		int i11 = this.mc.thePlayer.health;
		int i12 = this.mc.thePlayer.prevHealth;
		this.rand.setSeed((long)(this.updateCounter * 312871));
		int i13;
		int i14;
		int i15;
		if(this.mc.playerController.shouldDrawHUD()) {
			i13 = this.mc.thePlayer.getPlayerArmorValue();

			int i16;
			for(i14 = 0; i14 < 10; ++i14) {
				i15 = i7 - 32;
				if(i13 > 0) {
					i16 = i6 / 2 + 91 - i14 * 8 - 9;
					if(i14 * 2 + 1 < i13) {
						this.drawTexturedModalRect(i16, i15, 34, 9, 9, 9);
					}

					if(i14 * 2 + 1 == i13) {
						this.drawTexturedModalRect(i16, i15, 25, 9, 9, 9);
					}

					if(i14 * 2 + 1 > i13) {
						this.drawTexturedModalRect(i16, i15, 16, 9, 9, 9);
					}
				}

				byte b25 = 0;
				if(z10) {
					b25 = 1;
				}

				int i17 = i6 / 2 - 91 + i14 * 8;
				if(i11 <= 4) {
					i15 += this.rand.nextInt(2);
				}

				this.drawTexturedModalRect(i17, i15, 16 + b25 * 9, 0, 9, 9);
				if(z10) {
					if(i14 * 2 + 1 < i12) {
						this.drawTexturedModalRect(i17, i15, 70, 0, 9, 9);
					}

					if(i14 * 2 + 1 == i12) {
						this.drawTexturedModalRect(i17, i15, 79, 0, 9, 9);
					}
				}

				if(i14 * 2 + 1 < i11) {
					this.drawTexturedModalRect(i17, i15, 52, 0, 9, 9);
				}

				if(i14 * 2 + 1 == i11) {
					this.drawTexturedModalRect(i17, i15, 61, 0, 9, 9);
				}
			}

			if(this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
				i14 = (int)Math.ceil((double)(this.mc.thePlayer.air - 2) * 10.0D / 300.0D);
				i15 = (int)Math.ceil((double)this.mc.thePlayer.air * 10.0D / 300.0D) - i14;

				for(i16 = 0; i16 < i14 + i15; ++i16) {
					if(i16 < i14) {
						this.drawTexturedModalRect(i6 / 2 - 91 + i16 * 8, i7 - 32 - 9, 16, 18, 9, 9);
					} else {
						this.drawTexturedModalRect(i6 / 2 - 91 + i16 * 8, i7 - 32 - 9, 25, 18, 9, 9);
					}
				}
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();

		for(i13 = 0; i13 < 9; ++i13) {
			i14 = i6 / 2 - 90 + i13 * 20 + 2;
			i15 = i7 - 16 - 3;
			this.renderInventorySlot(i13, i14, i15, renderPartialTick);
		}

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		String string21;
		if(this.mc.options.d) {
			fontRenderer8.drawStringWithShadow("Minecraft Alpha+ v1.0.0 (" + this.mc.debug + ")", 2, 2, 0xFFFFFF);
			fontRenderer8.drawStringWithShadow(this.mc.debugInfoRenders(), 2, 12, 0xFFFFFF);
			fontRenderer8.drawStringWithShadow(this.mc.getEntityDebug(), 2, 22, 0xFFFFFF);
			fontRenderer8.drawStringWithShadow(this.mc.debugInfoEntities(), 2, 32, 0xFFFFFF);
			long j22 = Runtime.getRuntime().maxMemory();
			long j27 = Runtime.getRuntime().totalMemory();
			long j28 = Runtime.getRuntime().freeMemory();
			long j19 = j27 - j28;
			string21 = "Used memory: " + j19 * 100L / j22 + "% (" + j19 / 1024L / 1024L + "MB) of " + j22 / 1024L / 1024L + "MB";
			this.drawString(fontRenderer8, string21, i6 - fontRenderer8.getStringWidth(string21) - 2, 2, 14737632);
			string21 = "Allocated memory: " + j27 * 100L / j22 + "% (" + j27 / 1024L / 1024L + "MB)";
			this.drawString(fontRenderer8, string21, i6 - fontRenderer8.getStringWidth(string21) - 2, 12, 14737632);
		} else {
			fontRenderer8.drawStringWithShadow("Minecraft Alpha+ v1.0.0", 2, 2, 0xFFFFFF);
		}

		if(this.recordPlayingUpFor > 0) {
			float f23 = (float)this.recordPlayingUpFor - renderPartialTick;
			i14 = (int)(f23 * 256.0F / 20.0F);
			if(i14 > 255) {
				i14 = 255;
			}

			if(i14 > 0) {
				GL11.glPushMatrix();
				GL11.glTranslatef((float)(i6 / 2), (float)(i7 - 48), 0.0F);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				i15 = Color.HSBtoRGB(f23 / 50.0F, 0.7F, 0.6F) & 0xFFFFFF;
				fontRenderer8.drawString(this.recordPlaying, -fontRenderer8.getStringWidth(this.recordPlaying) / 2, -4, i15 + (i14 << 24));
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}
		}

		byte b24 = 10;
		boolean z26 = false;
		if(this.mc.currentScreen instanceof GuiChat) {
			b24 = 20;
			z26 = true;
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, (float)(i7 - 48), 0.0F);

		for(i15 = 0; i15 < this.chatMessageList.size() && i15 < b24; ++i15) {
			if(((ChatLine)this.chatMessageList.get(i15)).updateCounter < 200 || z26) {
				double d29 = (double)((ChatLine)this.chatMessageList.get(i15)).updateCounter / 200.0D;
				d29 = 1.0D - d29;
				d29 *= 10.0D;
				if(d29 < 0.0D) {
					d29 = 0.0D;
				}

				if(d29 > 1.0D) {
					d29 = 1.0D;
				}

				d29 *= d29;
				int i18 = (int)(255.0D * d29);
				if(z26) {
					i18 = 255;
				}

				if(i18 > 0) {
					byte b30 = 2;
					int i20 = -i15 * 9;
					string21 = ((ChatLine)this.chatMessageList.get(i15)).message;
					this.drawRect(b30, i20 - 1, b30 + 320, i20 + 8, i18 / 2 << 24);
					GL11.glEnable(GL11.GL_BLEND);
					fontRenderer8.drawStringWithShadow(string21, b30, i20, 0xFFFFFF + (i18 << 24));
				}
			}
		}

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderVignette(float brightness, int width, int height) {
		brightness = 1.0F - brightness;
		if(brightness < 0.0F) {
			brightness = 0.0F;
		}

		if(brightness > 1.0F) {
			brightness = 1.0F;
		}

		this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(brightness - this.prevVignetteBrightness) * 0.01D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
		GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/misc/vignette.png"));
		Tessellator tessellator4 = Tessellator.instance;
		tessellator4.startDrawingQuads();
		tessellator4.addVertexWithUV(0.0D, (double)height, -90.0D, 0.0D, 1.0D);
		tessellator4.addVertexWithUV((double)width, (double)height, -90.0D, 1.0D, 1.0D);
		tessellator4.addVertexWithUV((double)width, 0.0D, -90.0D, 1.0D, 0.0D);
		tessellator4.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tessellator4.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void renderInventorySlot(int i1, int i2, int i3, float renderPartialTick) {
		ItemStack itemStack5 = this.mc.thePlayer.inventory.mainInventory[i1];
		if(itemStack5 != null) {
			float f6 = (float)itemStack5.animationsToGo - renderPartialTick;
			if(f6 > 0.0F) {
				GL11.glPushMatrix();
				float f7 = 1.0F + f6 / 5.0F;
				GL11.glTranslatef((float)(i2 + 8), (float)(i3 + 12), 0.0F);
				GL11.glScalef(1.0F / f7, (f7 + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef((float)(-(i2 + 8)), (float)(-(i3 + 12)), 0.0F);
			}

			itemRenderer.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemStack5, i2, i3);
			if(f6 > 0.0F) {
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemStack5, i2, i3);
		}
	}

	public void updateTick() {
		if(this.recordPlayingUpFor > 0) {
			--this.recordPlayingUpFor;
		}

		++this.updateCounter;

		for(int i1 = 0; i1 < this.chatMessageList.size(); ++i1) {
			++((ChatLine)this.chatMessageList.get(i1)).updateCounter;
		}

	}

	public void addChatMessage(String message) {
		while(this.mc.fontRenderer.getStringWidth(message) > 320) {
			int i2;
			for(i2 = 1; i2 < message.length() && this.mc.fontRenderer.getStringWidth(message.substring(0, i2 + 1)) <= 320; ++i2) {
			}

			this.addChatMessage(message.substring(0, i2));
			message = message.substring(i2);
		}

		this.chatMessageList.add(0, new ChatLine(message));

		while(this.chatMessageList.size() > 50) {
			this.chatMessageList.remove(this.chatMessageList.size() - 1);
		}

	}

	public void setRecordPlayingMessage(String record) {
		this.recordPlaying = "Now playing: " + record;
		this.recordPlayingUpFor = 60;
	}
}
