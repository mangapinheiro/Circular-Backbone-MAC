package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class NeighborDiscoveryWUC extends WakeUpCall {
	public NeighborDiscoveryWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public NeighborDiscoveryWUC(Address sender) {
		super(sender);
	}
}
