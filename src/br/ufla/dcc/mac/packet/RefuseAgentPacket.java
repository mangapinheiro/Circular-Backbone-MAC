package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;
import br.ufla.dcc.packet.AgentPacket;

public class RefuseAgentPacket extends ApplicationPacket {

	private AgentPacket builderAgent;

	public RefuseAgentPacket(Address sender, NodeId receiver, AgentPacket builderAgent) {
		super(sender, receiver);
		this.setBuilderAgent(builderAgent);
	}

	private void setBuilderAgent(AgentPacket builderAgent) {
		this.builderAgent = builderAgent;
	}

	public AgentPacket getAgent() {
		return builderAgent;
	}

}
