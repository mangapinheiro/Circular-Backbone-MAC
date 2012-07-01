package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.packet.BbBuilderAgentPacket;

public class BackBoneAgentsDisseminateWUC extends WakeUpCall {

	private BbBuilderAgentPacket builderAgent = null;

	public BackBoneAgentsDisseminateWUC(Address sender) {
		super(sender);
		// TODO Auto-generated constructor stub
	}

	public BackBoneAgentsDisseminateWUC(Address sender, double delay, BbBuilderAgentPacket builderAgent) {
		super(sender, delay);
		this.setBuilderAgent(builderAgent);
	}

	public void setBuilderAgent(BbBuilderAgentPacket builderAgent) {
		this.builderAgent = builderAgent;
	}

	public BbBuilderAgentPacket getAgent() {
		return builderAgent;
	}

}
