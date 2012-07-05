package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;

public class GoodnessPkt extends MACAgent {

	private static final int DEFAULT_SIGNAL_STRENGTH = 6;
	private final ElectorAgent _agent;
	private final double _goodness;

	public GoodnessPkt(Address sender, NodeId receiver, double goodness, ElectorAgent agent) {
		this(sender, receiver, agent, goodness, PacketType.CONTROL, DEFAULT_SIGNAL_STRENGTH);
	}

	public GoodnessPkt(Address sender, NodeId receiver, ElectorAgent agent, double goodness, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
		_agent = agent;
		_goodness = goodness;
	}

	public double getSenderGoodness() {
		return _goodness;
	}

	public MACAgent getAgent() {
		return _agent;
	}
}
