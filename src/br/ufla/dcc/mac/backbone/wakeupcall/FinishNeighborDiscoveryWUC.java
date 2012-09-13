package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class FinishNeighborDiscoveryWUC extends WakeUpCall {

	public FinishNeighborDiscoveryWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public FinishNeighborDiscoveryWUC(Address sender) {
		super(sender);
	}

}
