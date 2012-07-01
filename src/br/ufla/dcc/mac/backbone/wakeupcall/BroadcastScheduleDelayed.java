package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.user.SendDelayedWakeUp;

public class BroadcastScheduleDelayed extends SendDelayedWakeUp {

	public BroadcastScheduleDelayed(Address sender, double delay, Packet lpkt) {
		super(sender, delay, lpkt);
		// TODO Auto-generated constructor stub
	}

	public BroadcastScheduleDelayed(Address sender) {
		super(sender);
		// TODO Auto-generated constructor stub
	}

}
