/********************************************************************************
This file is part of ShoX.

ShoX is free software; you can redistribute it and/or modify it under the terms
of the GNU General Public License as published by the Free Software Foundation;
either version 2 of the License, or (at your option) any later version.

ShoX is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
ShoX; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
Fifth Floor, Boston, MA 02110-1301, USA

Copyright 2006 The ShoX developers as defined under http://shox.sourceforge.net
 ********************************************************************************/

package br.ufla.dcc.pp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.event.Movement;
import br.ufla.dcc.grubix.simulator.kernel.Configuration;
import br.ufla.dcc.grubix.simulator.movement.MovementManager;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.grubix.simulator.node.user.Command;
import br.ufla.dcc.grubix.simulator.random.InheritRandomGenerator;
import br.ufla.dcc.grubix.simulator.random.RandomGenerator;
import br.ufla.dcc.grubix.xml.ConfigurationException;
import br.ufla.dcc.grubix.xml.ShoXParameter;

/**

 */
public class CityMovement extends MovementManager {
	/**
	 * Logger for the class SimulationManager..
	 */
	private static final Logger LOGGER = Logger.getLogger(CityMovement.class.getName());

	/**
	 * Vector containing a list of moves for every node.
	 */
	private ArrayList<List<Movement>> moveLists;

	private int Directions[] = null;
	private Double Speeds[] = null;
	private int MissingSteps[] = null;
	private int Steps[] = null;
	private Position destiny[] = null;
	@ShoXParameter(description = "random generator", defaultClass = InheritRandomGenerator.class)
	private RandomGenerator random;
	double xsize;
	double ysize;

	/** Constructor of the class CityMovement. */
	public CityMovement() {
	}

	private double squareSize;

	/**
	 * {@inheritDoc}
	 */
	public void initConfiguration(Configuration config) throws ConfigurationException {
		super.initConfiguration(config);
		squareSize = 20.0;
	}

	private Position newDestiny() {
		double x = 0;
		x = random.nextDouble() * xsize;
		x = (double) ((int) (x / 20));
		if (x == 0) {
			x = 1;
		}
		x *= 20;
		double y = 0;
		y = random.nextDouble() * ysize;
		y = (double) ((int) (y / 20));
		if (y == 0) {
			y = 1;
		}
		y *= 20;
		return new Position(x, y);
	}

	private void newDirection(Node node) {
		int node_id = node.getId().asInt();

		double distX = Math.abs(node.getPosition().getXCoord() - destiny[node_id].getXCoord());
		double distY = Math.abs(node.getPosition().getYCoord() - destiny[node_id].getYCoord());

		int Dx = (int) ((destiny[node_id].getXCoord() - node.getPosition().getXCoord()) / Math.abs(destiny[node_id].getXCoord()
				- node.getPosition().getXCoord()))
				* -1 + 1;
		int Dy = (int) ((destiny[node_id].getYCoord() - node.getPosition().getYCoord()) / Math.abs(destiny[node_id].getYCoord()
				- node.getPosition().getYCoord()))
				* -1 + 2;

		if (distY < 1 && distX < 1) {
			Directions[node_id] = 10;
		} else {
			if (distX < 1) {
				Directions[node_id] = Dy;
			} else {
				if (distY < 1) {
					Directions[node_id] = Dx;
				} else {
					if (Math.random() > 0.5)
						Directions[node_id] = Dx;
					else
						Directions[node_id] = Dy;
				}
			}

		}
	}

	private void start(Collection<Node> allNodes) {
		if (Directions == null) {
			Directions = new int[allNodes.size() + 10];
			Speeds = new Double[allNodes.size() + 10];
			MissingSteps = new int[allNodes.size() + 10];
			Steps = new int[allNodes.size() + 10];
			destiny = new Position[allNodes.size() + 10];
			for (int i = 0; i < allNodes.size() + 10; i++) {
				int dir = (int) (Math.random() * 3.0 + 0.5);
				Directions[i] = dir;
				int steps = ((int) (Math.random() * 10.0 + 0.5)) + 5;
				double sp = squareSize / (double) steps;
				Speeds[i] = sp;
				MissingSteps[i] = steps;
				Steps[i] = steps;
				destiny[i] = newDestiny();
			}
		}
	}

	/**
	 * @see br.ufla.dcc.grubix.simulator.movement.MovementManager#createMoves(java.util.Collection) Creates random moves for each node in the
	 *      SIMULATION. Determines a distance and direction randomly. Afterwards calculates the new coordinates.
	 * 
	 * @param allNodes
	 *            allNodes Collection containing all nodes to create moves for.
	 * @return Collection containing new moves.
	 */
	public final Collection<Movement> createMoves(Collection<Node> allNodes) {
		final Configuration configuration = Configuration.getInstance();
		xsize = configuration.getXSize();
		ysize = configuration.getYSize();

		Iterator<Node> iter = null;
		iter = allNodes.iterator();
		List<Movement> nextMoves = new LinkedList<Movement>();
		this.start(allNodes);

		while (iter.hasNext()) {
			Node node = iter.next();
			double newx, newy;
			Position currentPos = node.getPosition();
			newy = currentPos.getYCoord();
			newx = currentPos.getXCoord();
			int node_id = node.getId().asInt();

			if (MissingSteps[node_id] == 0) {

				if (Position.getDistance(destiny[node_id], node.getPosition()) < 0.5)
					destiny[node_id] = newDestiny();

				this.newDirection(node);
				MissingSteps[node_id] = Steps[node_id];
			}

			switch (Directions[node_id]) {
				case 0:
					newx += Speeds[node_id]; // (configuration.getMovementTimeInterval()/100.0);
					break;
				case 1:
					newy += Speeds[node_id];
					break;
				case 2:
					newx -= Speeds[node_id];
					break;
				case 3:
					newy -= Speeds[node_id];
					break;
				default:
					break;
			}
			MissingSteps[node_id]--;

			Position nextPos = new Position(newx, newy);
			Movement nextMove = new Movement(node, nextPos, 0);
			nextMoves.add(nextMove);
		}
		return nextMoves;
	}

	public void sendCommand(Command c) {
		if (c instanceof GetDestinationCommand) {
			GetDestinationCommand command = (GetDestinationCommand) c;
			command.setDestiny(destiny[command.getNode()]);
		}
	}
}
