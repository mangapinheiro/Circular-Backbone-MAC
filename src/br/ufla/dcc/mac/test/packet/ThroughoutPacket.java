package br.ufla.dcc.mac.test.packet;

import java.util.Collection;
import java.util.SortedMap;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.mac.backbone.CircularBackbone_MAC;

public class ThroughoutPacket extends Packet {

	private NodeId _destinationNode;

	public ThroughoutPacket(Address origin) {
		super(origin, (NodeId) null);

		SortedMap<NodeId, Node> allNodes = SimulationManager.getAllNodes();
		Node originNode = allNodes.get(origin.getId());

		Collection<Node> values = allNodes.values();

		double distance = 0;

		for (Node node : values) {
			if (_destinationNode == null) {
				_destinationNode = node.getId();
				distance = originNode.getPosition().getDistance(node.getPosition());
			} else {
				double distanceFromOrigin = originNode.getPosition().getDistance(node.getPosition());
				if (distanceFromOrigin > distance) {
					_destinationNode = node.getId();
					distance = distanceFromOrigin;
				}
			}
		}

		findNextTarget(originNode);
	}

	private ThroughoutPacket(Address origin, NodeId nextNode) {
		super(origin, nextNode);
	}

	private void findNextTarget(Node currentNode) {

		Node nextNode = null;
		double nextDistance = Double.MAX_VALUE;

		Node destinationNode = getDestinationNode();

		for (Node neighbor : currentNode.getNeighbors()) {

			CircularBackbone_MAC circularMacLayer = (CircularBackbone_MAC) neighbor.getLayer(LayerType.MAC);

			if (circularMacLayer.getBackboneParent() == neighbor.getId() || circularMacLayer.getBackboneChild() == neighbor.getId()) {
				double neighborDistanceFromDestination = destinationNode.getPosition().getDistance(neighbor.getPosition());
				double myDistanceFromDestination = destinationNode.getPosition().getDistance(currentNode.getPosition());

				if (neighborDistanceFromDestination < myDistanceFromDestination) {
					nextNode = neighbor;
					nextDistance = neighborDistanceFromDestination;
					break;
				}
			}

			if (nextNode == null) {
				nextNode = neighbor;
				nextDistance = destinationNode.getPosition().getDistance(neighbor.getPosition());
			} else {
				double distanceFromDestination = destinationNode.getPosition().getDistance(neighbor.getPosition());

				if (distanceFromDestination < nextDistance) {
					nextNode = neighbor;
					nextDistance = distanceFromDestination;
				}
			}
		}

		setReceiver(nextNode.getId());
	}

	public Node getDestinationNode() {
		SortedMap<NodeId, Node> allNodes = SimulationManager.getAllNodes();
		return allNodes.get(_destinationNode);
	}

	public ThroughoutPacket createForwardPacket(Node currentNode) {
		ThroughoutPacket throughoutPacket = new ThroughoutPacket(getSender(), null);

		throughoutPacket._destinationNode = _destinationNode;

		throughoutPacket.findNextTarget(currentNode);

		return throughoutPacket;
	}
}
