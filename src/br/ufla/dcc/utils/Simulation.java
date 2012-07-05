package br.ufla.dcc.utils;

import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.Node;

public class Simulation {
	public static final class Log {
		public static void state(String name, int value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "int", String.valueOf(value));
		}

		public static void state(String name, float value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "float", String.valueOf(value));
		}

		public static void state(String name, double value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "float", String.valueOf(value));
		}

		public static void state(String name, long value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "int", String.valueOf(value));
		}
	}

	public static final class Get {
		public static Position nodePosition(NodeId nodeId) {
			return SimulationManager.getAllNodes().get(nodeId).getPosition();
		}
	}

	public static final class Calculate {
		public static double distanceBetweenPositions(Position pos1, Position pos2) {
			double catetoA = pos1.getXCoord() - pos2.getXCoord();
			double catetoB = pos1.getYCoord() - pos2.getYCoord();
			double distanceFromNode = Math.sqrt(catetoA * catetoA + catetoB * catetoB);
			return distanceFromNode;
		}
	}
}
