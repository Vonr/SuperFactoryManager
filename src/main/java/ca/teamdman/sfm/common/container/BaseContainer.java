package ca.teamdman.sfm.common.container;

import java.lang.reflect.Field;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BaseContainer<T> extends Container {

	public final boolean IS_REMOTE;
	private final Field LISTENERS = ObfuscationReflectionHelper
		.findField(Container.class, "listeners");
	private final T SOURCE;

	public BaseContainer(ContainerType type, int windowId, T source, boolean isRemote) {
		super(type, windowId);
		this.SOURCE = source;
		this.IS_REMOTE = isRemote;
	}

	public T getSource() {
		return SOURCE;
	}

//	public void forEachPlayerWithContainerOpened(Consumer<ServerPlayerEntity> playerConsumer) {
//		try {
//			//noinspection unchecked
//			List<IContainerListener> listeners = (List<IContainerListener>) LISTENERS.get(this);
//			for (IContainerListener v : listeners) {
//				if (v instanceof ServerPlayerEntity) {
//					playerConsumer.accept(((ServerPlayerEntity) v));
//				}
//			}
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}

	//	@Override
//	public void detectAndSendChanges() {
//		//		super.detectAndSendChanges(); // no item slots, no need for super
//		if (this.x != getSource().x || this.y != getSource().y) {
//			this.x = getSource().x;
//			this.y = getSource().y;
//	forEachPlayerWithContainerOpened(p -> {
//				PacketHandler.INSTANCE.send(
//						PacketDistributor.PLAYER.with(() -> p),
//						new ButtonPositionPacketS2C(windowId, 0, x, y));
//	});
//		}
//	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

}
