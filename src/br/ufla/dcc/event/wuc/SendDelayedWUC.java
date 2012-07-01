package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class SendDelayedWUC extends WakeUpCall {

	public ApplicationPacket pak;

	public SendDelayedWUC(Address sender, double delay, ApplicationPacket pak) {
		super(sender, delay);
		this.pak = pak;
	}

	public SendDelayedWUC(Address sender) {
		super(sender);
	}

}
