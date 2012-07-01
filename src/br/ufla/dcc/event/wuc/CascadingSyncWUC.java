package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class CascadingSyncWUC extends WakeUpCall {

	public CascadingSyncWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public CascadingSyncWUC(Address sender) {
		super(sender);
	}

}
