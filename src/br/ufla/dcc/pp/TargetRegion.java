package br.ufla.dcc.pp;

import br.ufla.dcc.grubix.simulator.Position;

public class TargetRegion {

	public TargetRegion(Position p1, Position p2) {
		startCorner = p1;
		endCorner = p2;
	}

	private Position startCorner;
	private Position endCorner;

	public Position getStartCorner() {
		return startCorner;
	}

	public Position getEndCorner() {
		return endCorner;
	}

}
