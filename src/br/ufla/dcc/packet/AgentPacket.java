package br.ufla.dcc.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.Direction;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.Packet;

public abstract class AgentPacket extends Packet {

	private static Integer lastIdCodeCreated = 0;

	private Integer IDCode = lastIdCodeCreated++;

	private int hops = 0;

	private int maxHops = 15;

	public Integer getIdentity() {
		return IDCode;
	}

	public AgentPacket(Address sender, Packet packet) {
		super(sender, packet);
		// TODO Auto-generated constructor stub
	}

	public AgentPacket(Address sender, NodeId receiver) {
		super(sender, receiver);
		// TODO Auto-generated constructor stub
	}

	public AgentPacket(Address sender, NodeId receiver, Packet packet) {
		super(sender, receiver, packet);
		// TODO Auto-generated constructor stub
	}

	public AgentPacket(Address sender, NodeId receiver, Packet packet, int maxHops) {
		super(sender, receiver, packet);
		this.maxHops = maxHops;
	}

	public void setToForward(NodeId bestBackBoneCandidate) {
		this.setReceiver(bestBackBoneCandidate);
		if (this.getDirection().equals(Direction.UPWARDS)) {
			this.flipDirection();
		}
	}

	public void addHop() {
		this.hops++;
	}

	public void removeHop() {
		this.hops--;
	}

	public boolean canBeDisseminated() {
		return this.hops < maxHops;
	}
}
