package ca.teamdman.sfm.common.capabilityprovidermapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Optional;

public class BlockEntityCapabilityProviderMapper implements CapabilityProviderMapper {
    @Override
    public ICapabilityProvider getProviderFor(LevelAccessor level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }
}
