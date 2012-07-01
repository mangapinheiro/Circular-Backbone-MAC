package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;

public class CascadePacket extends ApplicationPacket {

	private int distanceInHops = 0;

	public CascadePacket(Address sender, NodeId receiver, Integer distanceInHops) {
		super(sender, receiver);
		this.setHopCounter(distanceInHops);
	}

	public int getHopCounter() {
		return distanceInHops;
	}

	public void setHopCounter(int hopCounter) {
		this.distanceInHops = hopCounter;
	}

}
