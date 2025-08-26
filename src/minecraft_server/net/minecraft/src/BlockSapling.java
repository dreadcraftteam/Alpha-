package net.minecraft.src;

import java.util.Random;

public class BlockSapling extends BlockFlower {
	protected BlockSapling(int i1, int i2) {
		super(i1, i2);
		float f3 = 0.4F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, f3 * 2.0F, 0.5F + f3);
	}

	public void updateTick(World world, int x, int y, int z, Random random) {
		super.updateTick(world, x, y, z, random);
		if(world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(5) == 0) {
			int i6 = world.getBlockMetadata(x, y, z);
			if(i6 < 15) {
				world.setBlockMetadataWithNotify(x, y, z, i6 + 1);
			} else {
				world.setBlock(x, y, z, 0);
				Object object7 = new WorldGenTrees();
				if(random.nextInt(10) == 0) {
					object7 = new WorldGenBigTree();
				}

				if(!((WorldGenerator)object7).generate(world, random, x, y, z)) {
					world.setBlock(x, y, z, this.blockID);
				}
			}
		}

	}
}
