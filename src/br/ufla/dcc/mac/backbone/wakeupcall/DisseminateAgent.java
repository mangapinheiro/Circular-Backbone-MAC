package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.backbone.packet.MACAgent;

public class DisseminateAgent extends WakeUpCall {

	private final MACAgent _agent;

	public DisseminateAgent(Address sender, double delay, MACAgent agent) {
		super(sender, delay);
		_agent = agent;
	}

	public DisseminateAgent(Address sender, MACAgent bbBuilderAgent) {
		this(sender, 0, bbBuilderAgent);
	}

	public MACAgent getAgent() {
		return _agent;
	}

}
