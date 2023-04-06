package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.resourcetype.*;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
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

    private static final DeferredRegister<ResourceType<?, ?>>                    TYPES          = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final  Supplier<IForgeRegistry<ResourceType<?, ?>>>            DEFERRED_TYPES = TYPES.makeRegistry(() -> new RegistryBuilder<ResourceType<?, ?>>().setName(
            REGISTRY_ID));
    public static final  RegistryObject<ResourceType<ItemStack, IItemHandler>>   ITEM           = TYPES.register(
            "item",
            ItemResourceType::new
    );
    public static final  RegistryObject<ResourceType<FluidStack, IFluidHandler>> FLUID          = TYPES.register(
            "fluid",
            FluidResourceType::new
    );

    public static final RegistryObject<ResourceType<GasStack, IGasHandler>>           GASES     = TYPES.register(
            "gas",
            GasResourceType::new
    );
    public static final RegistryObject<ResourceType<InfusionStack, IInfusionHandler>> INFUSIONS = TYPES.register(
            "infusion",
            InfusionResourceType::new
    );

    public static final RegistryObject<ResourceType<PigmentStack, IPigmentHandler>> PIGMENTS = TYPES.register(
            "pigment",
            PigmentResourceType::new
    );

    public static final RegistryObject<ResourceType<SlurryStack, ISlurryHandler>> SLURRIES = TYPES.register(
            "slurry",
            SlurryResourceType::new
    );

//    public static final GasStack gas;

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
