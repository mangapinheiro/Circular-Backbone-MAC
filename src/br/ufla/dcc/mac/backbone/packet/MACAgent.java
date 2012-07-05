package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.Direction;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;

public class MACAgent extends WlanFramePacket {

	private static int __lastId = 0;
	private final Integer _id;

	public MACAgent(Address sender, NodeId receiver, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
		_id = nextId();
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
	}
}
