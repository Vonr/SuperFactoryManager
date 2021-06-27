/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ItemConditionFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonBackground;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.MovementMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData.ItemMode;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData.Result;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData.TileMode;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.tile.manager.ExecutionStep;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

public class ItemConditionFlowData extends FlowData implements Observer, PositionHolder {

	private final FlowDataRemovedObserver OBSERVER;
	public Position position;
	public UUID rule;
	private transient Result result;

	public ItemConditionFlowData(ItemConditionFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy(),
			other.rule
		);
	}

	public ItemConditionFlowData(UUID uuid, Position position, UUID rule) {
		super(uuid);
		this.position = position;
		this.rule = rule;
		OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> data.getId().equals(rule),
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
		container.get(rule)
			.ifPresent(data -> data.removeFromDataContainer(container));
	}

	@Override
	public void execute(ExecutionStep step) {
		BasicFlowDataContainer container = step.TILE.getFlowDataContainer();
		Optional<CableNetwork> net = CableNetworkManager
			.getOrRegisterNetwork(step.TILE);
		if (!net.isPresent()) {
			return;
		}
		Optional<ItemConditionRuleFlowData> rule = container
			.get(this.rule, ItemConditionRuleFlowData.class);
		if (!rule.isPresent()) {
			return;
		}

		// set result to be used when branching later in {@code getNextUsingRelationships}
		result = isSatisfied(step, container, net.get(), rule.get());
	}

	@Override
	public ItemConditionFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		ItemConditionFlowData dupe = new ItemConditionFlowData(this);
		container.get(dupe.rule, ItemConditionRuleFlowData.class)
			.ifPresent(data -> {
				FlowData newRule = data.duplicate(container, dependencyTracker);
				dependencyTracker.accept(newRule);
				dupe.rule = newRule.getId();
			});

		// create line nodes and their relationships
		ConditionLineNodeFlowData acceptedNode = new ConditionLineNodeFlowData(
			Result.ACCEPTED);
		dependencyTracker.accept(acceptedNode);
		ConditionLineNodeFlowData rejectedNode = new ConditionLineNodeFlowData(
			Result.REJECTED);
		dependencyTracker.accept(rejectedNode);
		RelationshipFlowData acceptedRel = new RelationshipFlowData(
			dupe.getId(), acceptedNode.getId());
		dependencyTracker.accept(acceptedRel);
		RelationshipFlowData rejectedRel = new RelationshipFlowData(
			dupe.getId(), rejectedNode.getId());
		dependencyTracker.accept(rejectedRel);

		// ugly hack to initialize node positions to an offset of parent position
		dupe.position = new Position() {
			@Override
			public void setXY(int x, int y) {
				super.setXY(x, y);
				acceptedNode.position.setXY(dupe.getPosition()
					.withOffset(-ButtonBackground.LINE_NODE.WIDTH, ButtonBackground.NORMAL.HEIGHT));
				rejectedNode.position.setXY(dupe.getPosition()
					.withOffset(ButtonBackground.NORMAL.WIDTH, ButtonBackground.NORMAL.HEIGHT));
			}
		};
		return dupe;
	}

	@Override
	public boolean isValidRelationshipTarget() {
		return true;
	}

	@Override
	public Stream<ConditionLineNodeFlowData> getNextUsingRelationships(BasicFlowDataContainer container) {
		return super.getNextUsingRelationships(container)
			.filter(ConditionLineNodeFlowData.class::isInstance)
			.map(ConditionLineNodeFlowData.class::cast)
			.filter(node -> node.responsibility == result);
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (parent instanceof ManagerFlowController) {
			return new ItemConditionFlowButton(
				(ManagerFlowController) parent,
				this,
				((ManagerFlowController) parent).SCREEN.getFlowDataContainer()
					.get(rule, ItemConditionRuleFlowData.class)
					.orElseGet(ItemConditionRuleFlowData::new)
			);
		}
		return null;
	}

	@Override
	public Set<Class<?>> getDependencies() {
		return ImmutableSet.of(ItemConditionRuleFlowData.class);
	}

	@Override
	public FlowDataSerializer<ItemConditionFlowData> getSerializer() {
		return FlowDataSerializers.ITEM_CONDITION;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	private Result isSatisfied(
		ExecutionStep step,
		BasicFlowDataContainer container,
		CableNetwork network,
		ItemConditionRuleFlowData rule
	) {
		List<MovementMatcher> matchers = rule.itemMatcherIds.stream()
			.map(container::get)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(MovementMatcher.class::isInstance)
			.map(MovementMatcher.class::cast)
			.collect(Collectors.toList());

		List<IItemHandler> handlers = rule.getItemHandlers(container, network);

		if (matchers.size() == 0 || handlers.size() == 0) {
			return Result.ACCEPTED;
		}

		// for each inv
		for (IItemHandler handler : handlers) {
			// track remaining item matchers
			Set<MovementMatcher> unsatisfied = new HashSet<>(matchers);
			// for each slot in inv
			for (int slot : rule.slots.getSlots(handler.getSlots()).toArray()) {
				// get stack in slot
				ItemStack stack = handler.getStackInSlot(slot);
				// skip empty slots
				if (stack.isEmpty()) {
					continue;
				}
				// check if satisfied any matchers
				boolean satisfiedAny = unsatisfied.removeIf(matcher -> matcher.matches(stack));
				// if satisfied item-any, stop iterating slots
				if (satisfiedAny && rule.itemMode == ItemMode.MATCH_ANY) {
					break;
				}
				// if satisfied item-all, stop iterating slots
				if (unsatisfied.size() == 0) {
					break;
				}
				// if satisfied item-any and tile-any, short circuit
				if (rule.tileMode == TileMode.MATCH_ANY && satisfiedAny) {
					return Result.ACCEPTED;
				}
			}
			// if failed to satisfy item-any, reject
			if (unsatisfied.size() == matchers.size() && rule.itemMode == ItemMode.MATCH_ANY) {
				return Result.REJECTED;
			}
			// if failed to satisfy item-all, reject
			if (rule.itemMode == ItemMode.MATCH_ALL && unsatisfied.size() > 0) {
				return Result.REJECTED;
			}
		}
		// never rejected, so should be acceptable
		return Result.ACCEPTED;
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
	}

	public static class Serializer extends FlowDataSerializer<ItemConditionFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public ItemConditionFlowData fromNBT(CompoundNBT tag) {
			return new ItemConditionFlowData(
				getUUID(tag),
				new Position(tag.getCompound("pos")),
				UUID.fromString(tag.getString("tileEntityRule"))
			);
		}

		@Override
		public CompoundNBT toNBT(ItemConditionFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("tileEntityRule", data.rule.toString());
			return tag;
		}

		@Override
		public ItemConditionFlowData fromBuffer(PacketBuffer buf) {
			return new ItemConditionFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void toBuffer(ItemConditionFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			SFMUtil.writeUUID(data.rule, buf);
		}
	}
}
