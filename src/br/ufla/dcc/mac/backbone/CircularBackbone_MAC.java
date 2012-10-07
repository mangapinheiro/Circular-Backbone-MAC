package br.ufla.dcc.mac.backbone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import br.ufla.dcc.event.wuc.BroadcastDistanceFromCenter;
import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.Interval;
import br.ufla.dcc.grubix.simulator.LayerException;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.SimulationFailedException;
import br.ufla.dcc.grubix.simulator.event.CrossLayerEvent;
import br.ufla.dcc.grubix.simulator.event.Finalize;
import br.ufla.dcc.grubix.simulator.event.Initialize;
import br.ufla.dcc.grubix.simulator.event.LayerState;
import br.ufla.dcc.grubix.simulator.event.LogLinkPacket;
import br.ufla.dcc.grubix.simulator.event.MACCarrierSensing;
import br.ufla.dcc.grubix.simulator.event.MACEvent;
import br.ufla.dcc.grubix.simulator.event.MACPacket.PacketType;
import br.ufla.dcc.grubix.simulator.event.MACProcessAckTimeout;
import br.ufla.dcc.grubix.simulator.event.MACSendACK;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.PhysicalLayerState;
import br.ufla.dcc.grubix.simulator.event.PhysicalPacket;
import br.ufla.dcc.grubix.simulator.event.ReceiverInformationEvent;
import br.ufla.dcc.grubix.simulator.event.SendingTerminated;
import br.ufla.dcc.grubix.simulator.event.StartSimulation;
import br.ufla.dcc.grubix.simulator.event.StatisticLogRequest;
import br.ufla.dcc.grubix.simulator.event.TransmissionFailedEvent;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.grubix.simulator.event.user.DoublePacketEvent;
import br.ufla.dcc.grubix.simulator.event.user.LostPacketEvent;
import br.ufla.dcc.grubix.simulator.event.user.RejectedPacketEvent;
import br.ufla.dcc.grubix.simulator.event.user.SendDelayedWakeUp;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;
import br.ufla.dcc.grubix.simulator.kernel.Configuration;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.AirState;
import br.ufla.dcc.grubix.simulator.node.BitrateAdaptationPolicy;
import br.ufla.dcc.grubix.simulator.node.Link;
import br.ufla.dcc.grubix.simulator.node.MACLayer;
import br.ufla.dcc.grubix.simulator.node.MACState;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.grubix.simulator.node.RadioState;
import br.ufla.dcc.grubix.simulator.node.user.AARFRateAdaptation;
import br.ufla.dcc.grubix.simulator.node.user.CarrierSenseInterruptedEvent;
import br.ufla.dcc.grubix.simulator.node.user.IEEE_802_11_TimingParameters;
import br.ufla.dcc.grubix.simulator.node.user.MAC_IEEE802_11bg_DCF;
import br.ufla.dcc.grubix.xml.ConfigurationException;
import br.ufla.dcc.grubix.xml.ShoXParameter;
import br.ufla.dcc.mac.backbone.packet.BbCircleBuilderAgent;
import br.ufla.dcc.mac.backbone.packet.BbCircleRootFinderAgent;
import br.ufla.dcc.mac.backbone.packet.ElectorAgent;
import br.ufla.dcc.mac.backbone.packet.GoodnessPkt;
import br.ufla.dcc.mac.backbone.packet.GoodnessRequestPkt;
import br.ufla.dcc.mac.backbone.packet.MACAgent;
import br.ufla.dcc.mac.backbone.packet.SchedulePacket;
import br.ufla.dcc.mac.backbone.state.AbstractNodeState;
import br.ufla.dcc.mac.backbone.state.NodeState;
import br.ufla.dcc.mac.backbone.state.StateFactory;
import br.ufla.dcc.mac.backbone.util.BbMacTiming;
import br.ufla.dcc.mac.backbone.wakeupcall.BroadcastScheduleDelayed;
import br.ufla.dcc.mac.backbone.wakeupcall.CTSFrameStartWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.CreateScheduleWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.DataFrameStartWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.DisseminateAgent;
import br.ufla.dcc.mac.backbone.wakeupcall.FindAgentTarget;
import br.ufla.dcc.mac.backbone.wakeupcall.FinishNeighborDiscoveryWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.GoSleepWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.NeighborDiscoveryWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.RTSFrameStartWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.WakeUpWUC;
import br.ufla.dcc.mac.packet.AckPacket;
import br.ufla.dcc.mac.packet.CTSPacket;
import br.ufla.dcc.mac.packet.DistanceFromCenterPacket;
import br.ufla.dcc.mac.packet.RTSPacket;
import br.ufla.dcc.utils.BackboneNodeState;
import br.ufla.dcc.utils.NeighborGoodness;
import br.ufla.dcc.utils.Simulation;

//import br.ufla.dcc.mac.backbone.wakeupcall.StartCarrierSensingWUC;

public class CircularBackbone_MAC extends MACLayer {

	private static final int MARGIN_NODE = 7;

	private static final Random RANDOM = new Random();

	private static final Schedule DEFAULT_SCHEDULE = new Schedule(20, 1000);

	private static final int BACKBONE_RADIUS = 100;

	private static final String NODE_STATE = "NodeState";

	private static final double GOODNESS_FACTOR = .7;

	private final BbMacTiming __timing = new BbMacTiming();

	/** Logger of this class. */
	protected static final Logger LOGGER = Logger.getLogger(CircularBackbone_MAC.class.getName());

	/** Index for the statistic type "drops/s". */
	private static final int DROPS_PER_SECOND = 0;

	/**
	 * object to hold all needed timing parameters, generated from the 802.11 Phy.
	 */
	private static IEEE_802_11_TimingParameters globalTimings;

	/** define the length of contention window. */
	private static final int[] CW = { 7, 15, 31, 63, 127, 255 };

	/** the maximum possible CW-Array Index. */
	private static final int MAX_CW_IDX = 5;

	/** the maximum number of retries performed. */
	private int _maxRetryCount;

	/** the current index into the CW-Size-Array. */
	private int _currentCW;
	/** the randomized time to do wait for a free medium after a collision. */
	private double _backoffTime;
	/** the time, when the last packet was started to be sent. */
	private double _lastTrySent;
	/** the position index of the last trySend call. */
	private int _lastTrySendPos;

	/** the time, when the last packet was started to be sent. */
	private double _lastSent;

	/**
	 * is true, if a send was issued and the MAC waits for the medium to get access.
	 */
	private boolean _pendingCS;

	/** if true, an ACK for the last sent packet ist still missing. */
	private boolean _pendingACK;

	/** if true, an ACK will be send soon, so no other packets can be send. */
	protected boolean _willSendACK;

	/** timeout for a pending ACK. */
	private double[] _ackDelay;

	/** time to wait for signal propagation. */
	private double _propagationDelay;

	/**
	 * is true, if sending should be issued after a free carrier for the duration DIFS.
	 */
	private boolean _immediateSend;

	/** holds the current to be sent packet, until it has passed the air. */
	private WlanFramePacket _currentOutPacket;

	/** holds the not yet processed transmissions. */
	protected final LinkedList<WlanFramePacket> _outQueue;

	/**
	 * Counting the number of dropped packets (retryLimit exceeded) since last reset.
	 */
	private int _droppedPackets;

	/**
	 * If true, nodes will always do arandom backofo,after an broadcast was received.
	 */
	@ShoXParameter(description = "If true, nodes will always do arandom backofo,after an broadcast was received.", defaultValue = "false")
	private boolean _backoffAfterReceivedBroadcast;

	/**
	 * true if MAC is in promiscuous and delivers ALL received data packet to upperLayer.
	 */
	@ShoXParameter(description = "If true, nodes can overhear messages destined for neighbors.", defaultValue = "false")
	private boolean _promiscuous;

	/**
	 * The maximum number of retries, the MAC should do until giving up.
	 */
	@ShoXParameter(description = "The maximum number of retries, the MAC should do until giving up.", defaultValue = "10")
	private int _retryLimit;

	/**
	 * method to adapt the bitrate to changing environment.
	 */
	@ShoXParameter(description = "method to adapt the bitrate to changing environment, chose from ARF, AARF, NO_ARF.", defaultValue = "NO_ARF")
	private String _rateAdaption;

	/**
	 * the required number of consecutive successful transmissions before rising the bitrate.
	 */
	@ShoXParameter(description = "the required number of consecutive successfull transmissions before " + "rising the bitrate.", defaultValue = "10")
	private int _raUpLevel;

	/**
	 * the tolerated number of consecutive failed transmissions before lowering the bitrate.
	 */
	@ShoXParameter(description = "the tolerated number of consecutive failed transmissions before " + "lowering the bitrate.", defaultValue = "2")
	private int _raDownLevel;

	/**
	 * For AARF the multiplier to apply on erroneous bitrate increases after premature increases.
	 */
	@ShoXParameter(description = "For AARF the multiplier to aply on erroneous bitrate increases " + "after premature increases.", defaultValue = "2")
	private int _raUpMult;

	/**
	 * the bitrate is rised if raUpLevel packets were received successfully or this time has passed since the last retried packet.
	 */
	@ShoXParameter(description = "the bitrate is rised if raUpLevel packets were received successfully or "
			+ "this time has passed since the last retried packet.", defaultValue = "10")
	private double _raTimeout;

	/** set to true, if the mac should include the current queue into the state. */
	@ShoXParameter(description = "set to true, if the mac should include the current queue into the state.", defaultValue = "false")
	private boolean _includeQueueInState;

	/** notes the id of the last sent and checked physical packet. */
	private int _lastSeenId = -1;

	/** set to true, if the packet was received at least once. */
	private boolean _currentWasReceivedOnce = false;

	private AbstractNodeState _nodeState;

	private final List<Schedule> _schedules;
	private final Map<Schedule, Set<NodeId>> _knownNeighbors;

	private double _distanceFromCenter = -1;

	private static Node __centerNode;

	private final Map<Integer, SortedSet<NeighborGoodness>> _neighborGoodness = new HashMap<Integer, SortedSet<NeighborGoodness>>();
	private final ArrayList<Integer> _knownAgents = new ArrayList<Integer>();

	private PacketType __frameFor;

	private boolean _willReceiveData;

	private CTSPacket _ctsToSend;

	private WakeUpCall _waitingForSleepTime;

	private boolean _willSendData;

	private Schedule __currentSchedule;

	/**
	 * Method to start this layer.
	 * 
	 * @param start
	 *            StartSimulation event to start the layer.
	 */
	private static final boolean USE_DEFAULT_SCHEDULE = true;

	private static final boolean USE_DEFAULT_DISTANCE_FROM_CENTER = true;

	private static int PKT_COUNT = 0;

	public CircularBackbone_MAC() {
		/** constructor. */
		_outQueue = new LinkedList<WlanFramePacket>();
		_droppedPackets = 0;
		_promiscuous = false;

		_nodeState = null;
		_schedules = new ArrayList<Schedule>();
		_knownNeighbors = new HashMap<Schedule, Set<NodeId>>();
	}

	public void setNodeState(NodeState nodeState) {
		_nodeState = StateFactory.createState(nodeState);
		_nodeState.configureNode(getNode());
		Simulation.Log.state(NODE_STATE, getNodeState().getId(), getNode());
	}

	public AbstractNodeState getNodeState() {
		return _nodeState;
	}

	@Override
	protected void processEvent(StartSimulation start) {
		Address thisAddress = new Address(id, LayerType.MAC);
		StatisticLogRequest slr = new StatisticLogRequest(thisAddress, getConfig().getSimulationSteps(1), DROPS_PER_SECOND);
		sendEventSelf(slr);

		goSleepNow();

		if (USE_DEFAULT_SCHEDULE) {
			followSchedule(DEFAULT_SCHEDULE);
		} else {
			WakeUpCall neighborDiscovery = new NeighborDiscoveryWUC(myAddress(), (RANDOM.nextFloat() * 3 * __timing.getEntireCycleSize()));
			sendEventSelf(neighborDiscovery);
		}

		if (USE_DEFAULT_DISTANCE_FROM_CENTER) {
			if (!isThereACenterNode() && verifyCenter(this.node.getPosition())) {
				__centerNode = getNode();
				_distanceFromCenter = 0;
			} else {
				double xSize = Configuration.getInstance().getXSize();
				double ySize = Configuration.getInstance().getYSize();

				Position pos1 = new Position(xSize / 2, ySize / 2);
				_distanceFromCenter = Simulation.Calculate.distanceBetweenPositions(pos1, getNode().getPosition());
			}

			Simulation.Log.state("DistanceFromCenter", (int) _distanceFromCenter, getNode());
		} else {

			if (!isThereACenterNode() && verifyCenter(this.node.getPosition())) {
				__centerNode = getNode();
				_distanceFromCenter = 0;
				Simulation.Log.state("CenterNode", 1, __centerNode);
				Simulation.Log.state("BackBone", BackboneNodeState.IS_BACKBONE, getNode());

				WakeUpCall broadcastCenterFound = new BroadcastDistanceFromCenter(sender, time(0.1));// TODO - Adjust this timing
				sendEventSelf(broadcastCenterFound);

				// BbCircleRootFinderAgent bbBuilderAgent = new BbCircleRootFinderAgent(sender, NodeId.ALLNODES, BACKBONE_RADIUS);
				// WakeUpCall broadcastBbBuilderAgent = new FindAgentTarget(sender, time(0.2), bbBuilderAgent);
				// sendEventSelf(broadcastBbBuilderAgent);
			}
		}
	}

	private final int DISCOVERING = 9;
	private final int NOT_DISCOVERING = 8;

	private boolean __discoveryMode = false;

	private Schedule _mainSchedule;

	@SuppressWarnings("unused")
	private void process(NeighborDiscoveryWUC discovery) {
		if (USE_DEFAULT_SCHEDULE) {
			return;
		}

		__frameFor = PacketType.CONTROL;
		setNodeState(NodeState.LISTENING);
		__discoveryMode = true;
		Simulation.Log.state("Discovering", DISCOVERING, getNode());

		WakeUpCall finishNeighborDiscovery = new FinishNeighborDiscoveryWUC(myAddress(), __timing.getEntireCycleSize());
		sendEventSelf(finishNeighborDiscovery);
	}

	@SuppressWarnings("unused")
	private void process(FinishNeighborDiscoveryWUC discovery) {
		if (hasSchedule()) {
			turnOffDiscoveryMode();
			return;
		}

		if (RANDOM.nextDouble() < 0.05) {
			WakeUpCall createSchedule = new CreateScheduleWUC(myAddress(), 0);// new Random().nextDouble() * __timing.getEntireCycleSize());
			sendEventSelf(createSchedule);

			turnOffDiscoveryMode();
			return;
		}

		sendEventSelf(discovery);
	}

	private void turnOffDiscoveryMode() {
		scheduleGoSleep(__timing.getAwakeCycleSize());
		__discoveryMode = false;
		double variation = __timing.getEntireCycleSize() * RANDOM.nextDouble();
		double normalInterval = __timing.getEntireCycleSize();

		if (numberOfKnownNeighbors() == 0) {
			normalInterval *= 3;
		} else if (numberOfKnownNeighbors() < 3) {
			normalInterval *= 7;
		} else {
			normalInterval *= 10;
		}

		WakeUpCall finishNeighborDiscovery = new NeighborDiscoveryWUC(myAddress(), variation + normalInterval);
		sendEventSelf(finishNeighborDiscovery);
		Simulation.Log.state("Discovering", NOT_DISCOVERING, getNode());
	}

	private int numberOfKnownNeighbors() {
		int numberOfKnownNeighbors = 0;

		for (Schedule schedule : _knownNeighbors.keySet()) {
			numberOfKnownNeighbors += knownNeighborsCountForSchedule(schedule);
		}
		return numberOfKnownNeighbors;
	}

	@SuppressWarnings("unused")
	private void process(AckPacket ack) {
		_outQueue.removeFirst();
		_pendingACK = false;
	}

	@SuppressWarnings("unused")
	private void process(BbCircleBuilderAgent circleBuilder) {
		Simulation.Log.state("Visited by agent", circleBuilder.getIdentifier(), getNode());

		Simulation.Log.state("MAC_Circle Node", circleBuilder.getIdentifier(), getNode());

		WakeUpCall forwardBackboneBuilder = new FindAgentTarget(sender, time(0.02), circleBuilder);
		sendEventSelf(forwardBackboneBuilder);
	}

	@SuppressWarnings("unused")
	private void process(BbCircleRootFinderAgent rootFinderAgent) {
		Simulation.Log.state("Visited by agent", rootFinderAgent.getIdentifier(), getNode());

		if (rootFinderAgent.electsMe(getNode())) {

			BbCircleBuilderAgent circleBuilder = rootFinderAgent.createBuilder();
			Simulation.Log.state("MAC_Circle Node", circleBuilder.getIdentifier(), getNode());

			WakeUpCall forwardBackboneBuilder = new FindAgentTarget(sender, time(0.02), circleBuilder);
			sendEventSelf(forwardBackboneBuilder);
		} else {
			WakeUpCall broadcastBbBuilderAgent = new FindAgentTarget(sender, time(0.02), rootFinderAgent);
			sendEventSelf(broadcastBbBuilderAgent);
		}
	}

	@SuppressWarnings("unused")
	private void process(BroadcastDistanceFromCenter broadcastDistance) {
		WlanFramePacket centerPacket = new DistanceFromCenterPacket(myAddress(), NodeId.ALLNODES, getDistanceFromCenter());
		sendLanPacket(centerPacket);
	}

	@SuppressWarnings("unused")
	private void process(BroadcastScheduleDelayed scheduleDelayed) {
		if (hasThisNodeMultipleSchedules()) {
			// don't propagate
			return;
		}
		sendPacketDown((WlanFramePacket) scheduleDelayed.getPkt());
	}

	@SuppressWarnings("unused")
	private void process(CreateScheduleWUC event) {
		if (hasSchedule()) {
			return;
		}

		Schedule schedule = new Schedule(SimulationManager.getInstance().getCurrentTime(), __timing.getEntireCycleSize());

		followSchedule(schedule);
		__currentSchedule = schedule;
		broadcastSchedule(schedule); // ////""""""""""""""""""""""""""
	}

	@SuppressWarnings("unused")
	private void process(CrossLayerEvent cle) {
		cle.forwardUp(this);
	}

	@SuppressWarnings("unused")
	private void process(CTSFrameStartWUC event) {
		__frameFor = PacketType.CTS;

		if (_ctsToSend != null) {
			startCarrierSense(__timing.getDifs(), __timing.getRandomContentionTime());
		}
	}

	@SuppressWarnings("unused")
	private void process(CTSPacket cts) {
		if (!isPacketForThisNode(cts)) {
			goSleepNow();
			return;
		}

		_willSendData = true;
	}

	@SuppressWarnings("unused")
	private void process(DataFrameStartWUC event) {
		__frameFor = PacketType.DATA;

		if (!_willReceiveData && !_willSendData) {
			goSleepNow();
		} else {
			scheduleGoSleep(__timing.getAwakeCycleSize());
		}

		if (_willSendData) {
			startCarrierSense(__timing.getDifs(), __timing.getRandomContentionTime());
		}

		_willSendData = _willReceiveData = false;
	}

	public void process(DisseminateAgent disseminate) {
		MACAgent agent = disseminate.getAgent();

		NodeId chosenCandidate = this.getBestCandidate(agent);

		if (chosenCandidate != null) {
			agent.setSender(this.getSender());
			agent.setupToForwardTo(chosenCandidate);

			// sendLanPacket(agent);
		} else {

			// TODO - HANDLE "NO CANDIDATES FOUND"

			// this.setOnBackbone(false);
			// SimulationManager.logNodeState(this.getId(), "BackBone", "int",
			// BackboneNodeState.ERROR + "");
			// agent.removeHop();
			//
			// if (getSender().getId() == sinkNodeId) {
			// System.out.println("=======================> IMPOSSIBLE TO CREATE BACKBONE, NO NODE IS ELIGIBLE!!!");
			// return;
			// }
			// RefuseAgentPacket refusePkt = new RefuseAgentPacket(this.sender,
			// this.parent, agent);
			// this.sendEventSelf(new SendDelayedWUC(this.getSender(),
			// Math.random() * 100, refusePkt));
		}

	}

	@SuppressWarnings("unused")
	private void process(DistanceFromCenterPacket distancePacket) {
		// TODO - REIMPLEMENT THIS METHOD USING SIGNAL STENGHT TO CALCULATE THE
		// DISTANCE
		// double distanceFromCenter = distancePacket.getDistanceFromCenter();
		// double signalStength = distancePacket.getSignalStrength();

		Position senderPosition = Simulation.Get.nodePosition(distancePacket.getSender().getId());
		Position myPosition = getNode().getPosition();

		double distanceFromNode = Simulation.Calculate.distanceBetweenPositions(senderPosition, myPosition);
		double distanceFromCenter = distanceFromNode + distancePacket.getDistanceFromCenter();

		if (isDistanceFromCenterNotSet() || distanceFromCenter < getDistanceFromCenter()) {
			setDistanceFromCenter(distanceFromCenter);

			WakeUpCall broadcastDistanceFromCenter = new BroadcastDistanceFromCenter(myAddress(), time(0.1));
			sendEventSelf(broadcastDistanceFromCenter);
		}
	}

	@SuppressWarnings("unused")
	private void process(FindAgentTarget findTarget) {
		_neighborGoodness.put(findTarget.getAgent().getIdentifier(), new TreeSet<NeighborGoodness>());

		GoodnessRequestPkt goodnessRequest = new GoodnessRequestPkt(myAddress(), NodeId.ALLNODES, findTarget.getAgent());
		// sendLanPacket(goodnessRequest);

		WakeUpCall disseminateAgent = new DisseminateAgent(myAddress(), time(0.1), findTarget.getAgent());
		sendEventSelf(disseminateAgent);
	}

	@SuppressWarnings("unused")
	private void process(GoodnessPkt goodnessPkt) {
		goodnessPkt.getSenderGoodness();

		NeighborGoodness NeighborGoodness = new NeighborGoodness(goodnessPkt.getSender().getId(), goodnessPkt.getSenderGoodness());
		getGoodnessListForAgent(goodnessPkt.getAgent()).add(NeighborGoodness);
	}

	@SuppressWarnings("unused")
	private void process(GoodnessRequestPkt goodnessRequest) { // TODO - CHANGE TO RESPOND ONLY ONCE PER AGENT
		if (knowsAgent(goodnessRequest.getAgent())) {
			return; // do not respond to known agents
		}

		addKnownAgent(goodnessRequest.getAgent());

		GoodnessPkt goodness = goodnessRequest.evaluate(getNode());
		Simulation.Log.state("MAC_Goodness", goodness.getSenderGoodness(), getNode());

		WakeUpCall sendGoodness = new SendDelayedWakeUp(myAddress(), RANDOM.nextDouble() * time(0.01), goodness);
		sendEventSelf(sendGoodness);
	}

	@SuppressWarnings("unused")
	private void process(GoSleepWUC wakeUp) {
		if (isInDiscoveryMode()) {
			return;
		}

		if (_waitingForSleepTime == wakeUp) {
			setNodeState(NodeState.SLEEPING);
			// _nodeState = new Sleeping();
		}
	}

	private boolean isInDiscoveryMode() {
		return __discoveryMode;
		// return getNodeState().isState(NodeState.DISCOVERY_MODE);
	}

	/**
	 * internal method to handle MACCarrierSensing.
	 * 
	 * @param macCS
	 *            the to be handled event.
	 */
	@SuppressWarnings({ "unused" })
	private void process(MACCarrierSensing macCS) {

		setNodeState(NodeState.LISTENING);
		LOGGER.debug("Ending carrier sensing at " + SimulationManager.getInstance().getCurrentTime());

		if (macCS.isNoCarrier()) {

			WlanFramePacket packet;

			packet = _outQueue.peekFirst();

			switch (__frameFor) {
			case CONTROL:
				if (__currentSchedule != null) {
					broadcastSchedule(__currentSchedule);
				}

				break;

			case RTS:

				if (_outQueue.getFirst().getReceiver() == NodeId.ALLNODES) {
					if (packet == null) {
						LOGGER.warn("carrier sensing returned but no out packet present !");
						return;
					}

					applyBitrate(packet, -1);
					LOGGER.debug("Sent broadcast");
					preparePacketToBeSent(packet);
					sendPacket(packet);

					Simulation.Log.state("Paquet sent", PKT_COUNT++, getNode());

					break;
				} else {
					RTSPacket rts = new RTSPacket(myAddress(), packet.getReceiver());
					applyDefaultBitrate(rts);
					sendPacket(rts);
				}

				break;

			case CTS:

				if (_ctsToSend != null) {
					applyDefaultBitrate(_ctsToSend);
					sendPacket(_ctsToSend);
					_ctsToSend = null;
				}

				break;

			case DATA:

				if (packet == null) {
					LOGGER.warn("carrier sensing returned but no out packet present !");
					return;
				}

				preparePacketToBeSent(packet);
				sendPacket(packet);

				if (_pendingACK = packet.isAckRequested()) {

					double delay = packet.getDuration() + _ackDelay[packet.getBitrateIdx()];

					WakeUpCall wakeUpCall = new MACProcessAckTimeout(sender, delay);
					sendEventSelf(wakeUpCall);
				}

				Simulation.Log.state("Paquet sent", PKT_COUNT++, getNode());

				break;
			default:
				LOGGER.error("Carrier sensing for " + __frameFor + " frame not implemented!!!");

				break;
			}

		} else {
			calcBackoffTime(macCS.getCsStart());
		}
	}

	/**
	 * internal method to handle MACprocessAckTimeout events.
	 * 
	 * @param ackTimeout
	 *            the to be handled event.
	 */
	@SuppressWarnings("unused")
	private void process(MACProcessAckTimeout ackTimeout) {

		// see above in processMACCarrierSensing, in case of changes of this
		// method.

		if (_pendingACK) {
			_pendingACK = false;

			_currentOutPacket.getRaPolicy().processFailure(getNode().getCurrentTime());

			int retryCount = _currentOutPacket.getRetryCount() + 1;

			if (retryCount <= _maxRetryCount) {
				_currentOutPacket.setRetryCount(retryCount);
				calcBackoffTime(0.0);
				_currentOutPacket.setReadyForTransmission(true);
				_currentOutPacket.reset(); // prepare the packet for resending
											// ##
				applyBitrate(_currentOutPacket, -1);

				// enforce recalculation of a random backoff, since a collision
				// may have happened.

				if (_immediateSend || (_backoffTime <= 0.0)) {
					setBackoffTime();
				}

				// +++++++++++++ trySend(7); // #7#
			} else {
				Packet llP = _currentOutPacket.getEnclosedPacket();

				if (llP != null) {
					sendEventUp(new TransmissionFailedEvent(sender, llP));
				}
				_currentOutPacket = null;
				this._droppedPackets++;
				// +++++++++++++ trySend(8); // #8#
			}
		} else {
			if (_currentOutPacket != null) {
				if (_currentOutPacket.getReceiver() != NodeId.ALLNODES) {
					_currentOutPacket.getRaPolicy().processSuccess();
					_currentCW = 0;
				}
				_currentOutPacket = null;
			}
			// +++++++++++++ trySend(9); // #9#
		}
	}

	/**
	 * internal method to immediately send an ACK.
	 * 
	 * @param sendACK
	 *            the to be processed event.
	 */
	@SuppressWarnings("unused")
	private void process(MACSendACK sendAck) {
		sendPacket(sendAck.getAck());
		/*
		 * If this ack has left the building, an event will inform us and uppon handling of this event, trySend() will be issued.
		 */
	}

	/**
	 * method to process the receiver information event.
	 * 
	 * @param rie
	 *            the to be processed event.
	 */
	@SuppressWarnings("unused")
	private void process(ReceiverInformationEvent rie) {

		Packet packet = rie.getPacket();

		if ((packet != null) && (packet instanceof PhysicalPacket)) {
			PhysicalPacket pPacket = (PhysicalPacket) packet;
			WlanFramePacket mPacket = (WlanFramePacket) pPacket.getEnclosedPacket();

			if (!packet.getReceiver().equals(NodeId.ALLNODES) && !mPacket.isTerminal()) {
				Packet innerP = mPacket.getHighestEnclosedPacket();
				int packetId = innerP.getId().asInt() * 10 + innerP.getLayer().ordinal();

				if (_lastSeenId != packetId) {
					_currentWasReceivedOnce = false;
				}

				if (pPacket.hasReceived(pPacket.getReceiver())) {
					if (_lastSeenId == packetId) {
						sendEventUp(new DoublePacketEvent(sender, mPacket.getEnclosedPacket()));
					}

					_currentWasReceivedOnce = true;
				} else {
					if ((mPacket.getRetryCount() >= _maxRetryCount) && !_currentWasReceivedOnce) {
						sendEventUp(new LostPacketEvent(sender, mPacket.getEnclosedPacket()));
					}
				}

				_lastSeenId = packetId;
			}
		}
	}

	/**
	 * this method is to botch/debug an error of this class, where a packet is sent too early.
	 * 
	 * @param rpe
	 *            the event containing the too early sent packet.
	 */
	@SuppressWarnings("unused")
	private void process(RejectedPacketEvent rpe) {
		double now = node.getCurrentTime(), lts = now - _lastTrySent, ls = now - _lastSent;

		String msg = "";

		if (_pendingCS) {
			msg = "pCS,";
		}
		if (_pendingACK) {
			msg += "pACK,";
		}
		if (_willSendACK) {
			msg += "wsACK,";
		}
		if (_immediateSend) {
			msg += "imS,";
		}
		if (_currentOutPacket != null) {
			msg += "cOut, ";
		}

		msg = "state: " + msg + "tsPos:" + _lastTrySendPos + " now: " + now + "lts: " + lts + " ls: " + ls;

		Packet p = rpe.getCurrentPacket();

		if (p != null) {
			msg += "curr. p: " + p.getHighestEnclosedPacket().getClass().toString();
		}

		Packet packet = rpe.getPacket();

		if (packet != null) {
			LOGGER.warn(msg);

			if (packet instanceof WlanFramePacket) {
				tryHandleDroppedPacket(rpe, (WlanFramePacket) packet);
			}
		} else {
			if (rpe instanceof CarrierSenseInterruptedEvent) {
				LOGGER.info(msg);

				CarrierSenseInterruptedEvent csi = (CarrierSenseInterruptedEvent) rpe;
				LOGGER.info(id + " cs was interrupted and restarted. No Data is lost. (d=" + csi.getRest() + " p=" + csi.getPos() + ")");
			}
		}
	}

	@SuppressWarnings("unused")
	private void process(RTSFrameStartWUC event) {
		__frameFor = PacketType.RTS;

		if (_outQueue.size() > 0) {
			startCarrierSense(__timing.getDifs(), __timing.getRandomContentionTime());
		}
	}

	@SuppressWarnings("unused")
	private void process(RTSPacket rts) {
		if (!isPacketForThisNode(rts)) {
			goSleepNow();
			return;
		}

		_willReceiveData = true;

		CTSPacket cts = new CTSPacket(myAddress(), rts.getSender().getId());
		applyDefaultBitrate(cts);

		_ctsToSend = cts;
	}

	@SuppressWarnings("unused")
	private void process(SchedulePacket schedulePacket) {
		// if (!hasSchedule()) {
		// acceptSchedule(schedulePacket);
		// // broadcastSchedule(packet.getSchedule(), __timing.getRandomContentionTime());
		// return;
		// }

		if (isFollowingSchedule(schedulePacket.getSchedule())) {
			return;
		}

		if (knowsNeighbor(schedulePacket.getSender())) {
			return;
		}

		acceptSchedule(schedulePacket);
	}

	private boolean knowsNeighbor(Address sender) {
		return scheduleForNeighbor(sender.getId()) != null;
	}

	private Schedule scheduleForNeighbor(NodeId neighborId) {
		for (Schedule schedule : _schedules) {
			final Set<NodeId> knownNeighborsForSchedule = _knownNeighbors.get(schedule);
			if (knownNeighborsForSchedule != null && knownNeighborsForSchedule.contains(neighborId)) {
				return schedule;
			}
		}

		return null;
	}

	@SuppressWarnings("unused")
	private void process(SendDelayedWakeUp sendDelayed) {
		sendPacketDown((WlanFramePacket) sendDelayed.getPkt());
	}

	@SuppressWarnings("unused")
	private void process(SendingTerminated wuc) {
		_willSendACK = false;
		// trySend(4); // #4# TODO - Should try send for Adaptative listening
	}

	@SuppressWarnings("unused")
	private void process(StatisticLogRequest logRequest) {
		logStatistic(logRequest);
		sendEventSelf(logRequest);
	}

	@SuppressWarnings("unused")
	private void process(WakeUpWUC wakeUp) {
		if (!isFollowingSchedule(wakeUp.getSchedule())) {
			return;
		}

		if (isInDiscoveryMode()) {
			sendEventSelf(wakeUp);
			return;
		}

		setNodeState(NodeState.LISTENING);

		if (_mainSchedule == __currentSchedule) {
			startCarrierSense(0, time(__timing.getRandomContentionTime()));
		}

		__frameFor = PacketType.CONTROL;
		__currentSchedule = wakeUp.getSchedule();

		WakeUpCall waitRTS = new RTSFrameStartWUC(getSender(), __timing.getListenPeriodForSync());
		sendEventSelf(waitRTS);

		WakeUpCall waitCTS = new CTSFrameStartWUC(getSender(), waitRTS.getDelay() + __timing.getListenPeriodForRTS());
		sendEventSelf(waitCTS);

		WakeUpCall waitForDataOrGoToSleep = new DataFrameStartWUC(getSender(), waitCTS.getDelay() + __timing.getListenPeriodForCTS());
		sendEventSelf(waitForDataOrGoToSleep);

		wakeUp = new WakeUpWUC(myAddress(), __timing.getEntireCycleSize(), wakeUp.getSchedule());
		sendEventSelf(wakeUp);
		// scheduleWakeUpForSchedule(__currentSchedule);
	}

	private void findMainSchedule() {
		int neighborsCount = _knownNeighbors.get(_mainSchedule).size();
		for (Schedule sch : _schedules) {
			if (_knownNeighbors.get(sch).size() > neighborsCount) {
				_mainSchedule = sch;
			}
		}
	}

	/**
	 * process the finalize event.
	 * 
	 * @param end
	 *            the to be processed event.
	 */
	@Override
	protected void processEvent(Finalize end) {
		double now = SimulationManager.getInstance().getCurrentTime();

		now -= _lastTrySent;
		now = getConfig().getSeconds(now);

		int i = _outQueue.size();

		if (i > 0) {
			if (_pendingCS || (now < 0.05)) {
				LOGGER.info(id + " has still " + i + " outstanding packets left (busy).");
			} else {
				LOGGER.warn(id + " has still " + i + " outstanding packets (idle).");
			}
		}
	}

	/**
	 * @see br.ufla.dcc.grubix.simulator.node.Layer#processEvent(br.ufla.dcc.grubix.simulator.event.Initialize)
	 * @param init
	 *            Initialize event to start up the layer.
	 */
	@Override
	public final void processEvent(Initialize init) {
		int n = globalTimings.getMaxBitrateIDX();
		_ackDelay = new double[n + 1];

		for (int i = 0; i <= n; i++) {
			WlanFramePacket f = new WlanFramePacket(sender, NodeId.ALLNODES, PacketType.ACK, -1.0);
			raDefaultPolicy.setBitrateIdx(i);
			applyBitrate(f, -1);
			_ackDelay[i] = globalTimings.getSifs() + f.getDuration() + 2.0 * globalTimings.getSyncDuration() + 2.5 * _propagationDelay;
		}

		_maxRetryCount = Math.max(_retryLimit, MAX_CW_IDX + 1);
		raDefaultPolicy.setBitrateIdx(n);
	}

	/**
	 * process the incoming events MACCarrierSensing and MACprocessAckTimeout.
	 * 
	 * @param wuc
	 *            contains the to be processed wakeup-call.
	 * @throws LayerException
	 *             if the wakeup call could not be processed.
	 */
	@Override
	public void processWakeUpCall(WakeUpCall wuc) throws LayerException {
		try {
			Method method = getClass().getDeclaredMethod("process", wuc.getClass());
			method.invoke(this, wuc);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new LayerException("MacModule of Node " + id + " received unexpected wakeup call: " + wuc.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new LayerException("MacModule of Node " + id + "received wakeup call " + wuc.getClass().getName());
		}
	}

	private void acceptSchedule(SchedulePacket schedulePacket) {

		if (hasMultipleSchedules()) {
			return;
		}

		if (__currentSchedule != null && !(knownNeighborsCountForSchedule(__currentSchedule) > 0)) {
			unfollowSchedule(__currentSchedule);
		}

		followSchedule(schedulePacket.getSchedule());
		registerNeighborForSchedule(schedulePacket.getSender().getId(), schedulePacket.getSchedule());

	}

	private int knownNeighborsCountForSchedule(Schedule _currentSchedule) {
		Set<NodeId> knownNeighbors = _knownNeighbors.get(_currentSchedule);

		if (knownNeighbors == null) {
			return 0;
		}

		return knownNeighbors.size();
	}

	private void unfollowSchedule(Schedule schedule) {
		_schedules.remove(schedule);
		_knownNeighbors.remove(schedule);
		Simulation.Log.state("NumberOfSchedules", _schedules.size(), getNode());
	}

	private void addKnownAgent(ElectorAgent agent) {
		_knownAgents.add(agent.getIdentifier());
	}

	/**
	 * internal method to apply the current used bitrate. Afterwards getSendingTime is valid.
	 * 
	 * @param f
	 *            the frame, where the bitrate is applied to.
	 * @param forcedBitrateIdx
	 *            use a value >= 0 to set a specific bitrate index, (needs to be obtained from a rate adaptation object).
	 */
	private void applyBitrate(WlanFramePacket f, int forcedBitrateIdx) {
		if (forcedBitrateIdx >= 0) {
			/*
			 * This is only used for ACK or other control packets, which are not stored and processed in the normal queue of to be sent packets.
			 */
			f.setBPS(globalTimings, forcedBitrateIdx);
		} else {
			BitrateAdaptationPolicy raLocal = raDefaultPolicy;
			Link link = getMetaDataLink(f);

			if (link != null) {
				if (link.getBitrateAdaptationPolicy() != null) {
					raLocal = link.getBitrateAdaptationPolicy();
				}
			}

			f.setBPS(globalTimings, raLocal);
		}
	}

	private void applyDefaultBitrate(WlanFramePacket goodnessRequest) {
		applyBitrate(goodnessRequest, -1);
	}

	private void broadcastSchedule(Schedule schedule) {
		broadcastSchedule(schedule, 0);
	}

	private void broadcastSchedule(Schedule schedule, double delay) {
		if (hasMultipleSchedules()) {
			// don't propagate
			return;
		}

		SchedulePacket schedulePacket = new SchedulePacket(myAddress(), NodeId.ALLNODES, schedule);

		if (delay <= 0) {
			sendPacketDown(schedulePacket);
			return;
		}

		SendDelayedWakeUp sendDelayedWakeUp = new BroadcastScheduleDelayed(myAddress(), delay, schedulePacket);
		sendEventSelf(sendDelayedWakeUp);
	}

	private boolean hasMultipleSchedules() {
		return _schedules.size() > 1;
	}

	/**
	 * internal method to modify/set the backoff delay.
	 * 
	 * @param csStart
	 *            time from which the carrier seng started.
	 */
	private void calcBackoffTime(double csStart) {
		if (_immediateSend) {
			setBackoffTime();
		} else {
			long passedBackoffSlots = (long) Math.floor((getNode().getCurrentTime() - csStart) / globalTimings.getSlotTime());
			_backoffTime -= passedBackoffSlots * globalTimings.getSlotTime();

			if (_backoffTime <= 0.0) {
				_immediateSend = true;
				_backoffTime = 0.0;
			}
		}
	}

	private void followSchedule(Schedule schedule) {

		if (!hasSchedule()) {
			Simulation.Log.state("Schedule", schedule.getId(), getNode());
			_mainSchedule = schedule;
		} else {
			Simulation.Log.state("Margin with schedule", schedule.getId(), getNode());
			Simulation.Log.state("Margin node", MARGIN_NODE, getNode());
		}

		_schedules.add(schedule);

		scheduleWakeUpForSchedule(schedule);
		Simulation.Log.state("NumberOfSchedules", _schedules.size(), getNode());

		if (__currentSchedule == null) {
			WakeUpCall finshDiscovery = new FinishNeighborDiscoveryWUC(myAddress());
			sendEventSelf(finshDiscovery);
		}
	}

	private NodeId getBestCandidate(MACAgent agent) {
		if (_neighborGoodness.get(agent.getIdentifier()).isEmpty()) {
			return null;
		}

		return _neighborGoodness.get(agent.getIdentifier()).last().getNodeId();
	}

	public double getDistanceFromCenter() {
		return _distanceFromCenter;
	}

	private SortedSet<br.ufla.dcc.utils.NeighborGoodness> getGoodnessListForAgent(MACAgent agent) {
		if (_neighborGoodness.get(agent.getIdentifier()) == null) {
			_neighborGoodness.put(agent.getIdentifier(), new TreeSet<NeighborGoodness>());
		}
		return _neighborGoodness.get(agent.getIdentifier());
	}

	/**
	 * currently no states needed, thus no statechanges possible.
	 * 
	 * @return the current MAC-state.
	 */
	@Override
	public MACState getState() {
		int size = _outQueue.size();

		ArrayList<WlanFramePacket> queue = null;

		if (_includeQueueInState) {
			queue = new ArrayList<WlanFramePacket>();

			if (_currentOutPacket != null) {
				queue.add(_currentOutPacket);
			}

			queue.addAll(_outQueue);
		}

		if (_currentOutPacket != null) {
			size++;
		}

		MACState state = new MACState(raDefaultPolicy, 16.0, size);

		if (queue != null) {
			state.setQueue(queue);
		}

		return state;
	}

	private void goSleepNow() {
		scheduleGoSleep(0);
	}

	private boolean hasSchedule() {
		return _schedules.size() > 0;
	}

	/**
	 * method, to read the configuration parameters and to configure the MAC accordingly.
	 * 
	 * @param configuration
	 *            the configuration to use for this node.
	 * @throws ConfigurationException
	 *             is thrown, if an illegal rate adaptation method is chosen.
	 */
	@Override
	public void initConfiguration(Configuration configuration) throws ConfigurationException {
		super.initConfiguration(configuration);
		int i = 1, mode = AARFRateAdaptation.NO_ARF, maxBitrateIDX;
		double x;

		if (globalTimings == null) {
			PhysicalLayerState phyState = (PhysicalLayerState) getNode().getLayerState(LayerType.PHYSICAL);
			globalTimings = (IEEE_802_11_TimingParameters) phyState.getTimings();
		}
		timings = globalTimings;
		maxBitrateIDX = globalTimings.getMaxBitrateIDX();

		if (_rateAdaption.equals("ARF")) {
			mode = AARFRateAdaptation.ARF;
		} else if (_rateAdaption.equals("AARF")) {
			mode = AARFRateAdaptation.AARF;
			i = _raUpMult;
		} else if (_rateAdaption.equals("NO_ARF")) {
			LOGGER.info("no rate adaptation used.");
		} else {
			throw new ConfigurationException("MacModule of Node " + id + " illegal rate adaption policy " + _rateAdaption);
		}

		x = getConfig().getSimulationSteps(_raTimeout);
		raDefaultPolicy = new AARFRateAdaptation(this, maxBitrateIDX, maxBitrateIDX, mode, -_raDownLevel, _raUpLevel, i, x);
		_currentCW = 0;
		_pendingCS = false;
		_pendingACK = false;
		_willSendACK = false;
		_immediateSend = true;
		_currentOutPacket = null;

		_propagationDelay = getConfig().getPropagationDelay();
		if (_propagationDelay != getConfig().getSimulationSteps(1.0e-7)) {
			throw new SimulationFailedException("propagation delay is invalid for " + MAC_IEEE802_11bg_DCF.class.getName());
		}
	}

	private boolean isBroadcast(WlanFramePacket packet) {
		return packet.getReceiver() == NodeId.ALLNODES;
	}

	private boolean isDataPacket(WlanFramePacket packet) {
		return isPacketOfType(packet, PacketType.DATA);
	}

	private boolean isDistanceFromCenterNotSet() {
		return getDistanceFromCenter() < 0;
	}

	private boolean isFollowingSchedule(Schedule schedule) {
		return _schedules.contains(schedule);
	}

	/**
	 * @param packet
	 * @return true - if the packet is a broadcast
	 */
	private boolean isPacketForAllNodes(Packet packet) {
		return packet.getReceiver() == NodeId.ALLNODES;
	}

	/**
	 * @param packet
	 * @return true - if this node is the only receiver for the packet
	 */
	private boolean isPacketForThisNode(Packet packet) {
		return packet.getReceiver().equals(id);
	}

	private boolean isPacketOfType(WlanFramePacket packet, PacketType rts) {
		return packet.getType() == rts;
	}

	private boolean isRTSPacket(WlanFramePacket packet) {
		return isPacketOfType(packet, PacketType.RTS);
	}

	private boolean isThereACenterNode() {
		return __centerNode != null;
	}

	private boolean knowsAgent(ElectorAgent agent) {
		return _knownAgents.contains(agent.getIdentifier());
	}

	/**
	 * Method to log a statistic event of a certain type.
	 * 
	 * @param slr
	 *            A wakeup call with information on what to log
	 */
	private void logStatistic(StatisticLogRequest slr) {
		if (slr.getStatisticType() == DROPS_PER_SECOND) {
			SimulationManager.logStatistic(id, LayerType.MAC, "time", "Dropped Packets / s",
					Double.toString(SimulationManager.getInstance().getCurrentTime()), Integer.toString(_droppedPackets));
			_droppedPackets = 0;
		}
	}

	/**
	 * @see br.ufla.dcc.grubix.simulator.node.Layer#lowerSAP(br.ufla.dcc.grubix.simulator.event.Packet)
	 * 
	 * @param packet
	 *            to process coming from lower layer.
	 */
	@Override
	public final void lowerSAP(Packet packet) {

		if (!_nodeState.acceptsPacket(packet)) {
			return;
		}

		if (hasSchedule()) {
			if (!knowsNeighbor(packet.getSender())) {
				registerNeighborForSchedule(packet.getSender().getId(), __currentSchedule);
			} else {
				Schedule scheduleForNeighbor = scheduleForNeighbor(packet.getSender().getId());
				if (_mainSchedule == __currentSchedule && __currentSchedule != scheduleForNeighbor) {
					// if (knownNeighborsCountForSchedule(__currentSchedule) >= knownNeighborsCountForSchedule(scheduleForNeighbor)) {
					unregisterNeighborForSchedule(packet.getSender().getId(), scheduleForNeighbor);
					registerNeighborForSchedule(packet.getSender().getId(), __currentSchedule);
				}
			}
		}

		// if (!packetIsForThisNode(packet) && !packetIsForAllNodes(packet)) {
		// return;
		// }

		try {
			Method declaredMethod = getClass().getDeclaredMethod("process", packet.getClass());
			declaredMethod.invoke(this, packet);
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			LOGGER.error("This layer doesn't handle packets of type " + packet.getClass().getName());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WlanFramePacket frame = (WlanFramePacket) packet, ack;

		/*
		 * On any modification of this method, please ensure, that trySend() will be called after the reception of a packet, since this might have
		 * blocked a new outgoing packet from being sent.
		 */
		boolean doDropPacket = !isPacketForThisNode(frame) && !isPacketForAllNodes(frame);
		NodeId senderId = frame.getSender().getId();

		if (doDropPacket && _promiscuous && !frame.isTerminal() && !senderId.equals(id)) {
			doDropPacket = false;
		}

		if (doDropPacket) {
			if (LOGGER.isDebugEnabled()) { // check if enabled to reduce
											// performance impact
				LOGGER.debug("Packet not for this node. Throwing away " + frame);
			}
			// advance queue after receiving neither a broadcast nor a packet
			// for this node.
			// +++++++++++++ trySend(0); // #0#
		} else {
			if (isPacketForThisNode(frame) && frame.isTerminal() && (frame.isControl())) {
				switch (frame.getType()) {
				case ACK:
					_pendingACK = false;
					_immediateSend = true;
					break;
				case RTS:
					break;
				case CTS:
					break;
				default:
					break;
				}
				// TODO handle MAC control packets, like [rts, cts]

				// no trySend() needeed here, since the wakeupcall processing
				// MACProcessAckTimeout will handle this.
			} else {

				sendPacket(frame.getEnclosedPacket());

				if (frame.isAckRequested()) {
					_willSendACK = true;

					ack = new AckPacket(sender, packet.getSender().getId(), frame.getSignalStrength());

					applyBitrate(ack, frame.getBitrateIdx());
					sendPacket(ack);
					//
					// WakeUpCall wuc = new MACSendACK(sender, globalTimings.getSifs(), ack);
					// sendEventSelf(wuc);

					// no trySend() here, since any send prior the send of the
					// ack makes no sense.
				} else {
					if (_backoffAfterReceivedBroadcast && isPacketForAllNodes(packet)) {
						setBackoffTime();
					}

					// advance queue after receiving a broadcast.
					// +++++++++++++ trySend(1); // #1#
				}
			}
		}
	}

	private void unregisterNeighborForSchedule(NodeId neighborId, Schedule schedule) {
		Set<NodeId> neighborsForSchedule = _knownNeighbors.get(schedule);

		if (neighborsForSchedule == null) {
			return;
		}

		neighborsForSchedule.remove(neighborId);

		if (!(neighborsForSchedule.size() > 0)) {
			unfollowSchedule(schedule);
		}
	}

	private Address myAddress() {
		return getSender();
	}

	private boolean packetNeedsAck(WlanFramePacket packet) {
		return !isBroadcast(packet) && (isDataPacket(packet) || isRTSPacket(packet));
	}

	private void preparePacketToBeSent(WlanFramePacket packet) {
		packet.getRaPolicy().reset(getNode().getCurrentTime());
		packet.setAckRequested(packetNeedsAck(packet));
		packet.setReadyForTransmission(true);
	}

	private void registerNeighborForSchedule(NodeId neighborId, Schedule schedule) {

		if (schedule == null) {
			return;
		}

		// Next step: Fix the multi schedule problem. The nodes should be registering neighbor
		// in such a way they become listed in the schedule the node knows more neighbors
		// and also should unfollow the schedules with no neighbor if there is other to follow

		Set<NodeId> neighborsForSchedule = _knownNeighbors.get(schedule);

		if (neighborsForSchedule == null) {
			neighborsForSchedule = new TreeSet<NodeId>();
			_knownNeighbors.put(schedule, neighborsForSchedule);
		}

		neighborsForSchedule.add(neighborId);

		findMainSchedule();

		Simulation.Log.NumberOfKnownNeighbors(numberOfKnownNeighbors(), getNode());
	}

	private void scheduleGoSleep(double delayInSteps) {
		WakeUpCall goSleep = new GoSleepWUC(myAddress(), delayInSteps);
		_waitingForSleepTime = goSleep;
		sendEventSelf(goSleep);
	}

	private void scheduleWakeUpForSchedule(Schedule schedule) {
		double currentTime = SimulationManager.getInstance().getCurrentTime();
		double delayToWakeUpInSteps = schedule.getDelay(currentTime);
		WakeUpCall wakeUp = new WakeUpWUC(myAddress(), delayToWakeUpInSteps, schedule);
		sendEventSelf(wakeUp);
		double delayToSleepInSteps = delayToWakeUpInSteps - __timing.getEntireCycleSize() + __timing.getAwakeCycleSize();
		if (delayToSleepInSteps > 0) {
			scheduleGoSleep(delayToSleepInSteps);
		}
	}

	private void sendLanPacket(WlanFramePacket goodnessPkt) {
		// applyDefaultBitrate(goodnessPkt);
		_outQueue.add(goodnessPkt);
		// sendPacket(goodnessPkt);
	}

	private void sendPacket(WlanFramePacket packet) {
		Simulation.Log.state("PKT TYPE", packet.getType().ordinal(), getNode());
		_lastSent = getNode().getCurrentTime();
		super.sendPacket(packet);
	}

	private void sendPacketDown(WlanFramePacket packet) {
		applyBitrate(packet, -1);
		sendPacket(packet);
	}

	/** Internal method to set a random backoff delay. */
	private void setBackoffTime() {
		_immediateSend = false;

		int i = getRandom().nextInt(CW[_currentCW]);

		_backoffTime = globalTimings.getSlotTime() * i;

		if (_currentCW < MAX_CW_IDX) {
			_currentCW++;
		}
	}

	private void setDistanceFromCenter(double distanceFromCenter) {
		_distanceFromCenter = distanceFromCenter;
		Simulation.Log.state("Distance from center", (int) _distanceFromCenter, getNode());

		int backboneRadius = 100;
		int variation = 15;

		if (_distanceFromCenter > backboneRadius - variation && _distanceFromCenter < backboneRadius + variation) {
			Simulation.Log.state("Backbone Range", 4, getNode());
		} else {
			Simulation.Log.state("Backbone Range", 7, getNode());
		}

	}

	/**
	 * Currently only supports the change of the Bitrate via the default policy.
	 * 
	 * @param state
	 *            everything is ignored except promiscuous.
	 * @return false always.
	 */
	@Override
	public boolean setState(LayerState state) {
		// TODO the other attribute of state should be used
		_promiscuous = ((MACState) state).getPromiscuous();
		return false;
	}

	@Override
	public void startCarrierSense(double minFreeTime, double varFreeTime) {
		LOGGER.debug("Start carrier sensing at " + SimulationManager.getInstance().getCurrentTime());

		setNodeState(NodeState.CARRIERSENSING);
		super.startCarrierSense(0, varFreeTime);
	}

	private boolean hasThisNodeMultipleSchedules() {
		return _schedules != null && _schedules.size() > 1;
	}

	private boolean isThisNodeNotGoodEnough() {
		return RANDOM.nextDouble() < GOODNESS_FACTOR;
	}

	public double time(double seconds) {
		return Configuration.getInstance().getSimulationSteps(seconds);
	}

	/**
	 * If a packet was rejected by the Air, this method tries to avoid the loss of this packet.
	 * 
	 * @param rpe
	 *            the rejected packet event.
	 * @param frame
	 *            the dropped frame.
	 */
	protected void tryHandleDroppedPacket(RejectedPacketEvent rpe, WlanFramePacket frame) {
		// here the packet is just dropped. override this, to change the
		// behavior (see DBG variant).
	}

	/**
	 * internal method, to process the outQueue, fetch the next packet and send it.
	 * 
	 * @return true if a carrier sense was started.
	 */
	protected final boolean trySend(int posIdx) {
		_lastTrySendPos = posIdx;
		/*
		 * There is no periodic event issued (polling), where it is checked, if a packet is in the out queue and can be sent. It has to be ensured,
		 * that for every to be sent packet, at least once trySend() is called and is leading to a carrier sense, which uppon completion will actually
		 * send a packet or issue another trySend() with a changed backoff time. Thus trySend() is to be called after entering a new packet to the out
		 * queue, after the reception of a packet (which may have blocked sending another packet) and of course after the reception of a packet. If
		 * this is not done immediately, it has to be ensured, that it will definitely done later. Otherwhise, the out queue will contain some to be
		 * send packets at the end of the simulation.
		 */

		boolean ok = ((_currentOutPacket == null) && (_outQueue.size() > 0))
				|| ((_currentOutPacket != null) && _currentOutPacket.isReadyForTransmission());

		if (ok && !_willSendACK && !_pendingCS && !_pendingACK) {
			AirState airState = (AirState) getNode().getLayerState(LayerType.AIR);
			PhysicalLayerState phyState = (PhysicalLayerState) getNode().getLayerState(LayerType.PHYSICAL);
			RadioState radioState = phyState.getRadioState();
			boolean radioFree = radioState == RadioState.LISTENING;

			if (radioFree) {
				_pendingCS = true;

				double difs = globalTimings.getDifs();

				if (_immediateSend) {
					// as.getLastInterference().getEnd(); this would include own
					// transmissions too
					if ((airState == null) || (airState.getInterQueue().getMaxTime() + difs > getNode().getCurrentTime())) {
						calcBackoffTime(0.0);
					}
				}

				if (_immediateSend) {
					startCarrierSense(difs, 0.0);
				} else {
					startCarrierSense(difs, _backoffTime);
				}
				return true;
			} else {
				if (airState != null) {
					Interval lastIncoming = airState.getLastIncoming();

					if (lastIncoming != null) {
						double end = lastIncoming.getEnd();
						double now = node.getCurrentTime();

						if (end > now) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("initiate a delayed trySend()");
							}
							sendEventSelf(new MACEvent(sender, end - now));
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * @see br.ufla.dcc.grubix.simulator.node.Layer#upperSAP(br.ufla.dcc.grubix.simulator.event.Packet)
	 * @param packet
	 *            to process coming from higher layer.
	 */
	@Override
	public final void upperSAP(Packet packet) {
		boolean ownPacket = packet.getReceiver() == id;
		if (ownPacket) {
			packet.flipDirection();
			sendPacket(packet);
			return;
		}

		LogLinkPacket lp = (LogLinkPacket) packet;

		WlanFramePacket nextPacket = new WlanFramePacket(sender, lp);
		applyBitrate(nextPacket, -1);

		_outQueue.addLast(nextPacket);

		/*
		 * If multiple packets were sent at the same time, the first one triggers trySend(), since this means, now > lastSent. On receiving of
		 * MACSendingTerminated, the next trySend() is issued, to further process the queue. Thus, for every to be send packet at least once
		 * trySend()is called.
		 */

		// double now = getNode().getCurrentTime();
		// if (now > _lastTrySent) {
		// _lastTrySent = now;
		// // +++++++++++++ trySend(2); // #2#
		//
		// if (!_pendingCS && !_willSendACK) {
		// // something is currently received, thus backoff is needed.
		// calcBackoffTime(0.0);
		// }
		// }
	}

	private boolean verifyCenter(Position position) {
		if (isThereACenterNode()) {
			return false;
		}

		double nodeX = position.getXCoord();
		double nodeY = position.getYCoord();

		double fieldSizeX = Configuration.getInstance().getXSize();
		double fieldSizeY = Configuration.getInstance().getYSize();

		double fieldVarianceX = fieldSizeX * .05;
		double fieldVarianceY = fieldSizeY * .05;

		double centerX = fieldSizeX / 2;
		double centerY = fieldSizeY / 2;

		if (centerX - fieldVarianceX < nodeX && nodeX < centerX + fieldVarianceX) {
			if (centerY - fieldVarianceY < nodeY && nodeY < centerY + fieldVarianceY) {
				return true;
			}
		}

		return false;
	}
}
