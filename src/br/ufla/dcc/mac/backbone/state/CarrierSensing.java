package br.ufla.dcc.mac.backbone.state;

import br.ufla.dcc.grubix.simulator.event.Packet;

public class CarrierSensing implements NodeState {

	@Override
	public boolean acceptsPacket(Packet pkt) {
		return false;
	}

}
