package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.data.TileInputFlowData;
import ca.teamdman.sfm.common.flow.data.TileOutputFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class ExecutionFrame {

	private final List<ItemStackTileEntityRuleFlowData> INPUTS = new ArrayList<>();
	//todo: track matcher 'usage'
	// e.g., if transfered 2 stone already, don't transfer 2 more in diff slots
	private final ManagerTileEntity TILE;
	private final FlowData CURRENT;

	public ExecutionFrame(ManagerTileEntity tile, FlowData current) {
		this.TILE = tile;
		this.CURRENT = current;
	}

	/**
	 * When multiple paths lead from a node, fork execution to preserve state
	 *
	 * @return New execution frame with a snapshot of the inputs
	 */
	public ExecutionFrame fork(FlowData next) {
		ExecutionFrame other = new ExecutionFrame(TILE, next);
		other.INPUTS.addAll(INPUTS);
		return other;
	}

	public List<ExecutionFrame> step() {
		BasicFlowDataContainer container = TILE.getFlowDataContainer();
		if (CURRENT instanceof TileInputFlowData) {
			((TileInputFlowData) CURRENT).tileEntityRules.stream()
				.map(id -> container.get(id, ItemStackTileEntityRuleFlowData.class))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.forEach(INPUTS::add);
		} else if (CURRENT instanceof TileOutputFlowData) {
			CableNetworkManager.getOrRegisterNetwork(TILE)
				.ifPresent(network ->
					((TileOutputFlowData) CURRENT).tileEntityRules.stream()
						.map(id -> container.get(id, ItemStackTileEntityRuleFlowData.class))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.forEach(rule -> satisfyOutput(network, rule)));

		}
		return container.get(RelationshipFlowData.class)
			.filter(rel -> rel.from.equals(CURRENT.getId()))
			.map(rel -> rel.to)
			.map(container::get)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(this::fork)
			.collect(Collectors.toList());
	}

	private void satisfyOutput(CableNetwork network, ItemStackTileEntityRuleFlowData outRule) {
		BasicFlowDataContainer dataContainer = TILE.getFlowDataContainer();
		World world = TILE.getWorld();
		Objects.requireNonNull(world);
		List<IItemHandler> outHandlers = outRule.getItemHandlers(world, network);

		for (ItemStackTileEntityRuleFlowData inRule : INPUTS) {
			for (IItemHandler inHandler : inRule.getItemHandlers(world, network)) {
				IN:
				for (int inSlot : inRule.slots.getSlots(inHandler.getSlots()).toArray()) {
					ItemStack stack = inHandler.getStackInSlot(inSlot);
					int allowedToExtract = inRule.getAllowedQuantity(dataContainer, stack);
					int allowedToInsert = outRule.getAllowedQuantity(dataContainer, stack);
					int remaining = Math.max(0, Math.min(allowedToExtract, allowedToInsert));
					int previous = -1;
					while (remaining > 0 && previous != remaining) {
						previous = remaining;
						for (IItemHandler outHandler : outHandlers) {
							for (int outSlot : outRule.slots.getSlots(outHandler.getSlots()).toArray()) {
								ItemStack extracted = inHandler.extractItem(inSlot, remaining, true);
								if (extracted.isEmpty()) continue IN;
								ItemStack remainder = outHandler.insertItem(outSlot, extracted, true);
								int toTransfer = extracted.getCount() - remainder.getCount();
								extracted = inHandler.extractItem(inSlot, toTransfer, false);
								remainder = outHandler.insertItem(outSlot, extracted, false);
								remaining -= remainder.getCount();
								if (!remainder.isEmpty()) {
									SFM.LOGGER.warn(
										SFMUtil.getMarker(getClass()),
										"Failed to fully insert stack {}, remaining {}",
										extracted,
										remainder
									);
								}
							}
						}
					}
				}
			}
		}
	}
}
