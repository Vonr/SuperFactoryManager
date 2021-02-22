package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstackcomparermatcher.ItemStackComparerMatcherFlowComponent;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemStackComparerMatcherFlowData extends FlowData implements ItemStackMatcher {

	public ItemStack stack;
	public int quantity;
	public boolean open;


	public ItemStackComparerMatcherFlowData(UUID uuid, ItemStack stack, int quantity, boolean open) {
		super(uuid);
		this.stack = stack;
		this.quantity = quantity;
		this.open = open;
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
		FlowDataSerializer<ItemStackComparerMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemStackComparerMatcherFlowData fromNBT(CompoundNBT tag) {
			return new ItemStackComparerMatcherFlowData(
				UUID.fromString(tag.getString("uuid")),
				ItemStack.read(tag.getCompound("stack")),
				tag.getInt("quantity"),
				tag.getBoolean("open")

			);
		}

		@Override
		public CompoundNBT toNBT(ItemStackComparerMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putInt("quantity", data.quantity);
			tag.put("stack", data.stack.serializeNBT());
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public ItemStackComparerMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new ItemStackComparerMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readItemStack(),
				buf.readInt(),
				buf.readBoolean()

			);
		}

		@Override
		public void toBuffer(ItemStackComparerMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeItemStack(data.stack);
			buf.writeInt(data.quantity);
			buf.writeBoolean(data.open);
		}
	}
}
