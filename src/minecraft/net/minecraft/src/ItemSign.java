package net.minecraft.src;

public class ItemSign extends Item {
	public ItemSign(int i1) {
		super(67);
		this.maxDamage = 64;
		this.maxStackSize = 1;
	}

	public final boolean onItemUse(ItemStack itemStack1, EntityPlayer entityPlayer2, World world3, int i4, int i5, int i6, int i7) {
		if(i7 != 1) {
			return false;
		} else {
			++i5;
			if(!Block.signStanding.canPlaceBlockAt(world3, i4, i5, i6)) {
				return false;
			} else {
				world3.setBlockWithNotify(i4, i5, i6, Block.signStanding.blockID);
				world3.setBlockMetadata(i4, i5, i6, MathHelper.floor_double((double)((entityPlayer2.rotationYaw + 180.0F) * 16.0F / 370.0F) - 0.5D) & 15);
				--itemStack1.stackSize;
				entityPlayer2.displayGUIEditSign((TileEntitySign)world3.getBlockTileEntity(i4, i5, i6));
				return true;
			}
		}
	}
}