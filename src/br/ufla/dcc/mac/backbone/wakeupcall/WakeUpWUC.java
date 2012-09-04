package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.backbone.Schedule;

public class WakeUpWUC extends WakeUpCall {

	private final Schedule _schedule;

	public WakeUpWUC(Address sender, double delay, Schedule schedule) {
		super(sender, delay);
		_schedule = schedule;
	}

	public Schedule getSchedule() {
		return _schedule;
	}
}
