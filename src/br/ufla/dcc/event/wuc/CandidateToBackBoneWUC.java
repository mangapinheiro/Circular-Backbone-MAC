package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;

public class CandidateToBackBoneWUC extends WakeUpCall {

	public CandidateToBackBoneWUC(Address sender, double delay) {
		super(sender, delay);
	}

	public CandidateToBackBoneWUC(Address sender) {
		super(sender);
	}

}
