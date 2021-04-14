package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.ItemMatcher;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemInputFlowData;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemOutputFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ExecutionStep {

	private final List<ItemMovementRuleFlowData> INPUTS = new ArrayList<>();
	private final ManagerTileEntity TILE;
	private final FlowData CURRENT;
	private final ExecutionState STATE;

	public ExecutionStep(
		ManagerTileEntity tile,
		FlowData current,
		ExecutionState state
	) {
		this.TILE = tile;
		this.CURRENT = current;
		this.STATE = state;
	}

	public List<ExecutionStep> step() {
		BasicFlowDataContainer container = TILE.getFlowDataContainer();
		if (CURRENT instanceof ItemInputFlowData) {
			UUID ruleId = ((ItemInputFlowData) CURRENT).tileEntityRule;
			container.get(ruleId, ItemMovementRuleFlowData.class).ifPresent(INPUTS::add);
		} else if (CURRENT instanceof ItemOutputFlowData) {
			UUID ruleId = ((ItemOutputFlowData) CURRENT).tileEntityRule;
			CableNetworkManager.getOrRegisterNetwork(TILE).ifPresent(network ->
				container.get(ruleId, ItemMovementRuleFlowData.class)
					.ifPresent(rule -> satisfyOutput(network, rule)));

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

	private void satisfyOutput(CableNetwork network, ItemMovementRuleFlowData outRule) {
		BasicFlowDataContainer dataContainer = TILE.getFlowDataContainer();
		List<IItemHandler> outHandlers = outRule.getItemHandlers(dataContainer, network);

		// for each input rule hit so far during flow execution
		for (ItemMovementRuleFlowData inRule : INPUTS) {

			// for each tile defined in the input rule
			for (IItemHandler inHandler : inRule.getItemHandlers(dataContainer, network)) {

				// for each slot defined in the input rule
				IN_SLOT:
				for (int inSlot : inRule.slots.getSlots(inHandler.getSlots()).toArray()) {

					// Get stack in slot
					ItemStack stack = inHandler.getStackInSlot(inSlot);

					// go to next slot if empty
					if (stack.isEmpty()) {
						continue;
					}

					// get the matchers that determine how much of the item is allowed to move
					// transfer can be throttled input and output at the same time
					ItemMatcher inMatcher = inRule.getBestItemMatcher(dataContainer, stack, STATE)
						.orElse(null);
					ItemMatcher outMatcher = outRule
						.getBestItemMatcher(dataContainer, stack, STATE).orElse(null);

					// get the amount allowed to be moved, noting whitelist/blacklist
					int allowedToExtract = STATE.getRemainingQuantity(inRule, inMatcher);
					int allowedToInsert = STATE.getRemainingQuantity(outRule, outMatcher);

					// if none allowed to move for this slot's stack, skip
					if (allowedToExtract == 0 || allowedToInsert == 0) {
						continue;
					}

					// determine the maximum amount allowed to move
					// move the minimum of the input and output quantities
					int remainingQuantity = Math
						.max(0, Math.min(allowedToExtract, allowedToInsert));

					// if we are no longer able to distribute the input stack, end operation
					// causes partial movement, e.g.:
					// [INPUT 64xCobblestone] can result in only half a stack moving if no room in output
					// if user wants to ensure full stack can move, ensure destination is empty first I guess?
					int previous = -1;
					while (remainingQuantity > 0 && previous != remainingQuantity) {
						previous = remainingQuantity;

						// for each destination inventory, try and distribute input stack
						for (IItemHandler outHandler : outHandlers) {

							// for each slot in destination that is permitted by output rule
							for (int outSlot : outRule.slots.getSlots(outHandler.getSlots())
								.toArray()) {

								// get stack simulating extraction
								ItemStack extracted = inHandler
									.extractItem(inSlot, remainingQuantity, true);

								// if can't extract anything, skip to the next input slot
								if (extracted.isEmpty()) {
									continue IN_SLOT;
								}

								// get leftovers simulating insertion
								ItemStack leftoverStack = outHandler
									.insertItem(outSlot, extracted, true);

								// calculate difference based on what we will extract vs what we will insert
								int toTransfer = extracted.getCount() - leftoverStack.getCount();

								// extract for real, only pulling what we know we can insert into destination
								extracted = inHandler.extractItem(inSlot, toTransfer, false);

								// insert for real, leftovers should be empty
								leftoverStack = outHandler.insertItem(outSlot, extracted, false);

								// record how much we were able to move before attempting to insert to next output slot
								// e.g., if we're allowed to extract 64xCobble, it might have to be deposited into multiple slots
								remainingQuantity -= toTransfer;

								// record how much of the actual matcher quota we used
								STATE.recordUsage(inMatcher, toTransfer);
								STATE.recordUsage(outMatcher, toTransfer);

								// if somehow we extracted more than we were able to deposit, log a warning
								// the leftovers will be voided to prevent dupe bugs since this should never happen
								if (!leftoverStack.isEmpty()) {
									SFM.LOGGER.warn(
										SFMUtil.getMarker(getClass()),
										"Failed to fully insert stack {}, remaining {} will be voided",
										extracted,
										leftoverStack
									);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * When multiple paths lead from a node, fork execution to preserve state
	 *
	 * @return New execution frame with a snapshot of the inputs
	 */
	public ExecutionStep fork(FlowData next) {
		ExecutionStep other = new ExecutionStep(TILE, next, STATE);
		other.INPUTS.addAll(INPUTS);
		return other;
	}
}
