/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client;

import ca.teamdman.sfm.client.gui.screen.ConfigScreen;
import ca.teamdman.sfm.common.Proxy;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientProxy implements Proxy {

	@Override
	public <T extends Screen> Optional<T> getScreenFromPacket(
		IWindowIdProvider packet,
		Supplier<NetworkEvent.Context> ctx,
		Class<T> screenClass
	) {
		if (packet.getWindowId() == 0) {
			return Optional.empty();
		}
		if (packet.getWindowId() != Minecraft.getInstance().player.openContainer.windowId) {
			return Optional.empty();
		}
		if (!screenClass.isInstance(Minecraft.getInstance().currentScreen)) {
			return Optional.empty();
		}
		//noinspection unchecked
		return Optional.of((T) Minecraft.getInstance().currentScreen);
	}

	@Override
	public void fillItemGroup(ItemGroup group, Item[] items) {
		group.fill(Arrays.stream(items)
			.map(ItemStack::new)
			.collect(NonNullList::create, NonNullList::add, NonNullList::addAll));
	}

	@Override
	public void registerScreens() {
		ModLoadingContext.get().registerExtensionPoint(
			ExtensionPoint.CONFIGGUIFACTORY,
			() -> (mc, screen) -> new ConfigScreen(
				new TranslationTextComponent("gui.sfm.config.title"),
				512,
				256
			)
		);
	}

}
