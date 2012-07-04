package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;

public class DistanceFromCenterPacket extends WlanFramePacket {

	private static final int DEFAULT_SIGNAL_STRENGTH = 6;
	private final double _distanceFromCenter;

	public DistanceFromCenterPacket(Address sender, NodeId receiver, double distance) {
		this(sender, receiver, distance, PacketType.CONTROL, DEFAULT_SIGNAL_STRENGTH);
	}

	public DistanceFromCenterPacket(Address sender, NodeId receiver, double distance, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
		_distanceFromCenter = distance;
	}

	public double getDistanceFromCenter() {
		return _distanceFromCenter;
	}
}
