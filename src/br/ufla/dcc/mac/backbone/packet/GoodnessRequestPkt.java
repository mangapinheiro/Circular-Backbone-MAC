package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;
import br.ufla.dcc.grubix.simulator.node.Node;

public class GoodnessRequestPkt extends WlanFramePacket {

	private static final int DEFAULT_SIGNAL_STRENGTH = 6;
	private final ElectorAgent _agent;

	public GoodnessRequestPkt(Address sender, NodeId receiver, ElectorAgent agent) {
		this(sender, receiver, agent, PacketType.CONTROL, DEFAULT_SIGNAL_STRENGTH);
	}

	public GoodnessRequestPkt(Address sender, NodeId receiver, ElectorAgent agent, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
		_agent = agent;
	}

	public GoodnessPkt evaluate(Node node) {
		return new GoodnessPkt(node.getLayer(LayerType.MAC).getSender(), getSender().getId(), _agent.evaluate(node), _agent);
	}
}
