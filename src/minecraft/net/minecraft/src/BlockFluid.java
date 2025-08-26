package net.minecraft.src;

import java.util.Random;

public abstract class BlockFluid extends Block {
	protected int fluidType = 1;

	protected BlockFluid(int i1, Material material2) {
		super(i1, (material2 == Material.lava ? 14 : 12) * 16 + 13, material2);
		float f3 = 0.0F;
		float f4 = 0.0F;
		if(material2 == Material.lava) {
			this.fluidType = 2;
		}

		this.setBlockBounds(0.0F + f4, 0.0F + f3, 0.0F + f4, 1.0F + f4, 1.0F + f3, 1.0F + f4);
		this.setTickOnLoad(true);
	}

	public static float getFluidHeightPercent(int i0) {
		if(i0 >= 8) {
			i0 = 0;
		}

		float f1 = (float)(i0 + 1) / 9.0F;
		return f1;
	}

	public int getBlockTextureFromSide(int side) {
		return side != 0 && side != 1 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture;
	}

	protected int getFlowDecay(World worldObj, int x, int y, int z) {
		return worldObj.getBlockMaterial(x, y, z) != this.material ? -1 : worldObj.getBlockMetadata(x, y, z);
	}

	protected int getEffectiveFlowDecay(IBlockAccess blockAccess, int x, int y, int z) {
		if(blockAccess.getBlockMaterial(x, y, z) != this.material) {
			return -1;
		} else {
			int i5 = blockAccess.getBlockMetadata(x, y, z);
			if(i5 >= 8) {
				i5 = 0;
			}

			return i5;
		}
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean canCollideCheck(int metadata, boolean z2) {
		return z2 && metadata == 0;
	}

	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		Material material6 = blockAccess.getBlockMaterial(x, y, z);
		return material6 == this.material ? false : (material6 == Material.ice ? false : (side == 1 ? true : super.shouldSideBeRendered(blockAccess, x, y, z, side)));
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldObj, int x, int y, int z) {
		return null;
	}

	public int getRenderType() {
		return 4;
	}

	public int idDropped(int metadata, Random rand) {
		return 0;
	}

	public int quantityDropped(Random rand) {
		return 0;
	}

	private Vec3D getFlowVector(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		Vec3D vec3D5 = Vec3D.createVector(0.0D, 0.0D, 0.0D);
		int i6 = this.getEffectiveFlowDecay(iBlockAccess1, i2, i3, i4);

		for(int i7 = 0; i7 < 4; ++i7) {
			int i8 = i2;
			int i10 = i4;
			if(i7 == 0) {
				i8 = i2 - 1;
			}

			if(i7 == 1) {
				i10 = i4 - 1;
			}

			if(i7 == 2) {
				++i8;
			}

			if(i7 == 3) {
				++i10;
			}

			int i11 = this.getEffectiveFlowDecay(iBlockAccess1, i8, i3, i10);
			int i12;
			if(i11 < 0) {
				if(!iBlockAccess1.getBlockMaterial(i8, i3, i10).getIsSolid()) {
					i11 = this.getEffectiveFlowDecay(iBlockAccess1, i8, i3 - 1, i10);
					if(i11 >= 0) {
						i12 = i11 - (i6 - 8);
						vec3D5 = vec3D5.addVector((double)((i8 - i2) * i12), (double)((i3 - i3) * i12), (double)((i10 - i4) * i12));
					}
				}
			} else if(i11 >= 0) {
				i12 = i11 - i6;
				vec3D5 = vec3D5.addVector((double)((i8 - i2) * i12), (double)((i3 - i3) * i12), (double)((i10 - i4) * i12));
			}
		}

		if(iBlockAccess1.getBlockMetadata(i2, i3, i4) >= 8) {
			boolean z13 = false;
			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2, i3, i4 - 1, 2)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2, i3, i4 + 1, 3)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2 - 1, i3, i4, 4)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2 + 1, i3, i4, 5)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2, i3 + 1, i4 - 1, 2)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2, i3 + 1, i4 + 1, 3)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2 - 1, i3 + 1, i4, 4)) {
				z13 = true;
			}

			if(z13 || this.shouldSideBeRendered(iBlockAccess1, i2 + 1, i3 + 1, i4, 5)) {
				z13 = true;
			}

			if(z13) {
				vec3D5 = vec3D5.normalize().addVector(0.0D, -6.0D, 0.0D);
			}
		}

		vec3D5 = vec3D5.normalize();
		return vec3D5;
	}

	public void velocityToAddToEntity(World worldObj, int x, int y, int z, Entity entity, Vec3D velocityVector) {
		Vec3D vec3D7 = this.getFlowVector(worldObj, x, y, z);
		velocityVector.xCoord += vec3D7.xCoord;
		velocityVector.yCoord += vec3D7.yCoord;
		velocityVector.zCoord += vec3D7.zCoord;
	}

	public int tickRate() {
		return this.material == Material.water ? 5 : (this.material == Material.lava ? 30 : 0);
	}

	public float getBlockBrightness(IBlockAccess blockAccess, int x, int y, int z) {
		float f5 = blockAccess.getBrightness(x, y, z);
		float f6 = blockAccess.getBrightness(x, y + 1, z);
		return f5 > f6 ? f5 : f6;
	}

	public void updateTick(World worldObj, int x, int y, int z, Random rand) {
		super.updateTick(worldObj, x, y, z, rand);
	}

	public int getRenderBlockPass() {
		return this.material == Material.water ? 1 : 0;
	}

	public void randomDisplayTick(World worldObj, int x, int y, int z, Random rand) {
		if(this.material == Material.water && rand.nextInt(64) == 0) {
			int i6 = worldObj.getBlockMetadata(x, y, z);
			if(i6 > 0 && i6 < 8) {
				worldObj.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F);
			}
		}

		if(this.material == Material.lava && worldObj.getBlockMaterial(x, y + 1, z) == Material.air && !worldObj.isBlockNormalCube(x, y + 1, z) && rand.nextInt(100) == 0) {
			double d12 = (double)((float)x + rand.nextFloat());
			double d8 = (double)y + this.maxY;
			double d10 = (double)((float)z + rand.nextFloat());
			worldObj.spawnParticle("lava", d12, d8, d10, 0.0D, 0.0D, 0.0D);
		}

	}

	public static double getFlowDirection(IBlockAccess iBlockAccess0, int i1, int i2, int i3, Material material4) {
		Vec3D vec3D5 = null;
		if(material4 == Material.water) {
			vec3D5 = ((BlockFluid)Block.waterMoving).getFlowVector(iBlockAccess0, i1, i2, i3);
		}

		if(material4 == Material.lava) {
			vec3D5 = ((BlockFluid)Block.lavaMoving).getFlowVector(iBlockAccess0, i1, i2, i3);
		}

		return vec3D5.xCoord == 0.0D && vec3D5.zCoord == 0.0D ? -1000.0D : Math.atan2(vec3D5.zCoord, vec3D5.xCoord) - Math.PI / 2D;
	}

	public void onBlockAdded(World worldObj, int x, int y, int z) {
		this.checkForHarden(worldObj, x, y, z);
	}

	public void onNeighborBlockChange(World worldObj, int x, int y, int z, int id) {
		this.checkForHarden(worldObj, x, y, z);
	}

	private void checkForHarden(World world1, int i2, int i3, int i4) {
		if(world1.getBlockId(i2, i3, i4) == this.blockID) {
			if(this.material == Material.lava) {
				boolean z5 = false;
				if(z5 || world1.getBlockMaterial(i2, i3, i4 - 1) == Material.water) {
					z5 = true;
				}

				if(z5 || world1.getBlockMaterial(i2, i3, i4 + 1) == Material.water) {
					z5 = true;
				}

				if(z5 || world1.getBlockMaterial(i2 - 1, i3, i4) == Material.water) {
					z5 = true;
				}

				if(z5 || world1.getBlockMaterial(i2 + 1, i3, i4) == Material.water) {
					z5 = true;
				}

				if(z5 || world1.getBlockMaterial(i2, i3 + 1, i4) == Material.water) {
					z5 = true;
				}

				if(z5) {
					int i6 = world1.getBlockMetadata(i2, i3, i4);
					if(i6 == 0) {
						world1.setBlockWithNotify(i2, i3, i4, Block.obsidian.blockID);
					} else if(i6 <= 4) {
						world1.setBlockWithNotify(i2, i3, i4, Block.cobblestone.blockID);
					}

					this.triggerLavaMixEffects(world1, i2, i3, i4);
				}
			}

		}
	}

	protected void triggerLavaMixEffects(World world1, int i2, int i3, int i4) {
		world1.playSoundEffect((double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), "random.fizz", 0.5F, 2.6F + (world1.rand.nextFloat() - world1.rand.nextFloat()) * 0.8F);

		for(int i5 = 0; i5 < 8; ++i5) {
			world1.spawnParticle("largesmoke", (double)i2 + Math.random(), (double)i3 + 1.2D, (double)i4 + Math.random(), 0.0D, 0.0D, 0.0D);
		}

	}
}