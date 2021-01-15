package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstackmatcher.ItemStackComparerMatcherFlowComponent;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemStackComparerMatcherFlowData extends FlowData implements ItemStackMatcher {

	public ItemStack stack;
	public int quantity;

	public ItemStackComparerMatcherFlowData(UUID uuid, ItemStack stack, int quantity) {
		super(uuid);
		this.stack = stack;
		this.quantity = quantity;
	}

	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new ItemStackComparerMatcherFlowComponent(
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
		return quantity;
	}

	@Override
	public List<ItemStack> getPreview() {
		return Collections.singletonList(stack);
	}

	public static class ItemStackComparerMatcherFlowDataSerializer extends
		FlowDataSerializer<ItemStackComparerMatcherFlowData> {

		public ItemStackComparerMatcherFlowDataSerializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemStackComparerMatcherFlowData fromNBT(CompoundNBT tag) {
			return new ItemStackComparerMatcherFlowData(
				UUID.fromString(tag.getString("uuid")),
				ItemStack.read(tag.getCompound("stack")),
				tag.getInt("quantity")
			);
		}

		@Override
		public CompoundNBT toNBT(ItemStackComparerMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putInt("quantity", data.quantity);
			tag.put("stack", data.stack.serializeNBT());
			return tag;
		}

		@Override
		public ItemStackComparerMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new ItemStackComparerMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readItemStack(),
				buf.readInt()
			);
		}

		@Override
		public void toBuffer(ItemStackComparerMatcherFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeItemStack(data.stack);
			buf.writeInt(data.quantity);
		}
	}
}
