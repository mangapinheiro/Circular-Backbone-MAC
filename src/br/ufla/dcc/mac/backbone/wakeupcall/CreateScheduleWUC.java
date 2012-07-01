package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class CreateScheduleWUC extends WakeUpCall {

	public CreateScheduleWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public CreateScheduleWUC(Address sender) {
		super(sender);
	}

}
