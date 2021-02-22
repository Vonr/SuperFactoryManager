package ca.teamdman.sfm.common.flow.holder;

import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FlowDataRemovedObserver implements Observer {

	private final FlowData PARENT;
	private final Predicate<FlowData> PREDICATE;
	private final Consumer<BasicFlowDataContainer> ACTION;

	public FlowDataRemovedObserver(FlowData parent, Predicate<FlowData> predicate) {
		this(
			parent,
			predicate,
			c -> c.notifyChanged(parent)
		);
	}

	public FlowDataRemovedObserver(
		FlowData parent,
		Predicate<FlowData> predicate,
		Consumer<BasicFlowDataContainer> action
	) {
		this.PARENT = parent;
		this.PREDICATE = predicate;
		this.ACTION = action;
	}


	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange && o instanceof BasicFlowDataContainer) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			BasicFlowDataContainer container = (BasicFlowDataContainer) o;
			if (change.CHANGE == ChangeType.REMOVED) {
				if (PREDICATE.test(change.DATA)) {
					ACTION.accept(container);
				}
				if (change.DATA.getId().equals(PARENT.getId())) {
					container.deleteObserver(this);
				}
			}
		}
//		else if (arg instanceof FlowDataContainerClosedClientEvent) {
//			o.deleteObserver(this);
//		}
	}
}
