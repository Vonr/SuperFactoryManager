package ca.teamdman.sfm.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public class BlockPosList extends ArrayList<BlockPos> {

	public BlockPosList(Collection<? extends BlockPos> c) {
		super(c);
	}

	public BlockPosList(PacketBuffer buf) {
		IntStream.range(0, buf.readInt())
			.mapToLong(__ -> buf.readLong())
			.mapToObj(BlockPos::fromLong)
			.forEach(this::add);
	}

	public BlockPosList(CompoundNBT tag, String key) {
		tag.getList(key, NBT.TAG_LONG).stream()
			.map(LongNBT.class::cast)
			.map(LongNBT::getLong)
			.map(BlockPos::fromLong)
			.forEach(this::add);
	}

	public ListNBT serialize() {
		return stream()
			.map(BlockPos::toLong)
			.map(LongNBT::valueOf)
			.collect(ListNBT::new, ListNBT::add, ListNBT::addAll);
	}

	public void serialize(PacketBuffer buf) {
		buf.writeInt(size());
		forEach(id -> buf.writeLong(id.toLong()));
	}
}
