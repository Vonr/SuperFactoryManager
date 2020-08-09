package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class BaseContainer<T> extends Container {
	private final List<IFlowController> CONTROLLERS = new ArrayList<>();
	private final Field                 LISTENERS   = ObfuscationReflectionHelper
			.findField(Container.class, "listeners");
	private final T                     SOURCE;
	public final boolean IS_REMOTE;
	public BaseContainer(ContainerType type, int windowId, T source, boolean isRemote) {
		super(type, windowId);
		this.SOURCE = source;
		this.IS_REMOTE = isRemote;
	}

	public void init() {
		if (IS_REMOTE) {
			gatherControllers(CONTROLLERS::add);
		}
	}

	public abstract void gatherControllers(Consumer<IFlowController> c);

	public T getSource() {
		return SOURCE;
	}

	public Stream<IFlowView> getViews() {
		return getControllers()
				.map(IFlowController::getView);
	}

	public Stream<IFlowController> getControllers() {
		return this.CONTROLLERS.stream();
	}

	public void forEachPlayerWithContainerOpened(Consumer<ServerPlayerEntity> playerConsumer) {
		try {
			//noinspection unchecked
			List<IContainerListener> listeners = (List<IContainerListener>) LISTENERS.get(this);
			for (IContainerListener v : listeners) {
				if (v instanceof ServerPlayerEntity) {
					playerConsumer.accept(((ServerPlayerEntity) v));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

}
