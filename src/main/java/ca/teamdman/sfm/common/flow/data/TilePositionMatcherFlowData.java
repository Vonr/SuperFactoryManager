package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher.TilePositionMatcherFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class TilePositionMatcherFlowData extends FlowData implements TileMatcher {

	public BlockPos position;
	public boolean open;
	private transient List<ItemStack> previewCache;

	public TilePositionMatcherFlowData(TilePositionMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.position,
			false
		);
	}

	public TilePositionMatcherFlowData(UUID uuid, BlockPos position, boolean open) {
		super(uuid);
		this.position = position;
		this.open = open;
	}

	@Override
	public boolean matches(@Nonnull TileEntity tile) {
		return Objects.equals(tile.getPos(), position);
	}

	@Override
	public List<ItemStack> getPreview(CableNetwork network) {
		if (previewCache == null) {
			if (network.containsNeighbour(position)) {
				previewCache = Collections.singletonList(network.getPreview(position));
			} else {
				previewCache = Collections.singletonList(new ItemStack(Blocks.BARRIER));
			}
		}
		return previewCache;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(new FlowDataHolderObserver<>(
			ItemMovementRuleFlowData.class,
			data -> data.tileMatcherIds.contains(getId()),
			data -> this.open &= data.open // only keep this open if holder is also open
		));
	}

	@Override
	public List<? extends ITextProperties> getTooltip(List<? extends ITextProperties> normal) {
		List<ITextProperties> rtn = new ArrayList<>(normal);
		rtn.add(
			1,
			new StringTextComponent(I18n.format("gui.sfm.flow.tooltip.tile_position_matcher"))
				.mergeStyle(TextFormatting.GRAY)
		);
		rtn.add(
			2,
			new StringTextComponent(I18n.format(
				"gui.sfm.flow.tooltip.block_pos",
				position.getX(),
				position.getY(),
				position.getZ()
			))
				.mergeStyle(TextFormatting.GRAY)
		);
		return rtn;
	}

	@Override
	public FlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new TilePositionMatcherFlowData(this);
	}

	@Nullable
	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new TilePositionMatcherFlowComponent(
				((ManagerFlowController) parent),
				this
			);
		}
		return null;
	}

	@Override
	public FlowDataSerializer<TilePositionMatcherFlowData> getSerializer() {
		return FlowDataSerializers.TILE_POSITION_MATCHER;
	}

	@Override
	public boolean isVisible() {
		return open;
	}

	@Override
	public void setVisibility(boolean open) {
		this.open = open;
	}


	public static class Serializer extends FlowDataSerializer<TilePositionMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public TilePositionMatcherFlowData fromNBT(CompoundNBT tag) {
			return new TilePositionMatcherFlowData(
				getUUID(tag),
				NBTUtil.readBlockPos(tag.getCompound("pos")),
				tag.getBoolean("open")
			);
		}

		@Override
		public CompoundNBT toNBT(TilePositionMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", NBTUtil.writeBlockPos(data.position));
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public TilePositionMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new TilePositionMatcherFlowData(
				SFMUtil.readUUID(buf),
				BlockPos.fromLong(buf.readLong()),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(TilePositionMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			buf.writeBoolean(data.open);
		}
	}
}
