package br.ufla.dcc.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.Packet;

public class AgentContainerPacket extends Packet {

	public AgentContainerPacket(Address sender, NodeId receiver) {
		super(sender, receiver);
		// TODO Auto-generated constructor stub
	}

	public AgentContainerPacket(Address sender, Packet packet) {
		super(sender, packet);
		// TODO Auto-generated constructor stub
	}

	public AgentContainerPacket(Address sender, NodeId receiver, Packet packet) {
		super(sender, receiver, packet);
		// TODO Auto-generated constructor stub
	}

}
