package ca.teamdman.sfm.common.net.packet;

public interface IWindowIdProvider {
	/**
	 * @return The windowId that the sender of the packet should have open
	 */
	int getWindowId();
}
