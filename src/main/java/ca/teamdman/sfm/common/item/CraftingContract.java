package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.registrar.SFMItems;
import net.minecraft.item.Item;

public class CraftingContract extends Item {
//	public final ItemStack[] inputs;
//	public final ItemStack output;
//	public final ResourceLocation recipe;
//	public transient boolean verified = false;

	public CraftingContract() {
		super(new Item.Properties().group(SFMItems.GROUP));
	}
}
