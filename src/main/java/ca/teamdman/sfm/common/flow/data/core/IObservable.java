/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
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
