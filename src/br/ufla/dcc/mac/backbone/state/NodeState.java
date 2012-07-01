package br.ufla.dcc.mac.backbone.state;

import br.ufla.dcc.grubix.simulator.event.Packet;

public interface NodeState {
	int SLEEPING = 0;
	int AWAKEN = 1;

	boolean acceptsPacket(Packet pkt);
}
