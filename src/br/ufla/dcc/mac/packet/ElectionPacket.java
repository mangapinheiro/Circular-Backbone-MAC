package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;

public class ElectionPacket extends ApplicationPacket {

	private Integer distanceInHops = 0;

	public ElectionPacket(Address sender, NodeId receiver, int distanceInHops) {
		super(sender, receiver);
		this.setDistanceInHops(distanceInHops);
	}

	private void setDistanceInHops(Integer distanceInHops) {
		this.distanceInHops = distanceInHops;
	}

	public Integer getCandidateOrder() {
		return distanceInHops;
	}

	public NodeId getCandidateId() {
		return getSender().getId();
	}

}
