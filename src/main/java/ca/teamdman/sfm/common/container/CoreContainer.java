package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import com.google.common.eventbus.EventBus;
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

public abstract class CoreContainer<T> extends Container implements INBTSerializable<CompoundNBT> {
	private final List<IFlowController> CONTROLLERS = new ArrayList<>();
	private final EventBus              EVENT_BUS   = new EventBus();
	private final Field                 LISTENERS   = ObfuscationReflectionHelper
			.findField(Container.class, "listeners");

	public T getSource() {
		return SOURCE;
	}

	private final T SOURCE;

	public CoreContainer(ContainerType type, int windowId, T source, boolean isRemote) {
		super(type, windowId);
		this.SOURCE = source;
		if (isRemote) {
			gatherControllers(CONTROLLERS::add);
		}
	}

	public abstract void gatherControllers(Consumer<IFlowController> c);

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

	@Override
	public CompoundNBT serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}
}
