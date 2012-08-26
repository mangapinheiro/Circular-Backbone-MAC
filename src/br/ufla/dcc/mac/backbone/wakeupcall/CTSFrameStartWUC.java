package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class CTSFrameStartWUC extends WakeUpCall {

	public CTSFrameStartWUC(Address sender, double delay) {
		super(sender, delay);
	}
}
