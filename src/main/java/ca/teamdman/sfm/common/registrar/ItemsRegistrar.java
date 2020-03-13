package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.List;


@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemsRegistrar {
	public static final  ItemGroup group   = new ItemGroup(-1, "sfm") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(BlocksRegistrar.Blocks.MANAGER);
		}
	};
	private static final Item      WAITING = null;

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> e) {
		List<Item> items = Arrays.asList(
				new BlockItem(BlocksRegistrar.Blocks.MANAGER, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID, "manager"),
				new BlockItem(BlocksRegistrar.Blocks.CABLE, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID, "cable"),
				new BlockItem(BlocksRegistrar.Blocks.CRAFTER, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID, "crafter")
		);
		items.forEach(e.getRegistry()::register);
		SFM.PROXY.fillItemGroup(group, items);
		LogManager.getLogger(SFM.MOD_NAME + " Items Registrar").debug("Registered items");
	}

	@ObjectHolder(SFM.MOD_ID)
	public static class Items {
		public static final Item MANAGER = WAITING;
	}
}
