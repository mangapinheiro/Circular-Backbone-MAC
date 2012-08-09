package br.ufla.dcc.mac.backbone.wakeupcall.debug;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class Ping extends WakeUpCall {

	public Ping(Address sender, double delay) {
		super(sender, delay);
	}

}
