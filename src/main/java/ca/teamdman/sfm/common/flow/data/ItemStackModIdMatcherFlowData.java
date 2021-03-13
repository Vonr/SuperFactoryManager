package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.ItemStackSearchIndexer;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ItemStackModIdMatcherFlowComponent;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemStackModIdMatcherFlowData extends FlowData implements ItemStackMatcher {

	public String modId;
	public int quantity;
	public boolean open;
	private List<ItemStack> preview;


	public ItemStackModIdMatcherFlowData(ItemStackModIdMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.modId,
			other.quantity,
			other.open
		);
	}

	public ItemStackModIdMatcherFlowData(UUID uuid, String modId, int quantity, boolean open) {
		super(uuid);
		this.modId = modId;
		this.quantity = quantity;
		this.open = open;
	}

	@Override
	public ItemStackModIdMatcherFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new ItemStackModIdMatcherFlowData(this);
	}

	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new ItemStackModIdMatcherFlowComponent(
				((ManagerFlowController) parent),
				this
			);
		}
		return null;
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.ITEM_STACK_MOD_ID_MATCHER;
	}

	public static class Serializer extends
		FlowDataSerializer<ItemStackModIdMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemStackModIdMatcherFlowData fromNBT(CompoundNBT tag) {
			return new ItemStackModIdMatcherFlowData(
				UUID.fromString(tag.getString("uuid")),
				tag.getString("modId"),
				tag.getInt("quantity"),
				tag.getBoolean("open")

			);
		}

		@Override
		public CompoundNBT toNBT(ItemStackModIdMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putInt("quantity", data.quantity);
			tag.putString("modId", data.modId);
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public ItemStackModIdMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new ItemStackModIdMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(64),
				buf.readInt(),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(ItemStackModIdMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeString(data.modId, 64);
			buf.writeInt(data.quantity);
			buf.writeBoolean(data.open);
		}
	}

	@Override
	public boolean matches(@Nonnull ItemStack stack) {
		return stack.getItem().getRegistryName().getNamespace().equals(modId);
	}

	@Override
	public int getQuantity() {
		return quantity == 0 ? Integer.MAX_VALUE : quantity;
	}


	@Override
	public List<ItemStack> getPreview() {
		if (preview == null) {
			// only fetch preview when not on server
			return preview = ItemStackSearchIndexer.getSearchableItems()
				.filter(this::matches)
				.limit(100)
				.collect(Collectors.toList());
		} else {
			return preview;
		}
	}

	@Override
	public String getMatcherDisplayName() {
		return I18n.format("gui.sfm.flow.tooltip.item_stack_mod_id_matcher");
	}


}
