/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ItemOutputFlowButton;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.MovementMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.tile.manager.ExecutionStep;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

public class ItemOutputFlowData extends FlowData implements Observer, PositionHolder {

	private final FlowDataRemovedObserver OBSERVER;
	public Position position;
	public UUID tileEntityRule;

	public ItemOutputFlowData(ItemOutputFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy(),
			other.tileEntityRule
		);
	}

	public ItemOutputFlowData(UUID uuid, Position position, UUID tileEntityRule) {
		super(uuid);
		this.position = position;
		this.tileEntityRule = tileEntityRule;
		OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> data.getId().equals(tileEntityRule),
			c -> c.remove(getId()) // remove this if rule gets deleted
		);
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(this);
	}

	@Override
	public void removeFromDataContainer(BasicFlowDataContainer container) {
		super.removeFromDataContainer(container);
		container.get(tileEntityRule)
			.ifPresent(data -> data.removeFromDataContainer(container));
	}

	@Override
	public void execute(ExecutionStep step) {
		BasicFlowDataContainer container = step.TILE.getFlowDataContainer();
		CableNetworkManager.getOrRegisterNetwork(step.TILE).ifPresent(network ->
			container.get(tileEntityRule, ItemMovementRuleFlowData.class)
				.ifPresent(rule -> satisfyOutput(step, network, rule)));
	}

	private void satisfyOutput(
		ExecutionStep step,
		CableNetwork network,
		ItemMovementRuleFlowData outRule
	) {
		BasicFlowDataContainer dataContainer = step.TILE.getFlowDataContainer();
		List<IItemHandler> outHandlers = outRule.getItemHandlers(dataContainer, network);

		// for each input rule hit so far during flow execution
		for (ItemMovementRuleFlowData inRule : step.INPUTS) {

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
					MovementMatcher inMatcher = inRule
						.getBestItemMatcher(dataContainer, stack, step.STATE)
						.orElse(null);
					MovementMatcher outMatcher = outRule
						.getBestItemMatcher(dataContainer, stack, step.STATE).orElse(null);

					// get the amount allowed to be moved, noting whitelist/blacklist
					int allowedToExtract = step.STATE.getRemainingQuantity(inRule, inMatcher);
					int allowedToInsert = step.STATE.getRemainingQuantity(outRule, outMatcher);

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
								step.STATE.recordUsage(inMatcher, toTransfer);
								step.STATE.recordUsage(outMatcher, toTransfer);

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

	@Override
	public ItemOutputFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		ItemOutputFlowData newOutput = new ItemOutputFlowData(this);
		container.get(newOutput.tileEntityRule, ItemMovementRuleFlowData.class).ifPresent(data -> {
			FlowData newData = data.duplicate(container, dependencyTracker);
			dependencyTracker.accept(newData);
			newOutput.tileEntityRule = newData.getId();
		});
		return newOutput;
	}

	@Override
	public boolean isValidRelationshipTarget() {
		return true;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (parent instanceof ManagerFlowController) {
			return new ItemOutputFlowButton(
				(ManagerFlowController) parent,
				this,
				((ManagerFlowController) parent).SCREEN.getFlowDataContainer()
					.get(tileEntityRule, ItemMovementRuleFlowData.class)
					.orElseGet(ItemMovementRuleFlowData::new)
			);
		}
		return null;
	}

	@Override
	public Set<Class<?>> getDependencies() {
		return ImmutableSet.of(ItemMovementRuleFlowData.class);
	}

	@Override
	public FlowDataSerializer<ItemOutputFlowData> getSerializer() {
		return FlowDataSerializers.BASIC_OUTPUT;
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class Serializer extends FlowDataSerializer<ItemOutputFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public ItemOutputFlowData fromNBT(CompoundNBT tag) {
			return new ItemOutputFlowData(
				getUUID(tag),
				new Position(tag.getCompound("pos")),
				UUID.fromString(tag.getString("tileEntityRule"))
			);
		}

		@Override
		public CompoundNBT toNBT(ItemOutputFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("tileEntityRule", data.tileEntityRule.toString());
			return tag;
		}

		@Override
		public ItemOutputFlowData fromBuffer(PacketBuffer buf) {
			return new ItemOutputFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void toBuffer(ItemOutputFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			SFMUtil.writeUUID(data.tileEntityRule, buf);
		}
	}
}
