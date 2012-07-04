package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.LogLinkPacket;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;
import br.ufla.dcc.grubix.simulator.node.Node;

public class ElectorAgent extends WlanFramePacket {

	public ElectorAgent(Address sender, NodeId receiver, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
	}

	public ElectorAgent(Address sender, LogLinkPacket packet) {
		super(sender, packet);
	}

	public double evaluate(Node node) {
		return -1;
	}

}