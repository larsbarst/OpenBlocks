package openblocks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGrave;

public class PlayerDeathHandler {

	/**
	 * Switched this to the player death event, because the inventory
	 * is cleared out before the item drop event.
	 * Either would work, I guess, but this is a bit neater
	 * 
	 * ... I say neater...
	 * @param event
	 */
	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.entityLiving != null && event.entityLiving instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			World world = player.worldObj;
			
			if (OpenBlocks.proxy.isServer()) {
				int x = (int)player.posX;
				int y = (int)player.posY;
				int z = (int)player.posZ;
				boolean aboveIsAir = false;
				
				for (int checkY = y; checkY > 1; checkY--) {
					if (!world.isAirBlock(x, checkY, z) && aboveIsAir) {
						checkY++;
						world.setBlock(x, checkY, z, OpenBlocks.Config.blockGraveId, 0, 2);
						TileEntity tile = world.getBlockTileEntity(x, checkY, z);
						if (tile != null && tile instanceof TileEntityGrave) {
							System.out.println("Found the grave");
							TileEntityGrave grave = (TileEntityGrave) tile;
							grave.setUsername(player.username);
							grave.setLoot(player.inventory);
							player.inventory.clearInventory(-1, -1);
							break;
						}
					}else {
						aboveIsAir = true;
					}
				}
			}
			
		}
	}
}
