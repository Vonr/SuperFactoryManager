package ca.teamdman.sfm;

import ca.teamdman.sfm.client.ClientProxy;
import ca.teamdman.sfm.common.CommonProxy;
import ca.teamdman.sfm.common.registry.*;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("sfm")
public class SFM {
    public static final String      MOD_ID = "sfm";
    public static final Logger      LOGGER = LogManager.getLogger(SFM.MOD_ID);
    public static final CommonProxy PROXY  = DistExecutor.unsafeRunForDist(
            () -> ClientProxy::new,
            () -> CommonProxy::new
    );

    public SFM() {
        var bus = FMLJavaModLoadingContext
                .get()
                .getModEventBus();
        SFMBlocks.register(bus);
        SFMItems.register(bus);
        SFMBlockEntities.register(bus);
        SFMMenus.register(bus);
        bus.addListener((FMLClientSetupEvent e) -> PROXY.setupScreens());
        bus.addListener((FMLCommonSetupEvent e) -> SFMPackets.register());
    }
}
