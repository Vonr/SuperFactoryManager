package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            SFM.MOD_ID
    );

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register(
            "main",
            () -> CreativeModeTab
                    .builder()
                    .title(Constants.LocalizationKeys.ITEM_GROUP.getComponent())
                    .icon(() -> new ItemStack(SFMBlocks.MANAGER_BLOCK.get()))
                    .displayItems(SFMItems::populateMainCreativeTab)
                    .build()
    );

    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }
}
