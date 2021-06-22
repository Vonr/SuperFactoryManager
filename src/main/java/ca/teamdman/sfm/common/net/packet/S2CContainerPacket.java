package ca.teamdman.sfm.common.net.packet;

import ca.teamdman.sfm.SFM;
import java.util.function.Supplier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class S2CContainerPacket<SCREEN extends Screen> implements IWindowIdProvider {
	public final Class<SCREEN> SCREEN_CLASS;
	final int WINDOW_ID;

	public S2CContainerPacket(
		Class<SCREEN> screen_class,
		int WINDOW_ID
	) {
		SCREEN_CLASS = screen_class;
		this.WINDOW_ID = WINDOW_ID;
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}

	public abstract static class S2CContainerPacketHandler<SCREEN extends Screen, MSG extends S2CContainerPacket<SCREEN>> {
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
			SFM.PROXY.getScreenFromPacket(msg, ctx, msg.SCREEN_CLASS)
				.ifPresent(screen -> handleDetailed(screen, msg));
			ctx.get().setPacketHandled(true);
		}

		public abstract void handleDetailed(SCREEN screen, MSG msg);
	}
}
