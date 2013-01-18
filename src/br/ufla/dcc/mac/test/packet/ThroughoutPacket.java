package br.ufla.dcc.mac.test.packet;

import java.util.Collection;
import java.util.SortedMap;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.Node;

public class ThroughoutPacket extends Packet {

	private Node _destinationNode;
	private final Node _originNode;
	private double _distance;

	public ThroughoutPacket(Address origin) {
		super(origin, (NodeId) null);

		SortedMap<NodeId, Node> allNodes = SimulationManager.getAllNodes();
		_originNode = allNodes.get(origin.getId());

		Collection<Node> values = allNodes.values();

		for (Node node : values) {
			if (_destinationNode == null) {
				_destinationNode = node;
				_distance = _originNode.getPosition().getDistance(_destinationNode.getPosition());
			} else {
				double distanceFromOrigin = _originNode.getPosition().getDistance(node.getPosition());
				if (distanceFromOrigin > _distance) {
					_destinationNode = node;
					_distance = distanceFromOrigin;
				}
			}
		}

		findNextTarget(_originNode);
	}

	public void findNextTarget(Node currentNode) {

		Node nextNode = null;
		double nextDistance = Double.MAX_VALUE;

		for (Node neighbor : currentNode.getNeighbors()) {
			if (nextNode == null) {
				nextNode = neighbor;
				nextDistance = _destinationNode.getPosition().getDistance(neighbor.getPosition());
			} else {
				double distanceFromDestination = _destinationNode.getPosition().getDistance(neighbor.getPosition());

				if (distanceFromDestination < nextDistance) {
					nextNode = neighbor;
					nextDistance = distanceFromDestination;
				}
			}
		}

		reset();
		setReceiver(nextNode.getId());
	}

	public Node getDestinationNode() {
		return _destinationNode;
	}
}
