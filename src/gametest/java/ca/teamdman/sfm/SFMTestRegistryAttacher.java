package ca.teamdman.sfm;

import ca.teamdman.sfm.common.registry.SFMTestBlockEntities;
import ca.teamdman.sfm.common.registry.SFMTestBlocks;
import net.minecraftforge.fml.common.Mod;

// force this class to be loaded
@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMTestRegistryAttacher {
    static {
        SFM.LOGGER.info("Hello from SFM Test Mod!");
        SFM.TEST_ATTACHMENTS.add(b -> {
            SFMTestBlocks.register(b);
            SFMTestBlockEntities.register(b);
        });
    }
}
