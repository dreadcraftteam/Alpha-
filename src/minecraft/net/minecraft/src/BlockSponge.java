package net.minecraft.src;

public class BlockSponge extends Block {
	protected BlockSponge(int id) {
		super(id, Material.sponge);
		this.blockIndexInTexture = 48;
		this.blockParticleGravity = 0.9F;
	}

	public void onBlockAdded(World worldObj, int x, int y, int z) {
		byte b5 = 2;

		for(int i6 = x - b5; i6 <= x + b5; ++i6) {
			for(int i7 = y - b5; i7 <= y + b5; ++i7) {
				for(int i8 = z - b5; i8 <= z + b5; ++i8) {
					if(worldObj.getBlockMaterial(i6, i7, i8) == Material.water) {
						worldObj.setBlockWithNotify(i6, i7, i8, 0);
					}
				}
			}
		}

	}

	public void onBlockRemoval(World worldObj, int x, int y, int z) {
		byte b5 = 2;

		for(int i6 = x - b5; i6 <= x + b5; ++i6) {
			for(int i7 = y - b5; i7 <= y + b5; ++i7) {
				for(int i8 = z - b5; i8 <= z + b5; ++i8) {
					worldObj.notifyBlocksOfNeighborChange(i6, i7, i8, worldObj.getBlockId(i6, i7, i8));
				}
			}
		}

	}
}