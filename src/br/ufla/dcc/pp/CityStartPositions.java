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

package br.ufla.dcc.pp;

import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.kernel.Configuration;
import br.ufla.dcc.grubix.simulator.movement.StartPositionGenerator;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.grubix.simulator.random.InheritRandomGenerator;
import br.ufla.dcc.grubix.simulator.random.RandomGenerator;
import br.ufla.dcc.grubix.xml.ConfigurationException;
import br.ufla.dcc.grubix.xml.ShoXParameter;

/**
 * This class generates random start positions within the dimension of the deployment area, i.e. 0 <= x <= Configuration.xSize and y, accordingly.
 * 
 * @author jlsx
 */
public class CityStartPositions extends StartPositionGenerator {

	/**
	 * xSize (from the Configuration).
	 */
	private double xSize;

	/**
	 * ySize (from the Configuration).
	 */
	private double ySize;

	/**
	 * random generator. By the default the global random generator is used.
	 */
	@ShoXParameter(description = "random generator", defaultClass = InheritRandomGenerator.class)
	private RandomGenerator random;

	/**
	 * {@inheritDoc}
	 */
	public void initConfiguration(Configuration config) throws ConfigurationException {
		super.initConfiguration(config);
		xSize = config.getXSize();
		ySize = config.getYSize();
	}

	/**
	 * @see br.ufla.dcc.grubix.simulator.movement.StartPositionGenerator.
	 * @param node
	 *            The node for which the new position should be generated.
	 * @return A new position for a node
	 */
	@Override
	public final Position newPosition(Node node) {
		double x = random.nextDouble() * xSize;
		x = (double) ((int) (x / 20));
		x *= 20;
		double y = random.nextDouble() * ySize;
		y = (double) ((int) (y / 20));
		y *= 20;
		Position realPos = new Position(x, y);
		return realPos;
	}

	public RandomGenerator getRandom() {
		return random;
	}

	public void setRandom(RandomGenerator random) {
		this.random = random;
	}

	public double getXSize() {
		return xSize;
	}

	public void setXSize(double size) {
		xSize = size;
	}

	public double getYSize() {
		return ySize;
	}

	public void setYSize(double size) {
		ySize = size;
	}
}
