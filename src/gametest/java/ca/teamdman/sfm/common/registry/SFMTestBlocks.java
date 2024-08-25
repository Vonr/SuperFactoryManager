package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.BatteryBlock;
import ca.teamdman.sfm.common.block.TestBarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMTestBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SFM.MOD_ID);
    public static final RegistryObject<Block> BATTERY_BLOCK = BLOCKS.register("battery", BatteryBlock::new);
    public static final RegistryObject<Block> TEST_BARREL_BLOCK = BLOCKS.register("test_barrel", TestBarrelBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
