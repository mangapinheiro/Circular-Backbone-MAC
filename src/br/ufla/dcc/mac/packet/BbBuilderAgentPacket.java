package br.ufla.dcc.mac.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.Direction;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.packet.AgentPacket;
import br.ufla.dcc.utils.BbSyncDirection;

/**
 * @author INpHELLer
 * 
 *         Select the nodes witch will compose the backbone and gives the direction of the synchronization (default direction is TO_SOURCE)
 */
public class BbBuilderAgentPacket extends AgentPacket {

	private BbSyncDirection synchDirection = BbSyncDirection.TO_SOURCE;

	public BbBuilderAgentPacket(Address sender, Packet packet) {
		super(sender, packet);
		// TODO Auto-generated constructor stub
	}

	public BbBuilderAgentPacket(Address sender, NodeId receiver) {
		super(sender, receiver);
		// TODO Auto-generated constructor stub
	}

	public BbBuilderAgentPacket(Address sender, NodeId receiver, Packet packet) {
		super(sender, receiver, packet);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param synchDirection
	 *            - default is TO_SOURCE
	 */
	public BbBuilderAgentPacket(Address sender, NodeId receiver, BbSyncDirection synchDirection) {
		super(sender, receiver);
		this.setSynchDirection(synchDirection);
	}

	public void setSynchDirection(BbSyncDirection synchDirection) {
		this.synchDirection = synchDirection;
	}

	public BbSyncDirection getSynchDirection() {
		return synchDirection;
	}

}
