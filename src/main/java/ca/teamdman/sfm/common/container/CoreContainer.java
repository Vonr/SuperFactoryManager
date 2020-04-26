package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.container.core.controller.ButtonController;
import ca.teamdman.sfm.common.container.core.controller.CommandController;
import ca.teamdman.sfm.common.container.core.controller.PositionController;
import ca.teamdman.sfm.common.container.core.controller.RelationshipController;
import ca.teamdman.sfm.common.flow.core.FlowComponent;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CoreContainer<T> extends Container implements INBTSerializable<CompoundNBT> {
	private final List<FlowComponent>    COMPONENTS              = new ArrayList<>();
	public final  ButtonController       BUTTON_CONTROLLER       = new ButtonController(this);
	public final  CommandController      COMMAND_CONTROLLER      = new CommandController(this);
	public final  PositionController     POSITION_CONTROLLER     = new PositionController(this);
	public final  RelationshipController RELATIONSHIP_CONTROLLER = new RelationshipController(this);
	private final Field                  LISTENERS               = ObfuscationReflectionHelper
			.findField(Container.class, "listeners");
	private final T                      SOURCE;
	private       int                    tick                    = 0;

	public CoreContainer(ContainerType type, int windowId, T source) {
		super(type, windowId);
		this.SOURCE = source;
	}

	public void addComponent(FlowComponent c) {
		COMPONENTS.add(c);
	}

	@Override
	public void detectAndSendChanges() {
		if (++tick % 100 != 0)
			return;
		try {

			//noinspection unchecked
			List<IContainerListener> listeners = (List<IContainerListener>) LISTENERS.get(this);
			for (IContainerListener v : listeners) {
				if (v instanceof ServerPlayerEntity) {
					PacketHandler.INSTANCE.send(
							PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) v),
							new ManagerUpdatePacket("BEANS")
					);
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
