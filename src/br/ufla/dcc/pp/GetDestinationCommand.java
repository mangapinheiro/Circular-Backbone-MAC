package br.ufla.dcc.pp;

import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.node.user.Command;

public class GetDestinationCommand extends Command {

	private Position destiny;

	private int node;

	public int getNode() {
		return node;
	}

	public void setDestiny(Position destiny) {
		this.destiny = destiny;
	}

	public void setNode(int node) {
		this.node = node;
	}

	public Position getDestiny() {
		return destiny;
	}

}
