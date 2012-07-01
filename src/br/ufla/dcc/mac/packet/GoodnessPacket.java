package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;
import br.ufla.dcc.packet.AgentPacket;

public class GoodnessPacket extends ApplicationPacket {

	private AgentPacket agent;
	private Double nodeGoodness;

	public GoodnessPacket(Address sender, NodeId receiver, AgentPacket agent) {
		super(sender, receiver);
		this.setAgent(agent);
	}

	public GoodnessPacket(Address sender, NodeId receiver, AgentPacket agent, Double goodness) {
		super(sender, receiver);
		this.setAgent(agent);
		this.setNodeGoodness(goodness);
	}

	public void setAgent(AgentPacket agentPacket) {
		this.agent = agentPacket;
	}

	public AgentPacket getAgent() {
		return agent;
	}

	public void setNodeGoodness(Double nodeGoodness) {
		this.nodeGoodness = nodeGoodness;
	}

	public Double getNodeGoodness() {
		return nodeGoodness;
	}

}
