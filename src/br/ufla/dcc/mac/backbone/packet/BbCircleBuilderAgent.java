package br.ufla.dcc.mac.backbone.packet;

import java.util.Random;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.node.Node;

public class BbCircleBuilderAgent extends ElectorAgent {

	public BbCircleBuilderAgent(Address sender, NodeId receiver) {
		this(sender, receiver, PacketType.CONTROL, 6);
	}

	public BbCircleBuilderAgent(Address sender, NodeId receiver, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
	}

	@Override
	public double evaluate(Node node) {
		// TODO - IMPLEMENT THIS CALCULATION (currently it's returning a random number)
		return new Random().nextDouble();
	}
}
