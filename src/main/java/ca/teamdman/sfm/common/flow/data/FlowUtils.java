/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.impl.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.data.impl.RelationshipGraph;
import java.util.Optional;
import java.util.UUID;

public class FlowUtils {

	/**
	 * Gets the relationship data between two elements
	 *
	 * @param dataHolder Source of data
	 * @param from       Relationship start point
	 * @param to         Relationship end point
	 * @return Relationship if exists
	 */
	public static Optional<RelationshipFlowData> getRelationship(
		FlowDataContainer dataHolder,
		UUID from,
		UUID to
	) {
		return dataHolder.getData()
			.filter(data -> data instanceof RelationshipFlowData)
			.map(data -> ((RelationshipFlowData) data))
			.filter(data -> data.from.equals(from))
			.filter(data -> data.to.equals(to))
			.findAny();
	}

	/**
	 * Creates a line node between two elements
	 *
	 * @param holder     Data holder
	 * @param from       Start element ID
	 * @param to         End element ID
	 * @param elementPos Line node position
	 */
	public static void insertLineNode(
		FlowDataContainer holder,
		UUID from,
		UUID to,
		UUID nodeId,
		UUID fromToNodeId,
		UUID toToNodeId,
		Position elementPos
	) {
		// Remove existing relationship between FROM & TO
		getRelationship(holder, from, to).ifPresent(data -> holder.removeData(data.getId()));

		// Create node data
		LineNodeFlowData nodeData = new LineNodeFlowData(nodeId, elementPos);

		// Create relationship data
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

		// Add data to holder
		holder.addData(nodeData);
		holder.addData(startToNode);
		holder.addData(nodeToEnd);
	}

	public static RelationshipGraph getRelationshipGraph(FlowDataContainer holder) {
		RelationshipGraph graph = new RelationshipGraph();
		holder.getData()
			.filter(data -> data instanceof RelationshipFlowData)
			.map(data -> (RelationshipFlowData) data)
			.forEach(data -> holder.getData(data.from)
				.filter(__ -> holder.getData(data.to).isPresent())
				.ifPresent(from -> {
					graph.addNode(from);
					graph.addNode(holder.getData(data.to).get());
					graph.putEdge(data.getId(), data.from, data.to);
				}));
		return graph;
	}
}
