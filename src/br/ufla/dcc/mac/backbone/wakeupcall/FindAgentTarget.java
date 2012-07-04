package br.ufla.dcc.mac.backbone.wakeupcall;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.backbone.packet.ElectorAgent;

public class FindAgentTarget extends WakeUpCall {

	private final ElectorAgent _bbBuilderAgent;

	public FindAgentTarget(Address sender, double delay, ElectorAgent bbBuilderAgent) {
		super(sender, delay);
		_bbBuilderAgent = bbBuilderAgent;
	}

	public FindAgentTarget(Address sender, ElectorAgent bbBuilderAgent) {
		this(sender, 0, bbBuilderAgent);
	}

	public ElectorAgent getAgent() {
		return _bbBuilderAgent;
	}

}
