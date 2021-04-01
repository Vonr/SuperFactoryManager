package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.ItemStackSearchIndexer;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ItemModMatcherFlowComponent;
import ca.teamdman.sfm.common.flow.core.ItemMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
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

public class ItemModMatcherFlowData extends FlowData implements ItemMatcher {

	public String modId;
	public int quantity;
	public boolean open;
	private List<ItemStack> preview;


	public ItemModMatcherFlowData(ItemModMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.modId,
			other.quantity,
			other.open
		);
	}

	public ItemModMatcherFlowData(UUID uuid, String modId, int quantity, boolean open) {
		super(uuid);
		this.modId = modId;
		this.quantity = quantity;
		this.open = open;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(new FlowDataHolderObserver<>(
			ItemRuleFlowData.class,
			data -> data.itemMatcherIds.contains(getId()),
			data -> this.open &= data.open // only keep this open if holder is also open
		));
	}

	@Override
	public ItemModMatcherFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new ItemModMatcherFlowData(this);
	}

	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new ItemModMatcherFlowComponent(
				((ManagerFlowController) parent),
				this
			);
		}
		return null;
	}

	@Override
	public FlowDataSerializer<ItemModMatcherFlowData> getSerializer() {
		return FlowDataSerializers.ITEM_MOD_MATCHER;
	}

	public static class Serializer extends
		FlowDataSerializer<ItemModMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemModMatcherFlowData fromNBT(CompoundNBT tag) {
			return new ItemModMatcherFlowData(
				getUUID(tag),
				tag.getString("modId"),
				tag.getInt("quantity"),
				tag.getBoolean("open")

			);
		}

		@Override
		public CompoundNBT toNBT(ItemModMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putInt("quantity", data.quantity);
			tag.putString("modId", data.modId);
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public ItemModMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new ItemModMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(64),
				buf.readInt(),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(ItemModMatcherFlowData data, PacketBuffer buf) {
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
