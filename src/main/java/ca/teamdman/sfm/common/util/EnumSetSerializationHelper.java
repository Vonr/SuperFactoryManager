package ca.teamdman.sfm.common.util;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants.NBT;

public class EnumSetSerializationHelper {

	public static ListNBT serialize(EnumSet<?> set) {
		return set.stream()
			.map(Enum::name)
			.map(StringNBT::valueOf)
			.collect(ListNBT::new, ListNBT::add, ListNBT::addAll);
	}

	public static <T extends Enum<T>> EnumSet<T> deserialize(
		CompoundNBT tag,
		String key,
		Function<String, T> resolver
	) {
		return EnumSet.copyOf(
			tag.getList(key, NBT.TAG_STRING).stream()
				.map(INBT::getString)
				.map(resolver)
				.collect(Collectors.toList())
		);
	}

	public static void serialize(EnumSet<?> set, PacketBuffer buf) {
		buf.writeInt(set.size());
		set.stream()
			.map(Enum::name)
			.forEach(name -> buf.writeString(name, 128));
	}

	public static <T extends Enum<T>> EnumSet<T> deserialize(
		PacketBuffer buf,
		Function<String, T> resolver
	) {
		return EnumSet.copyOf(
			IntStream.range(0, buf.readInt())
				.mapToObj(__ -> buf.readString(128))
				.map(resolver)
				.collect(Collectors.toList())
		);
	}
}
