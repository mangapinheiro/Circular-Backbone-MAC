package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class RTSFrameStartWUC extends WakeUpCall {

	public RTSFrameStartWUC(Address sender, double delay) {
		super(sender, delay);
	}
}
