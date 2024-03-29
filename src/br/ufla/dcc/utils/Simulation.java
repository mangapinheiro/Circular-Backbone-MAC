package br.ufla.dcc.utils;

import java.util.ArrayList;

import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.Node;

public class Simulation {
	private static String[] ignoredLogs = new String[] { "PKT TYPE", "Schedule", "NumberOfSchedules", "DistanceFromCenter", "CenterNode", "Goodness",
			"BackBone", "Paquet sent", "Discovering", "Margin node", "NumberOfKnownNeighbors", "Margin with schedule" };
	private static ArrayList<String> ignoredLogsList = new ArrayList<String>();

	static {
		for (String ignored : ignoredLogs) {
			ignoredLogsList.add(ignored);
		}
	}

	public static final class Log {
		public static void NumberOfKnownNeighbors(int value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), "NumberOfKnownNeighbors", "int", String.valueOf(value));
		}

		public static void state(String name, int value, Node forNode) {
			if (ignoredLogsList.contains(name)) {
				return;
			}
			SimulationManager.logNodeState(forNode.getId(), name, "int", String.valueOf(value));
		}

		public static void state(String name, float value, Node forNode) {
			if (ignoredLogsList.contains(name)) {
				return;
			}
			SimulationManager.logNodeState(forNode.getId(), name, "float", String.valueOf(value));
		}

		public static void state(String name, double value, Node forNode) {
			if (ignoredLogsList.contains(name)) {
				return;
			}
			SimulationManager.logNodeState(forNode.getId(), name, "float", String.valueOf(value));
		}

		public static void state(String name, long value, Node forNode) {
			if (ignoredLogsList.contains(name)) {
				return;
			}
			SimulationManager.logNodeState(forNode.getId(), name, "int", String.valueOf(value));
		}

		public static void VisitedByAgent(int identifier, Node node) {
			Simulation.Log.state("Visited by agent", identifier, node);
		}

		public static void MacCircleNode(int identifier, Node node) {
			Simulation.Log.state("MAC_Circle Node", identifier, node);
		}

		public static void CircleClosedInNode(Node node) {
			Simulation.Log.state("Circle Closed in Node", 8, node);
		}

		public static void RemovedFromBackbone(Node node) {
			Simulation.Log.state("Removed From Backbone", 9, node);
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
