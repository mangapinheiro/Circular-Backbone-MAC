package br.ufla.dcc.mac.backbone.state;

import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.mac.backbone.packet.SchedulePacket;

public class NeighborDiscovering extends AbstractNodeState {

	public NeighborDiscovering(NodeState id) {
		super(id);
	}

	@Override
	public boolean acceptsPacket(Packet pkt) {
		return pkt instanceof SchedulePacket;
	}

	@Override
	public void configureNode(Node node) {
		// PhysicalLayer physical = (PhysicalLayer) node.getLayer(LayerType.PHYSICAL);
		// PhysicalLayerState state = (PhysicalLayerState) physical.getState();
		// state.setRadioState(RadioState.LISTENING);
		// physical.setState(state);
	}

}
