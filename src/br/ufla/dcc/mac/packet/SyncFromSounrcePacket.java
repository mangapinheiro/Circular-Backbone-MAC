package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;

public class SyncFromSounrcePacket extends ApplicationPacket {

	private NodeId originalSenderId;
	private Integer hopCounter;

	public SyncFromSounrcePacket(Address sender, NodeId receiver, NodeId originalSenderId, Integer hopCounter) {
		super(sender, receiver);
		this.setOriginalSenderId(originalSenderId);
		this.setHopCounter(hopCounter);
	}

	private void setOriginalSenderId(NodeId originalSenderId) {
		this.originalSenderId = originalSenderId;
	}

	public NodeId getOriginalSenderId() {
		return originalSenderId;
	}

	private void setHopCounter(Integer pktCascade) {
		this.hopCounter = pktCascade;
	}

	public Integer getPktCascade() {
		return hopCounter;
	}

}
