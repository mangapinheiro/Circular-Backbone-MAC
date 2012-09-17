package br.ufla.dcc.mac.backbone.state;

import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.PhysicalLayerState;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.grubix.simulator.node.PhysicalLayer;
import br.ufla.dcc.grubix.simulator.node.RadioState;

public class Sleeping extends AbstractNodeState {

	public Sleeping(NodeState id) {
		super(id);
	}

	@Override
	public boolean acceptsPacket(Packet pkt) {
		return false;
	}

	@Override
	public void configureNode(Node node) {
		PhysicalLayer physical = (PhysicalLayer) node.getLayer(LayerType.PHYSICAL);
		PhysicalLayerState state = (PhysicalLayerState) physical.getState();
		state.setRadioState(RadioState.OFF);
		physical.setState(state);
	}
}
