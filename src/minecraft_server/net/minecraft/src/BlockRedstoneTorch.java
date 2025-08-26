package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRedstoneTorch extends BlockTorch {
	private boolean torchActive = false;
	private static List torchUpdates = new ArrayList();

	private boolean checkForBurnout(World world, int x, int y, int z, boolean z5) {
		if(z5) {
			torchUpdates.add(new RedstoneUpdateInfo(x, y, z, world.worldTime));
		}

		int i6 = 0;

		for(int i7 = 0; i7 < torchUpdates.size(); ++i7) {
			RedstoneUpdateInfo redstoneUpdateInfo8 = (RedstoneUpdateInfo)torchUpdates.get(i7);
			if(redstoneUpdateInfo8.x == x && redstoneUpdateInfo8.y == y && redstoneUpdateInfo8.z == z) {
				++i6;
				if(i6 >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	protected BlockRedstoneTorch(int id, int blockIndex, boolean torchActive) {
		super(id, blockIndex);
		this.torchActive = torchActive;
		this.setTickOnLoad(true);
	}

	public int tickRate() {
		return 2;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		if(world1.getBlockMetadata(i2, i3, i4) == 0) {
			super.onBlockAdded(world1, i2, i3, i4);
		}

		if(this.torchActive) {
			world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
		}

	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		if(this.torchActive) {
			world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
		}

	}

	public boolean isPoweringTo(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(!this.torchActive) {
			return false;
		} else {
			int i6 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
			return i6 == 5 && i5 == 1 ? false : (i6 == 3 && i5 == 3 ? false : (i6 == 4 && i5 == 2 ? false : (i6 == 1 && i5 == 5 ? false : i6 != 2 || i5 != 4)));
		}
	}

	private boolean isIndirectlyPowered(World world, int x, int y, int z) {
		int i5 = world.getBlockMetadata(x, y, z);
		return i5 == 5 && world.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0) ? true : (i5 == 3 && world.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2) ? true : (i5 == 4 && world.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3) ? true : (i5 == 1 && world.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4) ? true : i5 == 2 && world.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5))));
	}

	public void updateTick(World world, int x, int y, int z, Random random) {
		boolean z6 = this.isIndirectlyPowered(world, x, y, z);

		while(torchUpdates.size() > 0 && world.worldTime - ((RedstoneUpdateInfo)torchUpdates.get(0)).updateTime > 100L) {
			torchUpdates.remove(0);
		}

		if(this.torchActive) {
			if(z6) {
				world.setBlockAndMetadataWithNotify(x, y, z, Block.torchRedstoneIdle.blockID, world.getBlockMetadata(x, y, z));
				if(this.checkForBurnout(world, x, y, z, true)) {
					world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for(int i7 = 0; i7 < 5; ++i7) {
						double d8 = (double)x + random.nextDouble() * 0.6D + 0.2D;
						double d10 = (double)y + random.nextDouble() * 0.6D + 0.2D;
						double d12 = (double)z + random.nextDouble() * 0.6D + 0.2D;
						world.spawnParticle("smoke", d8, d10, d12, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		} else if(!z6 && !this.checkForBurnout(world, x, y, z, false)) {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.torchRedstoneActive.blockID, world.getBlockMetadata(x, y, z));
		}

	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		super.onNeighborBlockChange(world1, i2, i3, i4, i5);
		world1.scheduleBlockUpdate(i2, i3, i4, this.blockID);
	}

	public boolean isIndirectlyPoweringTo(World world1, int i2, int i3, int i4, int i5) {
		return i5 == 0 ? this.isPoweringTo(world1, i2, i3, i4, i5) : false;
	}

	public int idDropped(int i1, Random random2) {
		return Block.torchRedstoneActive.blockID;
	}

	public boolean canProvidePower() {
		return true;
	}
}
