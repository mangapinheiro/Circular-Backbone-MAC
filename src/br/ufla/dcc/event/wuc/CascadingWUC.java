package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class CascadingWUC extends WakeUpCall {

	public CascadingWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public CascadingWUC(Address sender) {
		super(sender);
	}

}
