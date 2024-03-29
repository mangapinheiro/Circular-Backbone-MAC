package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.mac.backbone.CircularBackbone_MAC;
import br.ufla.dcc.mac.backbone.Schedule;

public class BbCircleRootFinderAgent extends ElectorAgent {

	private final int _radius;
	private static final int DEFAULT_TOLERANCE = 10;

	// TODO - Make this configurable
	private final double _tolerance = DEFAULT_TOLERANCE;

	public BbCircleRootFinderAgent(Address sender, NodeId receiver, int radius) {
		this(sender, receiver, 6, radius);
	}

	public BbCircleRootFinderAgent(Address sender, NodeId receiver, double signalStrength, int radius) {
		super(sender, receiver, signalStrength, 5);
		_radius = radius;
	}

	@Override
	public double evaluate(Node node) {
		double distanceFromRadius = getDistanceFromRadius(node);
		return distanceFromRadius == 0 ? 1 : 1 / distanceFromRadius;
	}

	private double getDistanceFromRadius(Node node) {
		return Math.abs(((CircularBackbone_MAC) node.getLayer(LayerType.MAC)).getDistanceFromCenter() - _radius);
	}

	public boolean electsMe(Node node) {
		if (getDistanceFromRadius(node) < _tolerance) {
			return true;
		}

		return false;
	}

	public BbCircleBuilderAgent createBuilderWithSchedule(Schedule creatorSchedule, double circleSchedulesDelay) {
		return new BbCircleBuilderAgent(getSender(), getReceiver(), _radius, creatorSchedule, circleSchedulesDelay);
	}
}
