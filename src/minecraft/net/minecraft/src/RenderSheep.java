package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderSheep extends RenderLiving {
	public RenderSheep(ModelBase modelBase1, ModelBase modelBase2, float f3) {
		super(modelBase1, f3);
		this.setRenderPassModel(modelBase2);
	}

	protected boolean renderFur(EntitySheep entitySheep1, int i2) {
		if (i2 == 0 && !entitySheep1.sheared) {
			this.loadTexture("/mob/sheep_fur.png");
			
			int woolColor = getWoolColorForSheep(entitySheep1);
			
			applyWoolColor(woolColor);
			
			return true;
		}
		return false;
	}

	private int getWoolColorForSheep(EntitySheep sheep) {
		try {
			java.lang.reflect.Field field = EntitySheep.class.getDeclaredField("woolColor");
			field.setAccessible(true);
			return field.getInt(sheep);
		} catch (Exception e) {
			return Block.clothWhite.blockID;
		}
	}

	private void applyWoolColor(int woolBlockID) {
		int[] woolColors = {
			0xFF0000, // red - 21
			0xFFA500, // orange - 22
			0xFFFF00, // yellow - 23
			0x7FFF00, // chartreuse - 24
			0x00FF00, // green - 25
			0x00FF7F, // spring green - 26
			0x00FFFF, // cyan - 27
			0x00BFFF, // capri - 28
			0x0000FF, // ultramarine - 29
			0x8A2BE2, // violet - 30
			0x800080, // purple - 31
			0xFF00FF, // magenta - 32
			0xFF007F, // rose - 33
			0x404040, // dark gray - 34
			0x808080, // gray - 35
			0xFFFFFF  // white - 36
		};

		int colorIndex = woolBlockID - 21; 
		if (colorIndex >= 0 && colorIndex < woolColors.length) {
			int rgb = woolColors[colorIndex];
			float r = ((rgb >> 16) & 0xFF) / 255.0F;
			float g = ((rgb >> 8) & 0xFF) / 255.0F;
			float b = (rgb & 0xFF) / 255.0F;
			
			GL11.glColor3f(r, g, b);
		} else {
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
		}
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2) {
		return this.renderFur((EntitySheep)entityLiving1, i2);
	}
}
