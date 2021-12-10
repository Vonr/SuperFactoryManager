package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.CableBlock;
import ca.teamdman.sfm.common.block.ManagerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class SFMBlocks {
    private static final DeferredRegister<Block> BLOCKS        = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            SFM.MOD_ID
    );
    public static final  RegistryObject<Block>   MANAGER_BLOCK = BLOCKS.register("manager", ManagerBlock::new);
    public static final  RegistryObject<Block>   CABLE_BLOCK   = BLOCKS.register("cable", CableBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

}
