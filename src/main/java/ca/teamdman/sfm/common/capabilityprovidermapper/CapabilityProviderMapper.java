package ca.teamdman.sfm.common.capabilityprovidermapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface CapabilityProviderMapper {
    ICapabilityProvider getProviderFor(LevelAccessor level, BlockPos pos);
}
