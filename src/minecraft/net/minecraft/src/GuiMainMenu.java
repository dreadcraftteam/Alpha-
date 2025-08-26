package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GuiMainMenu extends GuiScreen {
	private static final Random rand = new Random();
	String[] logoBlockLayers = new String[]{" *   * * *   * *** *** *** *** *** ***", " ** ** * **  * *   *   * * * * *    * ", " * * * * * * * **  *   **  *** **   * ", " *   * * *  ** *   *   * * * * *    * ", " *   * * *   * *** *** * * * * *    * "};
	private LogoEffectRandomizer[][] logoEffects;
	private float updateCounter = 0.0F;
	private String splashString = "";

	public GuiMainMenu() {
		try {
			ArrayList arrayList1 = new ArrayList();
			BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(GuiMainMenu.class.getResourceAsStream("/title/splashes.txt")));
			String string3 = "";

			while((string3 = bufferedReader2.readLine()) != null) {
				string3 = string3.trim();
				if(string3.length() > 0) {
					arrayList1.add(string3);
				}
			}

			this.splashString = (String)arrayList1.get(rand.nextInt(arrayList1.size()));
		} catch (Exception exception4) {
		}

	}

	public void updateScreen() {
		++this.updateCounter;
		if(this.logoEffects != null) {
			for(int i1 = 0; i1 < this.logoEffects.length; ++i1) {
				for(int i2 = 0; i2 < this.logoEffects[i1].length; ++i2) {
					this.logoEffects[i1][i2].updateLogoEffects();
				}
			}
		}

	}

	protected void keyTyped(char c1, int i2) {
	}

	public void initGui() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(new Date());
		if(calendar1.get(2) + 1 == 11 && calendar1.get(5) == 9) {
			this.splashString = "Happy birthday, ez!";
		} else if(calendar1.get(2) + 1 == 6 && calendar1.get(5) == 1) {
			this.splashString = "Happy birthday, Notch!";
		} else if(calendar1.get(2) + 1 == 12 && calendar1.get(5) == 24) {
			this.splashString = "Merry X-mas!";
		} else if(calendar1.get(2) + 1 == 1 && calendar1.get(5) == 1) {
			this.splashString = "Happy new year!";
		}

		this.controlList.clear();
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 48, "Singleplayer"));
		this.controlList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 72, "Multiplayer"));
		this.controlList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 96, "Play tutorial level"));
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, "Options..."));
		((GuiButton)this.controlList.get(1)).enabled = false;
		((GuiButton)this.controlList.get(2)).enabled = false;
		if(this.mc.session == null) {
			((GuiButton)this.controlList.get(1)).enabled = true;
		}

	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.options));
		}

		if(guiButton1.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if(guiButton1.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		Tessellator tessellator4 = Tessellator.instance;
		this.drawLogo(f3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/logo.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tessellator4.setColorOpaque_I(0xFFFFFF);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
		GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		float f5 = 1.8F - MathHelper.abs(MathHelper.sin((float)(System.currentTimeMillis() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
		f5 = f5 * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashString) + 32);
		GL11.glScalef(f5, f5, f5);
		this.drawCenteredString(this.fontRenderer, this.splashString, 0, -8, 16776960);
		GL11.glPopMatrix();
		super.drawScreen(i1, i2, f3);
	}

	private void drawLogo(float renderPartialTick) {
		int i3;
		if(this.logoEffects == null) {
			this.logoEffects = new LogoEffectRandomizer[this.logoBlockLayers[0].length()][this.logoBlockLayers.length];

			for(int i2 = 0; i2 < this.logoEffects.length; ++i2) {
				for(i3 = 0; i3 < this.logoEffects[i2].length; ++i3) {
					this.logoEffects[i2][i3] = new LogoEffectRandomizer(this, i2, i3);
				}
			}
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		ScaledResolution scaledResolution14 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		i3 = 120 * scaledResolution14.scaleFactor;
		GLU.gluPerspective(70.0F, (float)this.mc.displayWidth / (float)i3, 0.05F, 100.0F);
		GL11.glViewport(0, this.mc.displayHeight - i3, this.mc.displayWidth, i3);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);

		for(int i4 = 0; i4 < 3; ++i4) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.4F, 0.6F, -12.0F);
			if(i4 == 0) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glTranslatef(0.0F, -0.4F, 0.0F);
				GL11.glScalef(0.98F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if(i4 == 1) {
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			}

			if(i4 == 2) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(0.89F, 1.0F, 0.4F);
			GL11.glTranslatef((float)(-this.logoBlockLayers[0].length()) * 0.5F, (float)(-this.logoBlockLayers.length) * 0.5F, 0.0F);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			if(i4 == 0) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/title/black.png"));
			}

			RenderBlocks renderBlocks5 = new RenderBlocks();

			for(int i6 = 0; i6 < this.logoBlockLayers.length; ++i6) {
				for(int i7 = 0; i7 < this.logoBlockLayers[i6].length(); ++i7) {
					char c8 = this.logoBlockLayers[i6].charAt(i7);
					if(c8 != 32) {
						GL11.glPushMatrix();
						LogoEffectRandomizer logoEffectRandomizer9 = this.logoEffects[i7][i6];
						float f10 = (float)(logoEffectRandomizer9.prevHeight + (logoEffectRandomizer9.height - logoEffectRandomizer9.prevHeight) * (double)renderPartialTick);
						float f11 = 1.0F;
						float f12 = 1.0F;
						float f13 = 0.0F;
						if(i4 == 0) {
							f11 = f10 * 0.04F + 1.0F;
							f12 = 1.0F / f11;
							f10 = 0.0F;
						}

						GL11.glTranslatef((float)i7, (float)i6, f10);
						GL11.glScalef(f11, f11, f11);
						GL11.glRotatef(f13, 0.0F, 1.0F, 0.0F);
						renderBlocks5.renderBlockAsItem(Block.stone, f12);
						GL11.glPopMatrix();
					}
				}
			}

			GL11.glPopMatrix();
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	static Random getRandom() {
		return rand;
	}
}
