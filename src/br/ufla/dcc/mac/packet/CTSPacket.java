package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;

public class CTSPacket extends WlanFramePacket {

	private static final double DEFAULT_SIGNAL_STRENGTH = 6;

	public CTSPacket(Address sender, NodeId receiver) {
		this(sender, receiver, PacketType.CONTROL, DEFAULT_SIGNAL_STRENGTH);
	}

	public CTSPacket(Address sender, NodeId receiver, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
	}
}
