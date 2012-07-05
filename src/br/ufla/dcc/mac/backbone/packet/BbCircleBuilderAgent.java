package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.mac.backbone.CircularBackbone_MAC;

public class BbCircleBuilderAgent extends ElectorAgent {

	private final int _radius;

	public BbCircleBuilderAgent(Address sender, NodeId receiver, int radius) {
		this(sender, receiver, PacketType.CONTROL, 6, radius);
	}

	public BbCircleBuilderAgent(Address sender, NodeId receiver, PacketType type, double signalStrength, int radius) {
		super(sender, receiver, type, signalStrength);
		_radius = radius;
	}

	@Override
	public double evaluate(Node node) {
		// TODO - IMPLEMENT THIS CALCULATION (currently it's returning a random number)
		double distanceFromRadius = Math.abs(((CircularBackbone_MAC) node.getLayer(LayerType.MAC)).getDistanceFromCenter() - _radius);

		return distanceFromRadius == 0 ? 1 : 1 / distanceFromRadius;
	}
}
