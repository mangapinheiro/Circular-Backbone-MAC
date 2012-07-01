/********************************************************************************
This file is part of ShoX.

ShoX is free software; you can redistribute it and/or modify it under the terms
of the GNU General Public License as published by the Free Software Foundation;
either version 2 of the License, or (at your option) any later version.

ShoX is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with 
ShoX; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
Fifth Floor, Boston, MA 02110-1301, USA

Copyright 2006 The ShoX developers as defined under http://shox.sourceforge.net
 ********************************************************************************/

package br.ufla.dcc.position;

import java.util.Random;

import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.kernel.Configuration;
import br.ufla.dcc.grubix.simulator.movement.StartPositionGenerator;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.grubix.xml.ConfigurationException;

/**
 * This class generates uniformly distributed start positions. The positions are arranged in a grid. It is implicitly assumed that one position is to
 * be generated for each node, i.e. the method newPosition() is invoked Configuration.nodeCount times.
 * 
 * @author jlsx
 */
public class UniformlyRandomPositions extends StartPositionGenerator {

	/** Counts the number of nodes for which positions have already been generated. */
	private int nodeNumber;

	/** The number of rows and columns into which the nodes are arranged. */
	private int colCount = -1;

	/** The x- and y- distances between the positions. */
	private double xDist, yDist;

	/** Constructor of this class. */
	public UniformlyRandomPositions() {
		this.nodeNumber = 0;
	}

	/**
	 * Initializes after configuration is available.
	 * 
	 * @param configuration
	 *            The configuration.
	 * @throws ConfigurationException
	 *             inherited exception.
	 */
	@Override
	public void initConfiguration(Configuration configuration) throws ConfigurationException {
		super.initConfiguration(configuration);
		double nb = Math.sqrt(configuration.getNodeCount() * configuration.getYSize() / configuration.getXSize());
		double columns = Math.floor(nb); // first test for row count
		double rows = Math.ceil(configuration.getNodeCount() / columns);

		this.xDist = configuration.getXSize() / columns;
		this.colCount = (int) columns;
		this.yDist = configuration.getYSize() / rows;
	}

	/**
	 * Generates uniformly distributed start positions.
	 * 
	 * @param node
	 *            The node id for which the new position should be generated.
	 * @return A new position of a node
	 */
	@Override
	public Position newPosition(Node node) {
		int curCol = this.nodeNumber % this.colCount;
		int curRow = this.nodeNumber / this.colCount;
		double x = curCol * this.xDist + this.xDist / 2;
		double y = curRow * this.yDist + this.yDist / 2;

		this.nodeNumber++;
		return new Position(vary(x, this.xDist), vary(y, this.yDist));
	}

	Random variation = new Random();

	private double vary(double number, double range) {
		if (variation.nextDouble() < .5) {
			return number -= variation.nextDouble() * range / 4;
		}

		return number += variation.nextDouble() * range / 4;
	}
}
