package br.ufla.dcc.mac.backbone.state;

import java.util.ArrayList;
import java.util.List;

import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.PhysicalLayerState;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.grubix.simulator.node.PhysicalLayer;
import br.ufla.dcc.grubix.simulator.node.RadioState;
import br.ufla.dcc.mac.packet.AckPacket;

public class Listening extends AbstractNodeState {

	public static final List<Class<? extends Packet>> acceptedPacketsTypes;

	static {
		acceptedPacketsTypes = new ArrayList<Class<? extends Packet>>();
		acceptedPacketsTypes.add(AckPacket.class);
	}

	public Listening(NodeState id) {
		super(id);
	}

	@Override
	public boolean acceptsPacket(Packet pkt) {
		return true;
	}

	@Override
	public void configureNode(Node node) {
		PhysicalLayer physical = (PhysicalLayer) node.getLayer(LayerType.PHYSICAL);
		PhysicalLayerState state = (PhysicalLayerState) physical.getState();
		state.setRadioState(RadioState.LISTENING);
		physical.setState(state);
	}
}
