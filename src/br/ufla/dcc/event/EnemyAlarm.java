package br.ufla.dcc.event;

import br.ufla.dcc.grubix.simulator.Position;

public class EnemyAlarm extends Alarm {

	private double pherAmmount;
	private int pherId;
	public int step = 0;

	public EnemyAlarm(Position position, double timeStamp, int type, double pherAmmount, int pherId) {
		super(position, timeStamp, type);
		this.setPherAmmount(pherAmmount);
		this.setPherId(pherId);
	}

	public double getPherAmmount() {
		return this.pherAmmount;
	}

	public void setPherAmmount(double pherAmmount) {
		this.pherAmmount = pherAmmount;
	}

	public int getPherId() {
		return this.pherId;
	}

	public void setPherId(int pherId) {
		this.pherId = pherId;
	}

}
