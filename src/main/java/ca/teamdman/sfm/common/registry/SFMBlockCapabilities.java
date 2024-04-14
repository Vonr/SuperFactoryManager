package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockcapabilityprovider.CauldronBlockCapabilityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SFMBlockCapabilities {

    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> blockEntity.INVENTORY
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                SFMBlockEntities.WATER_TANK_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> blockEntity.TANK
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                SFMBlockEntities.BATTERY_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> blockEntity.CONTAINER
        );
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                new CauldronBlockCapabilityProvider()
        );
    }
}
