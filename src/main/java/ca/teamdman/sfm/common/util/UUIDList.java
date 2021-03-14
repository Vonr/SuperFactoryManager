package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants.NBT;

public class UUIDList extends ArrayList<UUID> {

	public UUIDList(Collection<? extends UUID> c) {
		super(c);
	}

	public UUIDList(PacketBuffer buf) {
		IntStream.range(0, buf.readInt())
			.mapToObj(__ -> SFMUtil.readUUID(buf))
			.forEach(this::add);
	}

	public UUIDList(CompoundNBT tag, String key) {
		tag.getList(key, NBT.TAG_STRING).stream()
			.map(INBT::getString)
			.map(UUID::fromString)
			.forEach(this::add);
	}

	public ListNBT serialize() {
		return stream()
			.map(UUID::toString)
			.map(StringNBT::valueOf)
			.collect(ListNBT::new, ListNBT::add, ListNBT::addAll);
	}

	public void serialize(PacketBuffer buf) {
		buf.writeInt(size());
		forEach(id -> SFMUtil.writeUUID(id, buf));
	}

	public <T> Stream<T> lookup(BasicFlowDataContainer container, Class<T> type) {
		return stream()
			.map(id -> container.get(id, type))
			.filter(Optional::isPresent)
			.map(Optional::get);
	}
}
