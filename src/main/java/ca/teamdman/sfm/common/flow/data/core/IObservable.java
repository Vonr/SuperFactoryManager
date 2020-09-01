package ca.teamdman.sfm.common.flow.data.core;

import java.util.ArrayList;

public interface IObservable {

	ArrayList<Runnable> listeners = new ArrayList<>();

	default void notifyChange() {
		listeners.forEach(Runnable::run);
	}

	default void subscribeToChanges(Runnable r) {
		listeners.add(r);
	}
}
