package net.minecraft.src;

public class GuiIngameMenu extends GuiScreen {
	private int updateCounter2 = 0;
	private int updateCounter = 0;

	public void initGui() {
		this.updateCounter2 = 0;
		this.controlList.clear();
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 48, "Save and quit to title"));
		if(this.mc.isMultiplayerWorld()) {
			((GuiButton)this.controlList.get(0)).displayString = "Disconnect";
		}

		this.controlList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24, "Back to game"));
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96, "Options..."));
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.options));
		}

		if(guiButton1.id == 1) {
			if(this.mc.isMultiplayerWorld()) {
				this.mc.theWorld.sendQuittingDisconnectingPacket();
			}

			this.mc.changeWorld1((World)null);
			this.mc.displayGuiScreen(new GuiMainMenu());
		}

		if(guiButton1.id == 4) {
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
		}

	}

	public void updateScreen() {
		super.updateScreen();
		++this.updateCounter;
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		boolean z4 = !this.mc.theWorld.saveWorld(this.updateCounter2++);
		if(z4 || this.updateCounter < 20) {
			float f5 = ((float)(this.updateCounter % 10) + f3) / 10.0F;
			f5 = MathHelper.sin(f5 * (float)Math.PI * 2.0F) * 0.2F + 0.8F;
			int i6 = (int)(255.0F * f5);
			this.drawString(this.fontRenderer, "Saving level..", 8, this.height - 16, i6 << 16 | i6 << 8 | i6);
		}

		this.drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
	}
}
