package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.packet.AgentPacket;

public class RemoveGoodnessListWUC extends WakeUpCall {

	private AgentPacket agent = null;

	public RemoveGoodnessListWUC(Address sender, AgentPacket agent) {
		super(sender);
		this.setBuilderAgent(agent);
	}

	public RemoveGoodnessListWUC(Address sender, int delay, AgentPacket agent) {
		super(sender, delay);
		this.setBuilderAgent(agent);
	}

	public void setBuilderAgent(AgentPacket agent) {
		this.agent = agent;
	}

	public AgentPacket getAgent() {
		return agent;
	}

}
