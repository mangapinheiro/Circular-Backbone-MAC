package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class GoSleepWUC extends WakeUpCall {

	public GoSleepWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public GoSleepWUC(Address sender) {
		super(sender);
	}
}
