/**
 * 
 */
package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.packet.BbBuilderAgentPacket;

/**
 * @author -Manga.-
 * 
 */
public class BackBoneCreateWUC extends WakeUpCall {

	private BbBuilderAgentPacket agent = null;

	public BackBoneCreateWUC(Address sender, double delay, BbBuilderAgentPacket agent) {
		super(sender, delay);
		this.agent = agent;
	}

	public BackBoneCreateWUC(Address sender, BbBuilderAgentPacket agent) {
		super(sender);
		this.agent = agent;
	}

	public BbBuilderAgentPacket getAgent() {

		return this.agent;
	}

}
