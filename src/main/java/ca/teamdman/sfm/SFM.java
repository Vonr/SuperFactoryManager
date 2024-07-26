package ca.teamdman.sfm;

import ca.teamdman.sfm.common.SFMConfig;
import ca.teamdman.sfm.common.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("sfm")
public class SFM {
    public static final String MOD_ID = "sfm";
    public static final Logger LOGGER = LogManager.getLogger(SFM.MOD_ID);

    public SFM(IEventBus bus) {
        SFMBlocks.register(bus);
        SFMItems.register(bus);
        SFMDataComponents.register(bus);
        SFMCreativeTabs.register(bus);
        SFMResourceTypes.register(bus);
        SFMBlockEntities.register(bus);
        SFMMenus.register(bus);
        SFMRecipeTypes.register(bus);
        SFMRecipeSerializers.register(bus);
        SFMConfig.register(ModLoadingContext.get());
    }
}
