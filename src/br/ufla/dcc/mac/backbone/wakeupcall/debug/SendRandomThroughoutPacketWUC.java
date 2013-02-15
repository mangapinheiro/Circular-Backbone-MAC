package br.ufla.dcc.mac.backbone.wakeupcall.debug;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class SendRandomThroughoutPacketWUC extends WakeUpCall {

	public SendRandomThroughoutPacketWUC(Address sender) {
		super(sender);
	}

}
