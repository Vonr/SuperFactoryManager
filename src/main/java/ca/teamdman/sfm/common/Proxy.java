package ca.teamdman.sfm.common;

import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface Proxy {

	default <T extends Screen> Optional<T> getScreenFromPacket(
		IWindowIdProvider packet,
		Supplier<Context> ctx,
		Class<T> screenClass
	) {
		return Optional.empty();
	}

	default void fillItemGroup(ItemGroup group, Item[] items) {
	}

	default void registerScreens() {
	}
}
