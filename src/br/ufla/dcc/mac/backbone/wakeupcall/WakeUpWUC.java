package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class WakeUpWUC extends WakeUpCall {

	public WakeUpWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public WakeUpWUC(Address sender) {
		super(sender);
	}

}
