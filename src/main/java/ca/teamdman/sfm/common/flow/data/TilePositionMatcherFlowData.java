package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TilePositionMatcherFlowData extends FlowData implements TileMatcher {
	public BlockPos position;

	public TilePositionMatcherFlowData(UUID uuid, BlockPos position) {
		super(uuid);
		this.position = position;
	}

	@Override
	public boolean matches(@Nonnull TileEntity tile) {
		return Objects.equals(tile.getPos(), position);
	}

	@Override
	public List<ItemStack> getPreview(CableNetwork network) {
		return Collections.singletonList(network.getPreview(position));
	}

	@Override
	public String getMatcherDisplayName() {
		return null;
	}

	@Override
	public FlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return null;
	}

	@Nullable
	@Override
	public FlowComponent createController(FlowComponent parent) {
		return null;
	}

	@Override
	public <T extends FlowData> FlowDataSerializer<T> getSerializer() {
		return null;
	}
}
