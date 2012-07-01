package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.event.Packet;

/**
 * @author INpHELLer
 * 
 */
public class BbCandidatesGoodnessRequestPacket extends Packet {

	private Position senderPosition;
	private BbBuilderAgentPacket builderAgent = null;
	private int senderOrder = Integer.MAX_VALUE;

	/**
	 * @param sender
	 * @param receiver
	 * @param builderAgent
	 * @param senderPosition
	 */
	public BbCandidatesGoodnessRequestPacket(Address sender, NodeId receiver, BbBuilderAgentPacket builderAgent, Position senderPosition,
			Integer senderOrder) {
		super(sender, receiver);
		this.setBuilderAgent(builderAgent);
		this.setSenderPosition(senderPosition);
		this.setSenderOrder(senderOrder);
	}

	private void setSenderOrder(Integer senderOrder) {
		this.senderOrder = senderOrder;
	}

	public void setBuilderAgent(BbBuilderAgentPacket builderAgent) {
		this.builderAgent = builderAgent;
	}

	public BbBuilderAgentPacket getAgent() {
		return builderAgent;
	}

	public void setSenderPosition(Position senderPosition) {
		this.senderPosition = senderPosition;
	}

	public Position getSenderPosition() {
		return senderPosition;
	}

	public int getSenderOrder() {
		return this.senderOrder;
	}

}
