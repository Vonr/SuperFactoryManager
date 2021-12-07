package ca.teamdman.sfm;

import ca.teamdman.sfm.common.registry.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("sfm")
public class SFM {
    public static final Logger LOGGER = LogManager.getLogger(SFM.class);
    public static final String MOD_ID = "sfm";

    public SFM() {
        var bus = FMLJavaModLoadingContext
                .get()
                .getModEventBus();
        SFMBlocks.register(bus);
        SFMItems.register(bus);
        SFMBlockEntities.register(bus);
        SFMMenus.register(bus);
        bus.addListener((FMLCommonSetupEvent e) -> SFMScreens.register());
        bus.addListener((FMLClientSetupEvent e) -> SFMPackets.register());
    }
}
