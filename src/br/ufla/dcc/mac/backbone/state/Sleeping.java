package br.ufla.dcc.mac.backbone.state;

import br.ufla.dcc.grubix.simulator.event.Packet;

public class Sleeping implements NodeState {

	@Override
	public boolean acceptsPacket(Packet pkt) {
		return false;
	}

}
