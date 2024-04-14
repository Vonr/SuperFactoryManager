package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockcapabilityprovider.CauldronBlockCapabilityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                Capabilities.ItemHandler.BLOCK,
                new IBlockCapabilityProvider<>() {
                    @Override
                    public @Nullable IItemHandler getCapability(
                            Level level,
                            BlockPos pos,
                            BlockState state,
                            @Nullable BlockEntity blockEntity,
                            Direction context
                    ) {
                        if (blockEntity instanceof BarrelBlockEntity bbe) {
                            return new InvWrapper(bbe);
                        }
                        return null;
                    }
                },
                SFMBlocks.TEST_BARREL_BLOCK.get()
        );
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                new CauldronBlockCapabilityProvider(),
                Blocks.CAULDRON,
                Blocks.LAVA_CAULDRON,
                Blocks.WATER_CAULDRON
        );
    }
}
