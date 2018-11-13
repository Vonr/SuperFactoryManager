package vswe.superfactory.registry;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.superfactory.SuperFactoryManager;
import vswe.superfactory.interfaces.IItemBlockProvider;
import vswe.superfactory.items.ItemMemoryDisk;

import java.util.List;


@Mod.EventBusSubscriber(modid = SuperFactoryManager.MODID)
@GameRegistry.ObjectHolder(SuperFactoryManager.MODID)
public class ModItems {
	public static final Item       DISK = Items.AIR;
	public static       List<Item> items;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		items = Lists.newArrayList();

		ModBlocks.blocks.stream()
				.filter(b -> b instanceof IItemBlockProvider)
				.forEach(b -> items.add(((IItemBlockProvider) b).getItem().setRegistryName(b.getRegistryName())));
		ModBlocks.blocks.stream()
				.filter(b -> !(b instanceof IItemBlockProvider))
				.forEach(b -> items.add(new ItemBlock(b).setRegistryName(b.getRegistryName())));

		items.addAll(Lists.newArrayList(
				new ItemMemoryDisk().setRegistryName("disk")
		));

		items.forEach(event.getRegistry()::register);
	}
}
