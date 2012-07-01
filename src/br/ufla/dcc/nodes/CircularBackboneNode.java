package br.ufla.dcc.nodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import br.ufla.dcc.event.wuc.BackBoneAgentsDisseminateWUC;
import br.ufla.dcc.event.wuc.BackBoneCreateWUC;
import br.ufla.dcc.event.wuc.RemoveGoodnessListWUC;
import br.ufla.dcc.event.wuc.SendDelayedWUC;
import br.ufla.dcc.grubix.simulator.LayerException;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.event.Finalize;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.StartSimulation;
import br.ufla.dcc.grubix.simulator.event.TrafficGeneration;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.grubix.simulator.kernel.Configuration;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.ApplicationLayer;
import br.ufla.dcc.grubix.simulator.node.Node;
import br.ufla.dcc.mac.packet.BbBuilderAgentPacket;
import br.ufla.dcc.mac.packet.BbCandidatesGoodnessRequestPacket;
import br.ufla.dcc.mac.packet.GoodnessPacket;
import br.ufla.dcc.mac.packet.RefuseAgentPacket;
import br.ufla.dcc.packet.AgentPacket;
import br.ufla.dcc.utils.BackboneNodeState;
import br.ufla.dcc.utils.BbSyncDirection;
import br.ufla.dcc.utils.NeighborGoodness;
import br.ufla.dcc.utils.Simulation;

public class CircularBackboneNode extends ApplicationLayer {

	private static Node __centerNode;

	/**
	 * KEY - the AgentPacket unique ID (found in 'AgentPacket.getIdentity()' method)
	 */
	private final Map<Integer, SortedSet<NeighborGoodness>> _neighborGoodness = new HashMap<Integer, SortedSet<NeighborGoodness>>();

	private boolean _onBackbone = false;
	private final Double _goodness;

	private NodeId _parent;
	private NodeId _child;

	private int _distanceFromCenter;

	public CircularBackboneNode() {
		_goodness = Math.random() * .2d + .8d;
	}

	private NodeId getBestBackBoneCandidate(AgentPacket agent) {
		if (_neighborGoodness.get(agent.getIdentity()).isEmpty()) {
			return null;
		}

		return _neighborGoodness.get(agent.getIdentity()).last().getNodeId();
	}

	@Override
	public int getPacketTypeCount() {
		return 1;
	}

	public boolean isOnBackbone() {
		return _onBackbone;
	}

	@Override
	public void lowerSAP(Packet packet) throws LayerException {

		this.processorInvoker("process", packet);

	}

	@Override
	protected void processEvent(Finalize finalize) {
	}

	@Override
	public void processEvent(StartSimulation start) {
		// if (this.node.getId().asInt() == 1) // If I'm THE ONE

		Simulation.Log.state("Goodness", _goodness, getNode());

		if (verifyCenter(this.node.getPosition()) && !isThereACenterNode()) {
			__centerNode = getNode();
			Simulation.Log.state("CenterNode", 1, __centerNode);
			Simulation.Log.state("BackBone", BackboneNodeState.IS_BACKBONE, getNode());

			BbBuilderAgentPacket builderAgentTO_SOURCE = new BbBuilderAgentPacket(sender, NodeId.ALLNODES, BbSyncDirection.TO_SOURCE);

			WakeUpCall wucBkb = new BackBoneCreateWUC(sender, 200, builderAgentTO_SOURCE);
			sendEventSelf(wucBkb);
		}
	}

	private boolean isThereACenterNode() {
		return __centerNode != null;
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

	@Override
	public void processEvent(TrafficGeneration tg) {
	}

	private Object processorInvoker(String prefix, Object obj) {
		Method declaredMethod = null;
		try {
			declaredMethod = this.getClass().getDeclaredMethod(prefix, obj.getClass());

			if (declaredMethod != null) {
				return declaredMethod.invoke(this, obj);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unused")
	private void process(BbBuilderAgentPacket builderAgent) {

		if (this.isOnBackbone()) {
			RefuseAgentPacket refusePkt = new RefuseAgentPacket(this.sender, builderAgent.getSender().getId(), builderAgent);
			this.sendEventSelf(new SendDelayedWUC(this.getSender(), Math.random() * 100, refusePkt));
			return; // If this node is already part of a Backbones it must
			// refuse to be part of another
		}

		this.setOnBackbone(true);
		SimulationManager.logNodeState(this.getId(), "BackBone", "int", builderAgent.getIdentity().toString());

		_parent = builderAgent.getSender().getId();

		if (!builderAgent.canBeDisseminated()) {
			return;
		}

		BbCandidatesGoodnessRequestPacket grPkt = new BbCandidatesGoodnessRequestPacket(this.sender, NodeId.ALLNODES, builderAgent, this.getNode()
				.getPosition(), _distanceFromCenter);
		_neighborGoodness.put(builderAgent.getIdentity(), new TreeSet<NeighborGoodness>());
		this.sendPacket(grPkt);
		// WakeUpCall removeGoodnessListWuc = new
		// RemoveGoodnessListWUC(this.sender, 500, builderAgent);
		// this.sendEventSelf(removeGoodnessListWuc);

		WakeUpCall disseminateAgentWuc = new BackBoneAgentsDisseminateWUC(this.sender, 100, builderAgent);
		this.sendEventSelf(disseminateAgentWuc);
	}

	@SuppressWarnings("unused")
	private void process(RefuseAgentPacket refusePkt) {

		AgentPacket agent = refusePkt.getAgent();
		SortedSet<NeighborGoodness> NeighborGoodness = _neighborGoodness.get(agent.getIdentity());

		NeighborGoodness.remove(NeighborGoodness.last());

		NodeId bestBackBoneCandidate = this.getBestBackBoneCandidate(agent);

		if (bestBackBoneCandidate != null) {
			agent.setSender(this.getSender());
			agent.setToForward(bestBackBoneCandidate);

			this.sendPacket(agent);
		} else {
			this.setOnBackbone(false);
			Simulation.Log.state("BackBone", BackboneNodeState.ERROR, getNode());
			agent.removeHop();

			RefuseAgentPacket rPkt = new RefuseAgentPacket(this.sender, _parent, agent);
			this.sendEventSelf(new SendDelayedWUC(this.getSender(), Math.random() * 100, rPkt));
		}
	}

	@SuppressWarnings("unused")
	private void process(BbCandidatesGoodnessRequestPacket requestPacket) {

		if (this.isOnBackbone()) {
			return; // The node cannot participate of more than one Backbone
		}

		AgentPacket agent = requestPacket.getAgent();

		SimulationManager.logNodeState(this.getId(), "Visited", "int", agent.getIdentity().toString());

		GoodnessPacket gnessPkt = new GoodnessPacket(this.sender, requestPacket.getSender().getId(), requestPacket.getAgent(),
				this.getGoodnes(requestPacket));
		this.sendEventSelf(new SendDelayedWUC(sender, Math.random() * 110, gnessPkt));

		// WakeUpCall removeActiveAgent = new RemoveActiveAgentWUC(this.sender,
		// 500, agent);
		// this.sendEventSelf(removeActiveAgent);

	}

	@SuppressWarnings("unused")
	private void processPKTGoodnessPacket(GoodnessPacket packet) {

		NeighborGoodness NeighborGoodness = new NeighborGoodness(packet.getSender().getId(), packet.getNodeGoodness());

		_neighborGoodness.get(packet.getAgent().getIdentity()).add(NeighborGoodness);

	}

	private Double getGoodnes(BbCandidatesGoodnessRequestPacket requestPacket) {
		Double distance = Position.getDistance(this.getNode().getPosition(), requestPacket.getSenderPosition());

		Double inherentGoodnessFactor = _goodness;
		Double distanceFactor = distance * 2.5;
		Double cascadingFactor = .5;

		return (inherentGoodnessFactor * .3) + (distanceFactor * .2) + (cascadingFactor * .5);
	}

	@Override
	public void processWakeUpCall(WakeUpCall wuc) {
		this.processorInvoker("processWUC", wuc);
	}

	public void process(BackBoneCreateWUC wuc) {

		BbBuilderAgentPacket builderAgent = wuc.getAgent();

		_neighborGoodness.put(builderAgent.getIdentity(), new TreeSet<NeighborGoodness>());

		BbCandidatesGoodnessRequestPacket grPkt = new BbCandidatesGoodnessRequestPacket(this.sender, NodeId.ALLNODES, builderAgent, this.getNode()
				.getPosition(), 0);
		this.sendPacket(grPkt);
		// WakeUpCall removeGoodnessListWuc = new
		// RemoveGoodnessListWUC(this.sender, 500, builderAgent);
		// this.sendEventSelf(removeGoodnessListWuc);

		WakeUpCall disseminateAgentWuc = new BackBoneAgentsDisseminateWUC(this.sender, 100, builderAgent);
		this.sendEventSelf(disseminateAgentWuc);

	}

	public void process(BackBoneAgentsDisseminateWUC wuc) {
		AgentPacket agent = wuc.getAgent();
		agent.addHop();

		NodeId bestBackBoneCandidate = this.getBestBackBoneCandidate(agent);
		_child = bestBackBoneCandidate;

		if (bestBackBoneCandidate != null) {
			agent.setSender(this.getSender());
			agent.setToForward(bestBackBoneCandidate);

			this.sendPacket(agent);
		} else {
			this.setOnBackbone(false);
			Simulation.Log.state("BackBone", BackboneNodeState.ERROR, getNode());
			agent.removeHop();

			// if (getSender().getId() == sinkNodeId) {
			// System.out.println("=======================> IMPOSSIBLE TO CREATE BACKBONE, NO NODE IS ELIGIBLE!!!");
			// return;
			// }
			RefuseAgentPacket refusePkt = new RefuseAgentPacket(this.sender, _parent, agent);
			this.sendEventSelf(new SendDelayedWUC(this.getSender(), Math.random() * 100, refusePkt));
		}

	}

	public void process(RemoveGoodnessListWUC wuc) {

		_neighborGoodness.remove(wuc.getAgent().getIdentity());

	}

	public void process(SendDelayedWUC wuc) {
		sendPacket(wuc.pak);
	}

	public void setOnBackbone(boolean onBackbone) {
		_onBackbone = onBackbone;
	}
}
