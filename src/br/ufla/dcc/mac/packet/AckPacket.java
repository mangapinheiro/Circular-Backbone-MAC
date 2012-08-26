package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;

public class AckPacket extends WlanFramePacket {

	private static final double DEFAULT_SIGNAL_STRENGTH = 6;

	public AckPacket(Address sender, NodeId receiver) {
		this(sender, receiver, DEFAULT_SIGNAL_STRENGTH);
	}

	public AckPacket(Address sender, NodeId receiver, double signalStrength) {
		super(sender, receiver, PacketType.ACK, signalStrength);
	}
}
