/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow;

import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.UUID;

public class FlowUtils {

	/**
	 * Creates a line node between two elements
	 *
	 * @param holder     Data holder
	 * @param from       Start element ID
	 * @param to         End element ID
	 * @param elementPos Line node position
	 */
	public static void insertLineNode(
		BasicFlowDataContainer holder,
		UUID from,
		UUID to,
		UUID nodeId,
		UUID fromToNodeId,
		UUID toToNodeId,
		Position elementPos
	) {
		// Create the line node
		LineNodeFlowData nodeData = new LineNodeFlowData(nodeId, elementPos);

		// Create relationships to and from the node
		RelationshipFlowData startToNode = new RelationshipFlowData(
			fromToNodeId,
			from,
			nodeData.getId()
		);
		RelationshipFlowData nodeToEnd = new RelationshipFlowData(
			toToNodeId,
			nodeData.getId(),
			to
		);

		// Add new data to holder
		nodeData.addToDataContainer(holder);
		startToNode.addToDataContainer(holder);
		nodeToEnd.addToDataContainer(holder);

		// Remove existing relationship between FROM & TO
		holder.removeIf(data -> data instanceof RelationshipFlowData
			&& ((RelationshipFlowData) data).from.equals(from)
			&& ((RelationshipFlowData) data).to.equals(to)
		);
	}

}
