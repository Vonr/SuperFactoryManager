package ca.teamdman.sfm.registrar;

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
import ca.teamdman.sfm.SFM;

import java.util.Arrays;
import java.util.List;


@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ItemsRegistrar {
	private static final Item WAITING = null;
	public static final ItemGroup group = new ItemGroup(-1,"sfm") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(BlocksRegistrar.Blocks.MANAGER);
		}
	};

	@ObjectHolder(SFM.MOD_ID)
	public static class Items {
		public static final Item MANAGER = WAITING;
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> e) {
		//noinspection ArraysAsListWithZeroOrOneArgument
		List<Item> items = Arrays.asList(
				new BlockItem(BlocksRegistrar.Blocks.MANAGER, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID,"manager")
		);
		items.forEach(e.getRegistry()::register);
		group.fill(items.stream().map(ItemStack::new).collect(NonNullList::create,NonNullList::add,NonNullList::addAll));
		LogManager.getLogger(SFM.MOD_NAME + " Items Registrar").debug("Registered items");
	}
}
