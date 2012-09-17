package br.ufla.dcc.mac.backbone.state;

import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.node.Node;

public abstract class AbstractNodeState {

	private final NodeState _state;

	public AbstractNodeState(NodeState id) {
		_state = id;
	}

	public int getId() {
		return _state.ordinal();
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public boolean isState(NodeState state) {
		return _state == state;
	}

	public NodeState getEnumState() {
		return _state;
	}

	public abstract boolean acceptsPacket(Packet pkt);

	public abstract void configureNode(Node node);

}
