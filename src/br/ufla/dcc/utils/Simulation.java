package br.ufla.dcc.utils;

import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.Node;

public class Simulation {
	public static class Log {
		public static void state(String name, int value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "int", String.valueOf(value));
		}

		public static void state(String name, float value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "float", String.valueOf(value));
		}

		public static void state(String name, double value, Node forNode) {
			SimulationManager.logNodeState(forNode.getId(), name, "float", String.valueOf(value));
		}
	}

}
