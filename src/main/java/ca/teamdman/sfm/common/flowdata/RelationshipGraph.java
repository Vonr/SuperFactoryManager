package ca.teamdman.sfm.common.flowdata;

import ca.teamdman.sfm.SFMUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class RelationshipGraph implements FlowDataHolder {

	private final HashMap<UUID, Node> nodes = new HashMap<>();

	public void addNode(FlowData data) {
		nodes.computeIfAbsent(data.getId(), __ -> new Node(data));
	}

	public int getNodeCount() {
		return nodes.size();
	}

	public int getEdgeCount() {
		return (int) getEdges().count();
	}

	public void clear() {
		nodes.clear();
	}

	public void removeNode(UUID id) {
		HashSet<Edge> toRemove = new HashSet<>();
		getNode(id).ifPresent(node -> {
			toRemove.addAll(node.incoming);
			toRemove.addAll(node.outgoing);
		});
		toRemove.forEach(edge -> removeEdge(edge.FROM.getId(), edge.TO.getId()));
		nodes.remove(id);
	}

	public void removeEdge(UUID from, UUID to) {
		getNode(from)
			.ifPresent(node -> node.outgoing
				.removeIf(rel -> rel.FROM.getId().equals(from)));
		getNode(to)
			.ifPresent(node -> node.incoming
				.removeIf(rel -> rel.TO.getId().equals(to)));
	}

	public void putEdge(UUID edgeId, UUID fromId, UUID toId) {
		if (fromId.equals(toId)) {
			return;
		}
		Node from = nodes.get(fromId);
		Node to = nodes.get(toId);
		Edge edge = new Edge(edgeId, from, to);
		from.outgoing.add(edge);
		to.incoming.add(edge);
	}

	public Optional<Edge> getEdge(UUID id) {
		return getEdges().filter(e -> e.EDGE_ID.equals(id)).findFirst();
	}

	public Optional<Edge> getEdge(UUID from, UUID to) {
		return getNode(from)
			.flatMap(node ->
				node.outgoing.stream()
					.filter(edge -> edge.TO.getId().equals(to))
					.findFirst()
			);
	}

	public Stream<Node> getAncestors(Node node) {
		return SFMUtil.getRecursiveStream(
			(current, enqueue) -> current.incoming.stream().map(edge -> edge.FROM).forEach(enqueue),
			__ -> true,
			node
		);
	}

	public Stream<Node> getAncestors(UUID id) {
		return getNode(id).map(this::getAncestors).orElse(Stream.empty());
	}

	public Stream<Node> getDescendants(UUID id) {
		return getNode(id).map(this::getDescendants).orElse(Stream.empty());
	}

	public Stream<Node> getDescendants(Node node) {
		return SFMUtil.getRecursiveStream(
			(current, enqueue) -> current.outgoing.stream().map(edge -> edge.TO).forEach(enqueue),
			__ -> true,
			node
		);
	}

	public Optional<Node> getNode(UUID id) {
		return Optional.ofNullable(nodes.get(id));
	}

	public Stream<Node> getNodes() {
		return nodes.values().stream();
	}

	public Stream<Edge> getEdges() {
		return nodes.values().stream().flatMap(node ->
			Stream.concat(node.incoming.stream(), node.outgoing.stream())
		);
	}

	@Override
	public Stream<FlowData> getData() {
		return getNodes().map(node -> node.NODE_DATA);
	}

	@Override
	public Optional<FlowData> getData(UUID id) {
		return getNode(id).map(node -> node.NODE_DATA);
	}

	@Override
	public void removeData(UUID id) {
		removeNode(id);
	}

	@Override
	public void addData(FlowData data) {
		addNode(data);
	}

	@Override
	public void clearData() {
		clear();
	}

	@Override
	public String toString() {
		return "RelationshipGraph [" + getNodeCount() + " nodes, " + getEdgeCount() + " edges]";
	}

	public static class Node {

		public final FlowData NODE_DATA;
		public final HashSet<Edge> incoming = new HashSet<>();
		public final HashSet<Edge> outgoing = new HashSet<>();

		public Node(FlowData data) {
			this.NODE_DATA = data;
		}

		public UUID getId() {
			return NODE_DATA.getId();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Node && ((Node) obj).getId().equals(getId());
		}

		@Override
		public int hashCode() {
			return getId().hashCode();
		}

		@Override
		public String toString() {
			return NODE_DATA.toString();
		}
	}

	public static class Edge {

		public final UUID EDGE_ID;
		public final Node FROM, TO;

		public Edge(UUID EDGE_ID, Node from, Node to) {
			this.EDGE_ID = EDGE_ID;
			this.FROM = from;
			this.TO = to;
		}

		@Override
		public int hashCode() {
			return Objects.hash(FROM.getId(), TO.getId());
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Edge
				&& ((Edge) obj).FROM.getId().equals(FROM.getId())
				&& ((Edge) obj).TO.getId().equals(TO.getId());
		}

		@Override
		public String toString() {
			return FROM.getId() + " to " + TO.getId();
		}
	}
}
