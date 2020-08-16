package ca.teamdman.sfm.common.flowdata;

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
		FlowDataHolder dataHolder,
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
		FlowDataHolder holder,
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
			nodeData.getId());
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
}
