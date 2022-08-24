package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.FluidResourceType;
import ca.teamdman.sfm.common.program.ItemResourceType;
import ca.teamdman.sfm.common.program.ResourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

// Ensure class is loaded (even though we're using field initializers instead of the register event)
//@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMResourceTypes {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SFM.MOD_ID, "resource_type");

//    @SubscribeEvent
//    public static void onRegisterRegistry(NewRegistryEvent e) {
//        var builder = new RegistryBuilder<ResourceType>();
//        builder.setName(REGISTRY_ID);
//        e.create(builder);
//    }

    private static final DeferredRegister<ResourceType>         TYPES          = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final  Supplier<IForgeRegistry<ResourceType>> DEFERRED_TYPES = TYPES.makeRegistry(() -> new RegistryBuilder<ResourceType>().setName(
            REGISTRY_ID));
    public static final  RegistryObject<ResourceType>           ITEM           = TYPES.register(
            "item",
            ItemResourceType::new
    );
    public static final  RegistryObject<ResourceType>           FLUID          = TYPES.register(
            "fluid",
            FluidResourceType::new
    );

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
