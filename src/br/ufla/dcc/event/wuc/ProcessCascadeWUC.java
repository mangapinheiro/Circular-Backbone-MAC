/**
 * 
 */
package br.ufla.dcc.event.wuc;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.mac.packet.CascadePacket;

/**
 * @author inpheller
 * 
 */
public class ProcessCascadeWUC extends WakeUpCall {

	private CascadePacket pkt;

	public ProcessCascadeWUC(Address sender, double delay) {
		super(sender, delay);
		// TODO Auto-generated constructor stub
	}

	public ProcessCascadeWUC(Address sender) {
		super(sender);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sender
	 */
	public ProcessCascadeWUC(Address sender, CascadePacket pkt) {
		super(sender);
		this.setPkt(pkt);
	}

	public void setPkt(CascadePacket pkt) {
		this.pkt = pkt;
	}

	public CascadePacket getPkt() {
		return pkt;
	}

}
