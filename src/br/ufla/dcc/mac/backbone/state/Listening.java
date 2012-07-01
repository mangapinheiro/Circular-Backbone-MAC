package br.ufla.dcc.mac.backbone.state;

import java.util.ArrayList;
import java.util.List;

import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.mac.packet.AckPacket;

public class Listening implements NodeState {

	public static final List<Class<? extends Packet>> acceptedPacketsTypes;

	static {
		acceptedPacketsTypes = new ArrayList<Class<? extends Packet>>();
		acceptedPacketsTypes.add(AckPacket.class);
	}

	@Override
	public boolean acceptsPacket(Packet pkt) {
		return false;
	}
}
