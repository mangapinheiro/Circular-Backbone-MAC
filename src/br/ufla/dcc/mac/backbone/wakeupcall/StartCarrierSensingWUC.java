package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class StartCarrierSensingWUC extends WakeUpCall {
	public StartCarrierSensingWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public StartCarrierSensingWUC(Address sender) {
		super(sender);
	}
}
