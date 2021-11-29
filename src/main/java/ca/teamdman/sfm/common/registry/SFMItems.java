package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SFMItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SFM.MOD_ID);

	public static final CreativeModeTab TAB = new CreativeModeTab(SFM.MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(SFMBlocks.MANAGER_BLOCK.get());
		}
	};

	public static final RegistryObject<Item> MANAGER_ITEM = ITEMS.register("manager", ()-> new BlockItem(
			SFMBlocks.MANAGER_BLOCK.get(),
			new Item.Properties().tab(TAB)
	));

	public static void register(IEventBus bus) {ITEMS.register(bus);}
}
