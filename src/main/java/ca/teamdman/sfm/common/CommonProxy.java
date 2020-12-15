/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common;

import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.network.NetworkEvent;

public class CommonProxy {
	public <T extends Screen> Optional<T> getScreenFromPacket(IWindowIdProvider packet, Supplier<NetworkEvent.Context> ctx, Class<T> screenClass) {
		return Optional.empty();
	}

	public void fillItemGroup(ItemGroup group, Item[] items) {
	}
}
