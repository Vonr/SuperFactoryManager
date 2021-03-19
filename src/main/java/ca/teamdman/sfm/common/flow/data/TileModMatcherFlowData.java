package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.TileModMatcherFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class TileModMatcherFlowData extends FlowData implements TileMatcher {

	public String modId;
	public boolean open;
	private transient List<ItemStack> preview;


	public TileModMatcherFlowData(TileModMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.modId,
			other.open
		);
	}

	public TileModMatcherFlowData(UUID uuid, String modId, boolean open) {
		super(uuid);
		this.modId = modId;
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
	public TileModMatcherFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new TileModMatcherFlowData(this);
	}

	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new TileModMatcherFlowComponent(
				((ManagerFlowController) parent),
				this
			);
		}
		return null;
	}

	@Override
	public FlowDataSerializer<TileModMatcherFlowData> getSerializer() {
		return FlowDataSerializers.TILE_MOD_MATCHER;
	}

	@Override
	public boolean matches(@Nonnull TileEntity tile) {
		return tile.getType().getRegistryName().getNamespace().equals(modId);
	}

	@Override
	public List<ItemStack> getPreview(CableNetwork network) {
		if (preview == null) {
			return preview = network.getInventories().stream()
				.filter(this::matches)
				.map(TileEntity::getPos)
				.map(network::getPreview)
				.collect(Collectors.toList());
		}
		return preview;
	}

	@Override
	public List<? extends ITextProperties> getTooltip(List<? extends ITextProperties> normal) {
		List<ITextProperties> rtn = new ArrayList<>(normal);
		rtn.add(
			1,
			new StringTextComponent(I18n.format("gui.sfm.flow.tooltip.tile_mod_matcher"))
				.mergeStyle(TextFormatting.GRAY)
		);
		rtn.add(
			2,
			new StringTextComponent(modId)
				.mergeStyle(TextFormatting.GRAY)
		);
		return rtn;
	}


	@Override
	public boolean isVisible() {
		return open;
	}

	@Override
	public void setVisibility(boolean open) {
		this.open = open;
	}

	public static class Serializer extends
		FlowDataSerializer<TileModMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public TileModMatcherFlowData fromNBT(CompoundNBT tag) {
			return new TileModMatcherFlowData(
				UUID.fromString(tag.getString("uuid")),
				tag.getString("modId"),
				tag.getBoolean("open")

			);
		}

		@Override
		public CompoundNBT toNBT(TileModMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putString("modId", data.modId);
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public TileModMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new TileModMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(64),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(TileModMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeString(data.modId, 64);
			buf.writeBoolean(data.open);
		}
	}
}
