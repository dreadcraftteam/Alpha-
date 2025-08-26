package net.minecraft.src;

import java.util.Random;

public class BlockGrass extends Block {
	protected BlockGrass(int blockID) {
		super(blockID, Material.grass);
		this.blockIndexInTexture = 3;
		this.setTickOnLoad(true);
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(i5 == 1) {
			return 0;
		} else if(i5 == 0) {
			return 2;
		} else {
			Material material6 = iBlockAccess1.getBlockMaterial(i2, i3 + 1, i4);
			return material6 != Material.snow && material6 != Material.craftedSnow ? 3 : 68;
		}
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(world1.getBlockLightValue(i2, i3 + 1, i4) < 4 && world1.getBlockMaterial(i2, i3 + 1, i4).getCanBlockGrass()) {
			if(random5.nextInt(4) != 0) {
				return;
			}

			world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
		} else if(world1.getBlockLightValue(i2, i3 + 1, i4) >= 9) {
			int i6 = i2 + random5.nextInt(3) - 1;
			int i7 = i3 + random5.nextInt(5) - 3;
			int i8 = i4 + random5.nextInt(3) - 1;
			if(world1.getBlockId(i6, i7, i8) == Block.dirt.blockID && world1.getBlockLightValue(i6, i7 + 1, i8) >= 4 && !world1.getBlockMaterial(i6, i7 + 1, i8).getCanBlockGrass()) {
				world1.setBlockWithNotify(i6, i7, i8, Block.grass.blockID);
			}
		}

	}

	public int idDropped(int i1, Random random2) {
		return Block.dirt.idDropped(0, random2);
	}
}
