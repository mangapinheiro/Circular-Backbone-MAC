package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.packet.AgentPacket;

public class RemoveActiveAgentWUC extends WakeUpCall {
	private AgentPacket agent;

	public RemoveActiveAgentWUC(Address sender, double delay, AgentPacket agent) {
		super(sender, delay);
		this.setAgent(agent);
	}

	public void setAgent(AgentPacket agent) {
		this.agent = agent;
	}

	public AgentPacket getAgent() {
		return agent;
	}

}
