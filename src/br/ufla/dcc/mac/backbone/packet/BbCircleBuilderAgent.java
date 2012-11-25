package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.mac.backbone.CircularBackbone_MAC;
import br.ufla.dcc.mac.backbone.Schedule;

public class BbCircleBuilderAgent extends ElectorAgent {

	private final int _radius;
	private final double _circleSchedulesDelay;
	private final Schedule _creatorSchedule;

	public BbCircleBuilderAgent(Address sender, NodeId receiver, int radius, Schedule creatorSchedule, double circleSchedulesDelay) {
		this(sender, receiver, PacketType.CONTROL, 6, radius, creatorSchedule, circleSchedulesDelay);
	}

	public BbCircleBuilderAgent(Address sender, NodeId receiver, PacketType type, double signalStrength, int radius, Schedule creatorSchedule,
			double circleSchedulesDelay) {
		super(sender, receiver, signalStrength, 5);
		_radius = radius;
		_creatorSchedule = creatorSchedule;
		_circleSchedulesDelay = circleSchedulesDelay;
	}

	@Override
	public double evaluate(Node node) {
		double distanceFromRadius = getDistanceFromRadius(node);
		return distanceFromRadius == 0 ? 1 : 1 / distanceFromRadius;
	}

	private double getDistanceFromRadius(Node node) {
		return Math.abs(((CircularBackbone_MAC) node.getLayer(LayerType.MAC)).getDistanceFromCenter() - _radius);
	}

	public Schedule getCreatorSchedule() {
		return _creatorSchedule;
	}

	public Schedule getScheduleForCurrentHop() {
		return getScheduleForHop(getHops());
	}

	private Schedule getScheduleForHop(double hop) {
		return new Schedule(_creatorSchedule.getCreationTime() + (hop * _circleSchedulesDelay), _creatorSchedule.getDelay());
	}

	public Schedule getScheduleForPreviousHop() {
		return getScheduleForHop(getHops() - 1);
	}

	public Schedule getScheduleForNextHop() {
		return getScheduleForHop(getHops() + 1);
	}
}