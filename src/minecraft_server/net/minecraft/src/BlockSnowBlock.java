package net.minecraft.src;

import java.util.Random;

public class BlockSnowBlock extends Block {
	protected BlockSnowBlock(int id, int blockIndex) {
		super(id, blockIndex, Material.craftedSnow);
		this.setTickOnLoad(true);
	}

	public int idDropped(int i1, Random random2) {
		return Item.snowball.shiftedIndex;
	}

	public int quantityDropped(Random random1) {
		return 4;
	}

	public void updateTick(World world, int x, int y, int z, Random random) {
		if(world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
			world.setBlockWithNotify(x, y, z, 0);
		}

	}
}
