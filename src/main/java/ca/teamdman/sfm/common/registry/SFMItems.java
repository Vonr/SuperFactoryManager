package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.item.LabelGunItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMItems {

    private static final DeferredRegister<Item> ITEMS           = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            SFM.MOD_ID
    );
    public static final  RegistryObject<Item>   MANAGER_ITEM    = register("manager", SFMBlocks.MANAGER_BLOCK);
    public static final  RegistryObject<Item>   CABLE_ITEM      = register("cable", SFMBlocks.CABLE_BLOCK);
    public static final  RegistryObject<Item>   WATER_TANK_ITEM = register("water_tank", SFMBlocks.WATER_TANK_BLOCK);
    public static final  RegistryObject<Item>   DISK_ITEM       = ITEMS.register("disk", DiskItem::new);
    public static final  RegistryObject<Item>   LABEL_GUN_ITEM  = ITEMS.register("labelgun", LabelGunItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static CreativeModeTab tab;

    private static RegistryObject<Item> register(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @SubscribeEvent
    public static void onRegister(CreativeModeTabEvent.Register event) {
        tab = event.registerCreativeModeTab(new ResourceLocation(SFM.MOD_ID, "main"), builder ->
                // Set name of tab to display
                builder.title(Component.translatable("item_group." + SFM.MOD_ID + ".main"))
                        // Set icon of creative tab
                        .icon(() -> new ItemStack(SFMBlocks.MANAGER_BLOCK.get()))
                        // Add default items to tab
                        .displayItems((params, output) -> output.acceptAll(ITEMS.getEntries().stream()
                                                                                   .map(RegistryObject::get)
                                                                                   .map(ItemStack::new)
                                                                                   .toList()))
        );
    }
}
