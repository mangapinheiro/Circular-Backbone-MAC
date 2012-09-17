package br.ufla.dcc.mac.backbone.state;

import java.util.HashMap;
import java.util.Map;

public class StateFactory {

	private static StateFactory INSTANCE;

	private static Map<NodeState, AbstractNodeState> __stateMap;

	private static StateFactory getInstance() {
		if (INSTANCE == null) {
			synchronized (StateFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = new StateFactory();
				}
			}
		}

		return INSTANCE;
	}

	public StateFactory() {
		__stateMap = new HashMap<NodeState, AbstractNodeState>();

		__stateMap.put(NodeState.SLEEPING, new Sleeping(NodeState.SLEEPING));
		__stateMap.put(NodeState.OFF, new Sleeping(NodeState.OFF));
		__stateMap.put(NodeState.LISTENING, new Listening(NodeState.LISTENING));
		__stateMap.put(NodeState.ON, new Listening(NodeState.ON));
		__stateMap.put(NodeState.CARRIERSENSING, new CarrierSensing(NodeState.CARRIERSENSING));
		__stateMap.put(NodeState.DISCOVERY_MODE, new NeighborDiscovering(NodeState.DISCOVERY_MODE));
	}

	public static AbstractNodeState createState(NodeState state) {
		return getInstance().getState(state);
	}

	private AbstractNodeState getState(NodeState state) {
		return __stateMap.get(state);
	}
}
