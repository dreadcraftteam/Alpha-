package net.minecraft.src;

import java.util.Random;

public class BlockGrass extends Block {
	private boolean[] canGrass = new boolean[256];

	protected BlockGrass(int blockID) {
		super(blockID, Material.grass);
		this.blockIndexInTexture = 3;
		this.setTickOnLoad(true);
	}

	public void initializeBlock() {
		for(int i1 = 0; i1 < 256; ++i1) {
			boolean z2 = Block.lightOpacity[i1] > 2;
			if(Block.blocksList[i1] != null) {
				Material material3 = Block.blocksList[i1].material;
				if(material3.getIsLiquid()) {
					z2 = true;
				}

				if(material3 == Material.leaves) {
					z2 = false;
				}

				if(material3 == Material.ice) {
					z2 = true;
				}
			}

			this.canGrass[i1] = z2;
		}

	}

	public int getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		Material material6 = blockAccess.getBlockMaterial(x, y + 1, z);
		boolean z7 = material6 != Material.snow && material6 != Material.craftedSnow;
		return side == 1 ? (z7 ? 0 : 66) : (side == 0 ? 2 : (z7 ? 3 : 68));
	}

	public int getBlockTextureFromSide(int side) {
		return side == 1 ? 0 : (side == 0 ? 2 : 3);
	}

	public void updateTick(World worldObj, int x, int y, int z, Random rand) {
		if(!worldObj.multiplayerWorld) {
			if(worldObj.getBlockLightValue(x, y + 1, z) < 4 && this.canGrass[worldObj.getBlockId(x, y + 1, z)]) {
				if(rand.nextInt(4) != 0) {
					return;
				}

				worldObj.setBlockWithNotify(x, y, z, Block.dirt.blockID);
			} else if(worldObj.getBlockLightValue(x, y + 1, z) >= 9) {
				int i6 = x + rand.nextInt(3) - 1;
				int i7 = y + rand.nextInt(5) - 3;
				int i8 = z + rand.nextInt(3) - 1;
				if(worldObj.getBlockId(i6, i7, i8) == Block.dirt.blockID && worldObj.getBlockLightValue(i6, i7 + 1, i8) >= 4 && !this.canGrass[worldObj.getBlockId(i6, i7 + 1, i8)]) {
					worldObj.setBlockWithNotify(i6, i7, i8, Block.grass.blockID);
				}
			}

		}
	}

	public int idDropped(int metadata, Random rand) {
		return Block.dirt.idDropped(0, rand);
	}

	public boolean canBlockGrass(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return this.canGrass[iBlockAccess1.getBlockId(i2, i3, i4)];
	}
}