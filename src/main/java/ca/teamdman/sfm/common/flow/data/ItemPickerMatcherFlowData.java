package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itempickermatcher.ItemPickerMatcherFlowComponent;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemPickerMatcherFlowData extends FlowData implements ItemStackMatcher {

	public ItemStack stack;
	public int quantity;
	public boolean open;


	public ItemPickerMatcherFlowData(ItemPickerMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.stack.copy(),
			other.quantity,
			other.open
		);
	}

	public ItemPickerMatcherFlowData(
		UUID uuid,
		ItemStack stack,
		int quantity,
		boolean open
	) {
		super(uuid);
		this.stack = stack;
		this.quantity = quantity;
		this.open = open;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(new FlowDataHolderObserver<>(
			ItemRuleFlowData.class,
			data -> data.matcherIds.contains(getId()),
			data -> this.open &= data.open // only keep this open if holder is also open
		));
	}

	@Override
	public ItemPickerMatcherFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new ItemPickerMatcherFlowData(this);
	}

	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new ItemPickerMatcherFlowComponent(
				((ManagerFlowController) parent),
				this
			);
		}
		return null;
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.ITEM_STACK_COMPARER_MATCHER;
	}


	@Override
	public boolean matches(@Nonnull ItemStack stack) {
		return this.stack.isItemEqual(stack);
	}

	@Override
	public int getQuantity() {
		return quantity == 0 ? Integer.MAX_VALUE : quantity;
	}

	@Override
	public List<ItemStack> getPreview() {
		return Collections.singletonList(stack);
	}

	@Override
	public String getMatcherDisplayName() {
		return I18n.format("gui.sfm.flow.tooltip.item_stack_comparer_matcher");
	}

	public static class Serializer extends
		FlowDataSerializer<ItemPickerMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemPickerMatcherFlowData fromNBT(CompoundNBT tag) {
			return new ItemPickerMatcherFlowData(
				UUID.fromString(tag.getString("uuid")),
				ItemStack.read(tag.getCompound("stack")),
				tag.getInt("quantity"),
				tag.getBoolean("open")

			);
		}

		@Override
		public CompoundNBT toNBT(ItemPickerMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putInt("quantity", data.quantity);
			tag.put("stack", data.stack.serializeNBT());
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public ItemPickerMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new ItemPickerMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readItemStack(),
				buf.readInt(),
				buf.readBoolean()

			);
		}

		@Override
		public void toBuffer(ItemPickerMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeItemStack(data.stack);
			buf.writeInt(data.quantity);
			buf.writeBoolean(data.open);
		}
	}
}
