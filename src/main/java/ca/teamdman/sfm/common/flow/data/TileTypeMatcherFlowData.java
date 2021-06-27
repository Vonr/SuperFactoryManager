package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tiletypematcher.TileTypeMatcherFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

public class TileTypeMatcherFlowData extends FlowData implements TileMatcher {

	public TileEntityType<?> type;
	public boolean open;
	private transient List<ItemStack> previewCache;

	public TileTypeMatcherFlowData(TileTypeMatcherFlowData other) {
		this(
			UUID.randomUUID(),
			other.type,
			false
		);
	}

	public TileTypeMatcherFlowData(
		UUID uuid,
		TileEntityType<?> type,
		boolean open
	) {
		super(uuid);
		this.type = type;
		this.open = open;
	}

	@Override
	public boolean matches(@Nonnull TileEntity tile) {
		return tile.getType().equals(type);
	}

	@Override
	public List<ItemStack> getPreview(CableNetwork network) {
		// return cached list if available
		if (previewCache != null) return previewCache;

		// start building new cache
		previewCache = new LinkedList<>();

		// find all tiles matching our type
		network.getInventories().stream()
			.filter(this::matches)
			.map(TileEntity::getPos)
			.map(network::getPreview)
			.forEach(previewCache::add);

		// if no matches, use barrier block as preview
		if (previewCache.size() == 0) {
			previewCache.add(new ItemStack(Blocks.BARRIER));
		}

		// return newly cached list
		return previewCache;
	}

	@Override
	public List<? extends ITextProperties> getTooltip(List<? extends ITextProperties> normal) {
		List<ITextProperties> rtn = new ArrayList<>(normal);
		rtn.add(
			1,
			new StringTextComponent(I18n.get(
				"gui.sfm.flow.tile_type_matcher.tooltip.name"))
				.mergeStyle(TextFormatting.GRAY)
		);
		rtn.add(
			2,
			new StringTextComponent(I18n.get(
				"gui.sfm.flow.tile_type_matcher.tooltip.data",
				type.getRegistryName()
			))
				.mergeStyle(TextFormatting.GRAY)
		);
		return rtn;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(new FlowDataHolderObserver<>(
			ItemMovementRuleFlowData.class,
			data -> data.tileMatcherIds.contains(getId()),
			data -> this.open &= data.open
			// only keep this open if holder is also open
		));
	}

	@Override
	public FlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new TileTypeMatcherFlowData(this);
	}

	@Nullable
	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new TileTypeMatcherFlowComponent(
				((ManagerFlowController) parent),
				this
			);
		}
		return null;
	}

	@Override
	public FlowDataSerializer<TileTypeMatcherFlowData> getSerializer() {
		return FlowDataSerializers.TILE_TYPE_MATCHER;
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
		FlowDataSerializer<TileTypeMatcherFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public TileTypeMatcherFlowData fromNBT(CompoundNBT tag) {
			return new TileTypeMatcherFlowData(
				getUUID(tag),
				ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation(tag.getString("type"))),
				tag.getBoolean("open")
			);
		}

		@Override
		public CompoundNBT toNBT(TileTypeMatcherFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putString("type", data.type.getRegistryName().toString());
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public TileTypeMatcherFlowData fromBuffer(PacketBuffer buf) {
			return new TileTypeMatcherFlowData(
				SFMUtil.readUUID(buf),
				buf.readRegistryIdSafe(TileEntityType.class),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(TileTypeMatcherFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeRegistryId(data.type);
			buf.writeBoolean(data.open);
		}
	}
}
