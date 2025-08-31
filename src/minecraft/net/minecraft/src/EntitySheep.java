package net.minecraft.src;

public class EntitySheep extends EntityAnimal {
	public boolean sheared = false;
	private int woolColor; 

	public EntitySheep(World world1) {
		super(world1);
		this.texture = "/mob/sheep.png";
		this.setSize(0.9F, 1.3F);
		this.woolColor = this.getRandomWoolColor();
	}

	private int getRandomWoolColor() {
		int[] woolColors = {
			21, 22, 23, 24, 25, 26, 27, 28, 
			29, 30, 31, 32, 33, 34, 35, 36
		};
		return woolColors[this.rand.nextInt(woolColors.length)];
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(!this.sheared && entity1 instanceof EntityLiving) {
			this.sheared = true;
			int i3 = 1 + this.rand.nextInt(3);

			for(int i4 = 0; i4 < i3; ++i4) {
				EntityItem entityItem5 = this.entityDropItem(this.woolColor, 1, 1.0F);
				entityItem5.motionY += (double)(this.rand.nextFloat() * 0.05F);
				entityItem5.motionX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				entityItem5.motionZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
			}
		}

		return super.attackEntityFrom(entity1, i2);
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("Sheared", this.sheared);
		nBTTagCompound1.setInteger("Color", this.woolColor); 
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.sheared = nBTTagCompound1.getBoolean("Sheared");

		if(nBTTagCompound1.hasKey("Color")) {
			this.woolColor = nBTTagCompound1.getInteger("Color");
		} else {
			this.woolColor = this.getRandomWoolColor();
		}
	}

	protected String getLivingSound() {
		return "mob.sheep";
	}

	protected String getHurtSound() {
		return "mob.sheep";
	}

	protected String getDeathSound() {
		return "mob.sheep";
	}
}
