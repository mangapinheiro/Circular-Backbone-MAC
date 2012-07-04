package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class BroadcastDistanceFromCenter extends WakeUpCall {

	public BroadcastDistanceFromCenter(Address sender, double delay) {
		super(sender, delay);
		// TODO Auto-generated constructor stub
	}

	public BroadcastDistanceFromCenter(Address sender) {
		super(sender);
		// TODO Auto-generated constructor stub
	}

}
