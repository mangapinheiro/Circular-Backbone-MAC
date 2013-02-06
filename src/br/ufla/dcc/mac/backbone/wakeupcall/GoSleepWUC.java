package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.backbone.Schedule;

public class GoSleepWUC extends WakeUpCall {

	private Schedule _sleepForSchedule;

	public GoSleepWUC(Address sender, double delay, Schedule forSchedule) {
		super(sender, delay);
	}

	public GoSleepWUC(Address sender) {
		super(sender);
	}

	public Schedule getSchedule() {
		return _sleepForSchedule;
	}
}
