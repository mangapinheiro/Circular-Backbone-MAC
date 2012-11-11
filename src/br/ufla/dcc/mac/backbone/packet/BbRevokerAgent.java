package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;

public class BbRevokerAgent extends MACAgent {

	public BbRevokerAgent(Address sender, NodeId receiver) {
		this(sender, receiver, PacketType.DATA, 6);
	}

	public BbRevokerAgent(Address sender, NodeId receiver, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength, 5);
	}
}
