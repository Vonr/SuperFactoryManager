package ca.teamdman.sfm.common.cablenetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Optional;

public class BlockEntityCapabilityProviderMapper implements CapabilityProviderMapper {
    @Override
    public Optional<ICapabilityProvider> getProviderFor(LevelAccessor level, BlockPos pos) {
        return Optional.ofNullable(level.getBlockEntity(pos));
    }
}
