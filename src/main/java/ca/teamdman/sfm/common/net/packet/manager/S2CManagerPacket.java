/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class S2CManagerPacket implements IWindowIdProvider {

	final int WINDOW_ID;

	public S2CManagerPacket(int WINDOW_ID) {
		this.WINDOW_ID = WINDOW_ID;
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}

	public abstract static class S2CHandler<MSG extends S2CManagerPacket> {
		public void encode(MSG msg, PacketBuffer buf) {
			buf.writeInt(msg.WINDOW_ID);
			finishEncode(msg, buf);
		}

		public abstract void finishEncode(MSG msg, PacketBuffer buf);

		public MSG decode(PacketBuffer buf) {
			return finishDecode(
				buf.readInt(),
				buf
			);
		}

		public abstract MSG finishDecode(int windowId, PacketBuffer buf);

		public void handle(MSG msg, Supplier<Context> ctx) {
			SFM.PROXY.getScreenFromPacket(msg, ctx, ManagerScreen.class)
				.ifPresent(screen -> handleDetailed(screen, msg));
			ctx.get().setPacketHandled(true);
		}

		public abstract void handleDetailed(ManagerScreen screen, MSG msg);
	}
}
