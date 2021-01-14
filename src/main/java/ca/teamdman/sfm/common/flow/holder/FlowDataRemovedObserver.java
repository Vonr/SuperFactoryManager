package ca.teamdman.sfm.common.flow.holder;

import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Predicate;

public class FlowDataRemovedObserver implements Observer {

	private final FlowData PARENT;
	private final Predicate<FlowData> PREDICATE;

	public FlowDataRemovedObserver(FlowData parent, Predicate<FlowData> predicate) {
		PARENT = parent;
		this.PREDICATE = predicate;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange && o instanceof BasicFlowDataContainer) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			BasicFlowDataContainer container = (BasicFlowDataContainer) o;
			if (change.CHANGE == ChangeType.REMOVED) {
				if (PREDICATE.test(change.DATA)) {
					container.notifyChanged(PARENT);
				}
				if (change.DATA.getId().equals(PARENT.getId())) {
					container.deleteObserver(this);
				}
			}
		}
//		else if (arg instanceof FlowDataContainerClosedEvent) {
//			o.deleteObserver(this);
//		}
	}
}
