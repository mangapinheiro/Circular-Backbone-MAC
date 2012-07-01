package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.Packet;

public class AckPacket extends Packet {

	public AckPacket(Address sender, NodeId receiver) {
		super(sender, receiver);
	}

}
