package ca.teamdman.sfm.common.flow.holder;

import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerClosedClientEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FlowDataHolderObserver<T extends FlowData> implements Observer {

	private final Class<T> CLAZZ;
	private final Predicate<T> CHECK;
	private final Consumer<T> ACTION;

	public FlowDataHolderObserver(Class<T> clazz, Predicate<T> check, Consumer<T> action) {
		this.CLAZZ = clazz;
		this.CHECK = check;
		this.ACTION = action;
	}

	public FlowDataHolderObserver(Class<T> clazz, FlowDataHolder<T> holder) {
		// default behaviour - call setData
		this(
			clazz,
			data -> holder.getData().getId().equals(data.getId()),
			holder::setData
		);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			if (CLAZZ.isInstance(change.DATA)) {
				T data = CLAZZ.cast(change.DATA);
				if (CHECK.test(data)) {
					if (change.CHANGE == ChangeType.REMOVED) {
						o.deleteObserver(this);
					} else if (change.CHANGE == ChangeType.UPDATED) {
						ACTION.accept(data);
					}
				}
			}
		} else if (arg instanceof FlowDataContainerClosedClientEvent) {
			o.deleteObserver(this);
		}
	}
}
