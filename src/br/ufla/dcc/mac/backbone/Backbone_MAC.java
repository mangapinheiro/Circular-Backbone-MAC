package br.ufla.dcc.mac.backbone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.Interval;
import br.ufla.dcc.grubix.simulator.LayerException;
import br.ufla.dcc.grubix.simulator.LayerType;
import br.ufla.dcc.grubix.simulator.NodeId;
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
import br.ufla.dcc.grubix.simulator.node.RadioState;
import br.ufla.dcc.grubix.simulator.node.user.AARFRateAdaptation;
import br.ufla.dcc.grubix.simulator.node.user.CarrierSenseInterruptedEvent;
import br.ufla.dcc.grubix.simulator.node.user.IEEE_802_11_TimingParameters;
import br.ufla.dcc.grubix.simulator.node.user.MAC_IEEE802_11bg_DCF;
import br.ufla.dcc.grubix.xml.ConfigurationException;
import br.ufla.dcc.grubix.xml.ShoXParameter;
import br.ufla.dcc.mac.backbone.packet.SchedulePacket;
import br.ufla.dcc.mac.backbone.state.CarrierSensing;
import br.ufla.dcc.mac.backbone.state.Listening;
import br.ufla.dcc.mac.backbone.state.NodeState;
import br.ufla.dcc.mac.backbone.util.BbMacTiming;
import br.ufla.dcc.mac.backbone.wakeupcall.BroadcastScheduleDelayed;
import br.ufla.dcc.mac.backbone.wakeupcall.CreateScheduleWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.GoSleepWUC;
import br.ufla.dcc.mac.backbone.wakeupcall.WakeUpWUC;
import br.ufla.dcc.utils.Simulation;

public class Backbone_MAC extends MACLayer {

	private static final String NODE_STATE = "NodeState";

	private static final double GOODNESS_FACTOR = .7;

	private final BbMacTiming __timing = new BbMacTiming();

	/** Logger of this class. */
	protected static final Logger LOGGER = Logger.getLogger(Backbone_MAC.class.getName());

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

	private NodeState __NodeState;

	private final List<Schedule> __schedules;

	public Backbone_MAC() {
		/** constructor. */
		_outQueue = new LinkedList<WlanFramePacket>();
		_droppedPackets = 0;
		_promiscuous = false;

		__NodeState = new Listening();
		__schedules = new ArrayList<Schedule>();
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

	private void broadcastSchedule(Schedule schedule, double delay) {
		if (thisNodeHasMultipleSchedules()) {
			// don't propagate
			return;
		}

		SchedulePacket schedulePacket = new SchedulePacket(getSender(), NodeId.ALLNODES, schedule);

		if (delay <= 0) {
			sendPacketDown(schedulePacket);
			return;
		}

		SendDelayedWakeUp sendDelayedWakeUp = new BroadcastScheduleDelayed(getSender(), delay, schedulePacket);
		sendEventSelf(sendDelayedWakeUp);
	}

	private boolean thisNodeHasMultipleSchedules() {
		return __schedules != null && __schedules.size() > 1;
	}

	private void sendPacketDown(WlanFramePacket packet) {
		applyBitrate(packet, -1);
		sendPacket(packet);
	}

	/**
	 * internal method to modify/set the backoff delay.
	 * 
	 * @param csStart
	 *            time from which the carrier sensing started.
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
		// TODO CONFIGURE THE NODE TO FOLOW A SCHEDULE (create WakeUp and GoSleep wake up calls)
		if (!hasSchedule()) {
			Simulation.Log.state("Schedule", schedule.getId(), getNode());
		} else {
			Simulation.Log.state("Margin", schedule.getId(), getNode());
		}
		__schedules.add(schedule);
		this.scheduleWakeUp(schedule.getDelay(SimulationManager.getInstance().getCurrentTime()));
	}

	private String getHandlerMethodName(WakeUpCall wuc) {
		return "process" + wuc.getClass().getSimpleName();
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

	private boolean hasSchedule() {
		return __schedules.size() > 0;
	}

	private boolean isFollowingSchedule(Schedule schedule) {
		return __schedules.contains(schedule);
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

		WakeUpCall createSchedule = new CreateScheduleWUC(getSender(), new Random().nextDouble() * __timing.getSleepCycleSize());
		sendEventSelf(createSchedule);

		// scheduleWakeUp(SLEEP_CYCLE_SIZE);
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
		boolean doDropPacket = !packetIsForThisNode(packet) && !packetIsForAllNodes(packet);
		NodeId senderId = frame.getSender().getId();

		if (doDropPacket && _promiscuous && !packet.isTerminal() && !senderId.equals(id)) {
			doDropPacket = false;
		}

		if (doDropPacket) {
			if (LOGGER.isDebugEnabled()) { // check if enabled to reduce
											// performance impact
				LOGGER.debug("Packet not for this node. Throwing away " + packet);
			}
			// advance queue after receiving neither a broadcast nor a packet
			// for this node.
			// +++++++++++++ trySend(0); // #0#
		} else {
			if (packetIsForThisNode(packet) && packet.isTerminal() && (frame.isControl())) {
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

				sendPacket(packet.getEnclosedPacket());

				if (packetIsForThisNode(packet)) {
					_willSendACK = true;

					ack = new WlanFramePacket(sender, packet.getSender().getId(), PacketType.ACK, frame.getSignalStrength());

					applyBitrate(ack, frame.getBitrateIdx());

					WakeUpCall wuc = new MACSendACK(sender, globalTimings.getSifs(), ack);
					sendEventSelf(wuc);

					// no trySend() here, since any send prior the send of the
					// ack makes no sense.
				} else {
					if (_backoffAfterReceivedBroadcast && packetIsForAllNodes(packet)) {
						setBackoffTime();
					}

					// advance queue after receiving a broadcast.
					// +++++++++++++ trySend(1); // #1#
				}
			}
		}
	}

	/**
	 * @param packet
	 * @return true - if the packet is a broadcast
	 */
	private boolean packetIsForAllNodes(Packet packet) {
		return packet.getReceiver() == NodeId.ALLNODES;
	}

	/**
	 * @param packet
	 * @return true - if this node is the only receiver for the packet
	 */
	private boolean packetIsForThisNode(Packet packet) {
		return packet.getReceiver().equals(id);
	}

	@SuppressWarnings("unused")
	private void process(CrossLayerEvent cle) {
		cle.forwardUp(this);
	}

	/**
	 * internal method to handle MACCarrierSensing.
	 * 
	 * @param macCS
	 *            the to be handled event.
	 */
	@SuppressWarnings({ "unused" })
	private void process(MACCarrierSensing macCS) {

		_pendingCS = false;

		/*
		 * like in lowerSAP, any change to this method requires, that trySend() will be called, after sending a packet, or after the decision not to
		 * do so.
		 */

		if (macCS.isNoCarrier()) {
			if ((_currentOutPacket == null) && (_outQueue.size() > 0)) {
				_currentOutPacket = _outQueue.removeFirst();
			}

			if (_currentOutPacket == null) {
				LOGGER.warn("carrier sensing returned but no out packet present !");
			} else {
				_currentOutPacket.getRaPolicy().reset(getNode().getCurrentTime());

				// immediateSend = true;

				PacketType pType = _currentOutPacket.getType();
				boolean toAllNodes = _currentOutPacket.getReceiver() == NodeId.ALLNODES;

				_pendingACK = !toAllNodes && ((pType == PacketType.DATA) || (pType == PacketType.RTS));

				_currentOutPacket.setAckRequested(_pendingACK);
				_currentOutPacket.setReadyForTransmission(false);

				_lastSent = getNode().getCurrentTime();
				sendPacket(_currentOutPacket);

				if (_pendingACK) {
					// 2nd syncDuration already included in ackDelay !!!
					double delay = _currentOutPacket.getDuration() + _ackDelay[_currentOutPacket.getBitrateIdx()];

					WakeUpCall wakeUpCall = new MACProcessAckTimeout(sender, delay);
					sendEventSelf(wakeUpCall);
				} else {
					// since no ack expected, this is shoot and forget
					_currentOutPacket = null;
				}
				/*
				 * If the current packet is sent, a wakeup call will inform us and another trySend() will be issued.
				 */
			}
		} else {
			calcBackoffTime(macCS.getCsStart());
			// +++++++++++++ trySend(6); // #6#
		}
	}

	/**
	 * Method to process the generic MACEvent. This is only used for a delayed trySend().
	 * 
	 * @param me
	 *            the to be processed wakeup call.
	 */
	@SuppressWarnings("unused")
	private void process(MACEvent wuc) {
		boolean res = false; // +++++++++++++ this.trySend(5); // #5#

		if (res) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("successfully initiate the delayed trySend()");
			}
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
				_currentOutPacket.reset(); // prepare the packet for resending ##
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
	private void process(SchedulePacket packet) {
		if (!hasSchedule()) {
			followSchedule(packet.getSchedule());
			broadcastSchedule(packet.getSchedule(), __timing.getContentionTime());
		}

		if (isFollowingSchedule(packet.getSchedule())) {
			return;
		}

		followSchedule(packet.getSchedule());
	}

	private void broadcastSchedule(Schedule schedule) {
		broadcastSchedule(schedule, 0);
	}

	@SuppressWarnings("unused")
	private void process(SendingTerminated wuc) {
		_willSendACK = false;
		trySend(4); // #4#
	}

	@SuppressWarnings("unused")
	private void process(StatisticLogRequest logRequest) {
		logStatistic(logRequest);
		sendEventSelf(logRequest);
	}

	@SuppressWarnings("unused")
	private void process(CreateScheduleWUC event) {
		if (thisNodeIsNotGoodEnough()) {
			return; // don't create schedule
		}

		if (hasSchedule()) {
			return;
		}

		Schedule schedule = new Schedule(SimulationManager.getInstance().getCurrentTime(), __timing.getSleepCycleSize());

		followSchedule(schedule);

		broadcastSchedule(schedule);
	}

	private boolean thisNodeIsNotGoodEnough() {
		return new Random().nextDouble() < GOODNESS_FACTOR;
	}

	@SuppressWarnings("unused")
	private void process(GoSleepWUC wakeUp) {
		Simulation.Log.state(NODE_STATE, NodeState.SLEEPING, getNode());

		// TODO - CHANGE THE STATE E CONFIGURE NODE
	}

	@SuppressWarnings("unused")
	private void process(WakeUpWUC wakeUp) {
		Simulation.Log.state(NODE_STATE, NodeState.AWAKEN, getNode());
		scheduleGoSleep(__timing.getAwakeCycleSize());
		scheduleWakeUp(__timing.getSleepCycleSize());
		trySend(0);
	}

	private void startCarrierSensing() {
		__NodeState = new CarrierSensing();
		sendEventSelf(new MACCarrierSensing(getSender(), __timing.getCarrierSensingSize()));
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
	 * Method to start this layer.
	 * 
	 * @param start
	 *            StartSimulation event to start the layer.
	 */
	@Override
	protected void processEvent(StartSimulation start) {
		Address thisAddress = new Address(id, LayerType.MAC);
		StatisticLogRequest slr = new StatisticLogRequest(thisAddress, getConfig().getSimulationSteps(1), DROPS_PER_SECOND);
		sendEventSelf(slr);
	}

	@SuppressWarnings("unused")
	private void process(SendDelayedWakeUp sendDelayed) {
		sendPacketDown((WlanFramePacket) sendDelayed.getPkt());
	}

	@SuppressWarnings("unused")
	private void process(BroadcastScheduleDelayed scheduleDelayed) {
		if (thisNodeHasMultipleSchedules()) {
			// don't propagate
			return;
		}
		sendPacketDown((WlanFramePacket) scheduleDelayed.getPkt());
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

	private void scheduleGoSleep(double delayInSteps) {
		WakeUpCall goSleep = new GoSleepWUC(getSender(), delayInSteps);
		sendEventSelf(goSleep);
	}

	private void scheduleWakeUp(double delayInSteps) {
		WakeUpCall wakeUp = new WakeUpWUC(getSender(), delayInSteps);
		sendEventSelf(wakeUp);
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

		double now = getNode().getCurrentTime();
		if (now > _lastTrySent) {
			_lastTrySent = now;
			// +++++++++++++ trySend(2); // #2#

			if (!_pendingCS && !_willSendACK) {
				// something is currently received, thus backoff is needed.
				calcBackoffTime(0.0);
			}
		}
	}
}
