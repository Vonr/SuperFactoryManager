package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TilePositionMatcherFlowData extends FlowData implements TileMatcher {

	public BlockPos position;

	public TilePositionMatcherFlowData(TilePositionMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.position
		);
	}

	public TilePositionMatcherFlowData(UUID uuid, BlockPos position) {
		super(uuid);
		this.position = position;
	}

	@Override
	public boolean matches(@Nonnull TileEntity tile) {
		return Objects.equals(tile.getPos(), position);
	}

	@Override
	public List<ItemStack> getPreview(CableNetwork network) {
		return Collections.singletonList(network.getPreview(position));
	}

	@Override
	public String getMatcherDisplayName() {
		return I18n.format("gui.sfm.flow.tooltip.tile_position_matcher");
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
		return null;
	}

	@Override
	public FlowDataSerializer<TilePositionMatcherFlowData> getSerializer() {
		return FlowDataSerializers.TILE_POSITION_MATCHER;
	}


	public static class Serializer extends FlowDataSerializer<TilePositionMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public TilePositionMatcherFlowData fromNBT(CompoundNBT tag) {
			return new TilePositionMatcherFlowData(
				UUID.fromString(tag.getString("uuid")),
				NBTUtil.readBlockPos(tag.getCompound("pos"))
			);
		}

		@Override
		public CompoundNBT toNBT(TilePositionMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", NBTUtil.writeBlockPos(data.position));
			return tag;
		}

		@Override
		public TilePositionMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new TilePositionMatcherFlowData(
				SFMUtil.readUUID(buf),
				BlockPos.fromLong(buf.readLong())
			);
		}

		@Override
		public void toBuffer(TilePositionMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
		}
	}
}
