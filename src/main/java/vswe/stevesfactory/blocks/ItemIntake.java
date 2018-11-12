package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.StevesFactoryManager;

public class ItemIntake extends ItemBlock {
	public ItemIntake(Block block) {
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		return "tile." + StevesFactoryManager.UNLOCALIZED_START + (BlockCableIntake.isAdvanced(item.getItemDamage()) ? "cable_intake_instant" : "cable_intake");
	}
}
