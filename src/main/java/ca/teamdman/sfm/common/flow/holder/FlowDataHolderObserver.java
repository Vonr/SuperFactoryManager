package ca.teamdman.sfm.common.flow.holder;

import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerClosedEvent;
import java.util.Observable;
import java.util.Observer;

public class FlowDataHolderObserver<T extends FlowData> implements Observer {

	private final FlowDataHolder<T> HOLDER;
	private final Class<T> CLAZZ;

	public FlowDataHolderObserver(FlowDataHolder<T> holder, Class<T> clazz) {
		this.HOLDER = holder;
		this.CLAZZ = clazz;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			if (change.CHANGE == ChangeType.REMOVED) {
				o.deleteObserver(this);
			} else {
				if (CLAZZ.isInstance(change.DATA)) {
					if (HOLDER.getData().getId().equals(change.DATA.getId())) {
						HOLDER.setData(CLAZZ.cast(change.DATA));
					}
				}
			}
		} else if (arg instanceof FlowDataContainerClosedEvent) {
			o.deleteObserver(this);
		}
	}
}
