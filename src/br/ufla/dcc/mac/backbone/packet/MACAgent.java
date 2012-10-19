package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.Direction;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;

public class MACAgent extends WlanFramePacket {

	private static int __lastId = 0;
	private final Integer _id;
	private int _hops = 0;
	private final int _hopsEquality;

	public MACAgent(Address sender, NodeId receiver, PacketType type, double signalStrength) {
		this(sender, receiver, type, signalStrength, Integer.MAX_VALUE);
	}

	public MACAgent(Address sender, NodeId receiver, PacketType type, double signalStrength, int hopsEquality) {
		super(sender, receiver, type, signalStrength);
		_id = nextId();
		_hopsEquality = hopsEquality;
	}

	private int nextId() {
		return (__lastId++);
	}

	public int getIdentifier() {
		return _id;
	}

	public void setupToForwardTo(NodeId chosenCandidate) {
		this.setReceiver(chosenCandidate);
		if (this.getDirection().equals(Direction.UPWARDS)) {
			this.flipDirection();
		}
		this.addHop();
	}

	public void addHop() {
		_hops++;
	}

	public int getHops() {
		return _hops;
	}

	public int getHopsEquality() {
		return _hopsEquality;
	}
}
