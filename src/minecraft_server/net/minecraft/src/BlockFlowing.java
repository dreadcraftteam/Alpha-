package net.minecraft.src;

import java.util.Random;

public class BlockFlowing extends BlockFluid {
	int numAdjacentSources = 0;
	boolean[] isOptimalFlowDirection = new boolean[4];
	int[] flowCost = new int[4];

	protected BlockFlowing(int i1, Material material2) {
		super(i1, material2);
	}

	private void updateFlow(World world, int x, int y, int z) {
		int i5 = world.getBlockMetadata(x, y, z);
		world.setBlockAndMetadata(x, y, z, this.blockID + 1, i5);
		world.markBlocksDirty(x, y, z, x, y, z);
		world.markBlockNeedsUpdate(x, y, z);
	}

	public void updateTick(World world, int x, int y, int z, Random random) {
		int i6 = this.getFlowDecay(world, x, y, z);
		boolean z7 = true;
		int i9;
		if(i6 > 0) {
			byte b8 = -100;
			this.numAdjacentSources = 0;
			int i11 = this.getSmallestFlowDecay(world, x - 1, y, z, b8);
			i11 = this.getSmallestFlowDecay(world, x + 1, y, z, i11);
			i11 = this.getSmallestFlowDecay(world, x, y, z - 1, i11);
			i11 = this.getSmallestFlowDecay(world, x, y, z + 1, i11);
			i9 = i11 + this.fluidType;
			if(i9 >= 8 || i11 < 0) {
				i9 = -1;
			}

			if(this.getFlowDecay(world, x, y + 1, z) >= 0) {
				int i10 = this.getFlowDecay(world, x, y + 1, z);
				if(i10 >= 8) {
					i9 = i10;
				} else {
					i9 = i10 + 8;
				}
			}

			if(this.numAdjacentSources >= 2 && this.material == Material.water) {
				if(world.isBlockNormalCube(x, y - 1, z)) {
					i9 = 0;
				} else if(world.getBlockMaterial(x, y - 1, z) == this.material && world.getBlockMetadata(x, y, z) == 0) {
					i9 = 0;
				}
			}

			if(this.material == Material.lava && i6 < 8 && i9 < 8 && i9 > i6 && random.nextInt(4) != 0) {
				i9 = i6;
				z7 = false;
			}

			if(i9 != i6) {
				i6 = i9;
				if(i9 < 0) {
					world.setBlockWithNotify(x, y, z, 0);
				} else {
					world.setBlockMetadataWithNotify(x, y, z, i9);
					world.scheduleBlockUpdate(x, y, z, this.blockID);
					world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
				}
			} else if(z7) {
				this.updateFlow(world, x, y, z);
			}
		} else {
			this.updateFlow(world, x, y, z);
		}

		if(this.l(world, x, y - 1, z)) {
			if(i6 >= 8) {
				world.setBlockAndMetadataWithNotify(x, y - 1, z, this.blockID, i6);
			} else {
				world.setBlockAndMetadataWithNotify(x, y - 1, z, this.blockID, i6 + 8);
			}
		} else if(i6 >= 0 && (i6 == 0 || this.k(world, x, y - 1, z))) {
			boolean[] z12 = this.getOptimalFlowDirections(world, x, y, z);
			i9 = i6 + this.fluidType;
			if(i6 >= 8) {
				i9 = 1;
			}

			if(i9 >= 8) {
				return;
			}

			if(z12[0]) {
				this.flowIntoBlock(world, x - 1, y, z, i9);
			}

			if(z12[1]) {
				this.flowIntoBlock(world, x + 1, y, z, i9);
			}

			if(z12[2]) {
				this.flowIntoBlock(world, x, y, z - 1, i9);
			}

			if(z12[3]) {
				this.flowIntoBlock(world, x, y, z + 1, i9);
			}
		}

	}

	private void flowIntoBlock(World world, int x, int y, int z, int metadata) {
		if(this.l(world, x, y, z)) {
			int i6 = world.getBlockId(x, y, z);
			if(i6 > 0) {
				if(this.material == Material.lava) {
					this.triggerLavaMixEffects(world, x, y, z);
				} else {
					Block.blockList[i6].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
				}
			}

			world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, metadata);
		}

	}

	private int calculateFlowCost(World world, int x, int y, int z, int i5, int i6) {
		int i7 = 1000;

		for(int i8 = 0; i8 < 4; ++i8) {
			if((i8 != 0 || i6 != 1) && (i8 != 1 || i6 != 0) && (i8 != 2 || i6 != 3) && (i8 != 3 || i6 != 2)) {
				int i9 = x;
				int i11 = z;
				if(i8 == 0) {
					i9 = x - 1;
				}

				if(i8 == 1) {
					++i9;
				}

				if(i8 == 2) {
					i11 = z - 1;
				}

				if(i8 == 3) {
					++i11;
				}

				if(!this.k(world, i9, y, i11) && (world.getBlockMaterial(i9, y, i11) != this.material || world.getBlockMetadata(i9, y, i11) != 0)) {
					if(!this.k(world, i9, y - 1, i11)) {
						return i5;
					}

					if(i5 < 4) {
						int i12 = this.calculateFlowCost(world, i9, y, i11, i5 + 1, i8);
						if(i12 < i7) {
							i7 = i12;
						}
					}
				}
			}
		}

		return i7;
	}

	private boolean[] getOptimalFlowDirections(World world, int x, int y, int z) {
		int i5;
		int i6;
		for(i5 = 0; i5 < 4; ++i5) {
			this.flowCost[i5] = 1000;
			i6 = x;
			int i8 = z;
			if(i5 == 0) {
				i6 = x - 1;
			}

			if(i5 == 1) {
				++i6;
			}

			if(i5 == 2) {
				i8 = z - 1;
			}

			if(i5 == 3) {
				++i8;
			}

			if(!this.k(world, i6, y, i8) && (world.getBlockMaterial(i6, y, i8) != this.material || world.getBlockMetadata(i6, y, i8) != 0)) {
				if(!this.k(world, i6, y - 1, i8)) {
					this.flowCost[i5] = 0;
				} else {
					this.flowCost[i5] = this.calculateFlowCost(world, i6, y, i8, 1, i5);
				}
			}
		}

		i5 = this.flowCost[0];

		for(i6 = 1; i6 < 4; ++i6) {
			if(this.flowCost[i6] < i5) {
				i5 = this.flowCost[i6];
			}
		}

		for(i6 = 0; i6 < 4; ++i6) {
			this.isOptimalFlowDirection[i6] = this.flowCost[i6] == i5;
		}

		return this.isOptimalFlowDirection;
	}

	private boolean k(World world, int x, int y, int z) {
		int i5 = world.getBlockId(x, y, z);
		if(i5 != Block.doorWood.blockID && i5 != Block.doorSteel.blockID && i5 != Block.signStanding.blockID && i5 != Block.ladder.blockID && i5 != Block.reed.blockID) {
			if(i5 == 0) {
				return false;
			} else {
				Material material6 = Block.blockList[i5].material;
				return material6.isSolid();
			}
		} else {
			return true;
		}
	}

	protected int getSmallestFlowDecay(World world, int x, int y, int z, int i5) {
		int i6 = this.getFlowDecay(world, x, y, z);
		if(i6 < 0) {
			return i5;
		} else {
			if(i6 == 0) {
				++this.numAdjacentSources;
			}

			if(i6 >= 8) {
				i6 = 0;
			}

			return i5 >= 0 && i6 >= i5 ? i5 : i6;
		}
	}

	private boolean l(World world, int x, int y, int z) {
		Material material5 = world.getBlockMaterial(x, y, z);
		return material5 == this.material ? false : (material5 == Material.lava ? false : !this.k(world, x, y, z));
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		if(world1.getBlockId(i2, i3, i4) == this.blockID) {
			world1.scheduleBlockUpdate(i2, i3, i4, this.blockID);
		}

	}
}
