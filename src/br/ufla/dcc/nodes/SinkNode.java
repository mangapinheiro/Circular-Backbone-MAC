package br.ufla.dcc.nodes;

import br.ufla.dcc.grubix.simulator.LayerException;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.TrafficGeneration;
import br.ufla.dcc.grubix.simulator.node.ApplicationLayer;

public class SinkNode extends ApplicationLayer {

	@Override
	public int getPacketTypeCount() {
		return 2;
	}

	@Override
	public void processEvent(TrafficGeneration tg) {

	}

	@Override
	public void lowerSAP(Packet packet) throws LayerException {

	}

}
