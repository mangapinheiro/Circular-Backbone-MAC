package br.ufla.dcc.nodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import br.ufla.dcc.event.wuc.BackBoneAgentsDisseminateWUC;
import br.ufla.dcc.event.wuc.BackBoneCreateWUC;
import br.ufla.dcc.event.wuc.CascadingWUC;
import br.ufla.dcc.event.wuc.ProcessCascadeWUC;
import br.ufla.dcc.event.wuc.RemoveActiveAgentWUC;
import br.ufla.dcc.event.wuc.RemoveGoodnessListWUC;
import br.ufla.dcc.event.wuc.SendDelayedWUC;
import br.ufla.dcc.grubix.simulator.LayerException;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.Position;
import br.ufla.dcc.grubix.simulator.event.ApplicationPacket;
import br.ufla.dcc.grubix.simulator.event.Finalize;
import br.ufla.dcc.grubix.simulator.event.Packet;
import br.ufla.dcc.grubix.simulator.event.StartSimulation;
import br.ufla.dcc.grubix.simulator.event.TrafficGeneration;
import br.ufla.dcc.grubix.simulator.event.WakeUpCall;
import br.ufla.dcc.grubix.simulator.kernel.Configuration;
import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.grubix.simulator.node.ApplicationLayer;
import br.ufla.dcc.mac.packet.BbBuilderAgentPacket;
import br.ufla.dcc.mac.packet.BbCandidatesGoodnessRequestPacket;
import br.ufla.dcc.mac.packet.CascadePacket;
import br.ufla.dcc.mac.packet.GoodnessPacket;
import br.ufla.dcc.mac.packet.RefuseAgentPacket;
import br.ufla.dcc.packet.AgentPacket;
import br.ufla.dcc.utils.BackboneNodeState;
import br.ufla.dcc.utils.BbSyncDirection;
import br.ufla.dcc.utils.NeighborGoodness;

public class RegularNode extends ApplicationLayer {

	private static boolean thereIsARoot = false;
	public static Double maxReachingDistance = 0d;
	public static NodeId sinkNodeId;

	private final List<Integer> activeAgents = new ArrayList<Integer>();

	/**
	 * KEY - the AgentPacket unique ID (found in 'AgentPacket.getIdentity()' method)
	 */
	private final Map<Integer, SortedSet<NeighborGoodness>> NeighborGoodness = new HashMap<Integer, SortedSet<NeighborGoodness>>();

	private Integer cascadeOrder = Integer.MAX_VALUE;
	private boolean onCascade = false;
	private boolean onBackbone = false;
	private final boolean knowOtherCandidate = false;

	private final boolean isFirsElectionPacket = true;
	private Double goodness = Math.random();

	private NodeId parent;
	private NodeId child;

	public RegularNode() {
		this.goodness = Math.random() * .2d + .8d;
	}

	private NodeId getBestBackBoneCandidate(AgentPacket agent) {
		if (this.NeighborGoodness.get(agent.getIdentity()).isEmpty()) {
			return null;
		}

		return this.NeighborGoodness.get(agent.getIdentity()).last().getNodeId();
	}

	@Override
	public int getPacketTypeCount() {
		return 1;
	}

	public boolean isOnBackbone() {
		return onBackbone;
	}

	@Override
	public void lowerSAP(Packet packet) throws LayerException {

		this.processorInvoker("processLSAP", packet);

	}

	@Override
	protected void processEvent(Finalize finalize) {
	}

	@Override
	public void processEvent(StartSimulation start) {
		// if (this.node.getId().asInt() == 1) // If I'm THE ONE

		SimulationManager.logNodeState(this.getId(), "Goodness", "float", this.goodness.toString());

		if (this.verifyBorder(this.node.getPosition()) && !this.isThereARoot()) {
			this.cascadeOrder = 1;
			this.setRoot();
			SimulationManager.logNodeState(this.getId(), "SinkNode", "float", "0");

			sinkNodeId = this.getId();

			SimulationManager.logNodeState(this.getId(), "BackBone", "int", BackboneNodeState.IS_BACKBONE + "");

			// CascadingSyncWUC cswuc = new CascadingSyncWUC(sender, 100);
			// sendEventSelf(cswuc);

			WakeUpCall cskdBkb = new CascadingWUC(sender, 100);
			sendEventSelf(cskdBkb);

			BbBuilderAgentPacket builderAgentTO_SOUERCE = new BbBuilderAgentPacket(sender, NodeId.ALLNODES, BbSyncDirection.TO_SOURCE);
			BbBuilderAgentPacket builderAgentFROM_SOURCE = new BbBuilderAgentPacket(sender, NodeId.ALLNODES, BbSyncDirection.FROM_SOURCE);

			WakeUpCall wucBkb = new BackBoneCreateWUC(sender, 200, builderAgentTO_SOUERCE);
			sendEventSelf(wucBkb);

			wucBkb = new BackBoneCreateWUC(sender, 300, builderAgentFROM_SOURCE);
			sendEventSelf(wucBkb);
		}

	}

	private void setRoot() {
		thereIsARoot = true;
	}

	private boolean isThereARoot() {

		return thereIsARoot;
	}

	private boolean verifyBorder(Position position) {
		double x = position.getXCoord();
		double y = position.getYCoord();

		double fieldSizeX = Configuration.getInstance().getXSize();
		double fieldSizeY = Configuration.getInstance().getYSize();

		double fieldVarianceX = fieldSizeX * .2;
		double fieldVarianceY = fieldSizeY * .2;

		if ((fieldSizeX / 2) - fieldVarianceX < x && x < (fieldSizeX / 2) + fieldVarianceX) {
			if (y < 10) {
				return true;
			}

			if (y > Configuration.getInstance().getYSize() - 10) {
				return true;
			}
		}

		if ((fieldSizeY / 2) - fieldVarianceY < y && y < (fieldSizeY / 2) + fieldVarianceY) {
			if (x < 10) {
				return true;
			}

			if (x > Configuration.getInstance().getXSize() - 10) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void processEvent(TrafficGeneration tg) {
	}

	// @SuppressWarnings("unused")
	// private void
	// processLSAPBbCandidatesGoodnessRequestPacket(BbCandidatesGoodnessRequestPacket
	// gnessRqPkt) {
	// processorInvoker("processPKT", gnessRqPkt);
	// }

	// @SuppressWarnings("unused")
	// private void processLSAPCascadePacket(CascadePacket kskdPKT) {
	// if (kskdPKT.getSender() != sender) {
	// // Packet ack = new AckPacket(sender, kskdPKT.getSender().getId());
	// // sendPacket(packet);
	//
	// WakeUpCall wucPC = new ProcessCascadeWUC(sender, kskdPKT);
	// sendEventSelf(wucPC);
	// }
	// }
	//
	// @SuppressWarnings("unused")
	// private void processLSAPElectionPacket(ElectionPacket lekPKT) {
	// processorInvoker("processPKT", lekPKT);
	// }

	private Object processorInvoker(String prefix, Object obj) {
		Method declaredMethod = null;
		try {
			declaredMethod = this.getClass().getDeclaredMethod(prefix + obj.getClass().getSimpleName(), obj.getClass());

			if (declaredMethod != null) {
				return declaredMethod.invoke(this, obj);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			if (!prefix.equals("processPKT")) {
				this.processorInvoker("processPKT", obj);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void processPacket(ApplicationPacket packet) {
		this.processorInvoker("processPKT", packet);
	}

	@SuppressWarnings("unused")
	private void processPKTBbBuilderAgentPacket(BbBuilderAgentPacket builderAgent) {

		if (this.isOnBackbone()) {
			RefuseAgentPacket refusePkt = new RefuseAgentPacket(this.sender, builderAgent.getSender().getId(), builderAgent);
			this.sendEventSelf(new SendDelayedWUC(this.getSender(), Math.random() * 100, refusePkt));
			return; // If this node is already part of a Backbones it must
			// refuse to be part of another
		}

		this.setOnBackbone(true);
		SimulationManager.logNodeState(this.getId(), "BackBone", "int", builderAgent.getIdentity().toString());

		this.parent = builderAgent.getSender().getId();

		if (!builderAgent.canBeDisseminated()) {
			return;
		}

		BbCandidatesGoodnessRequestPacket grPkt = new BbCandidatesGoodnessRequestPacket(this.sender, NodeId.ALLNODES, builderAgent, this.getNode()
				.getPosition(), this.cascadeOrder);
		this.NeighborGoodness.put(builderAgent.getIdentity(), new TreeSet<NeighborGoodness>());
		this.sendPacket(grPkt);
		// WakeUpCall removeGoodnessListWuc = new
		// RemoveGoodnessListWUC(this.sender, 500, builderAgent);
		// this.sendEventSelf(removeGoodnessListWuc);

		WakeUpCall disseminateAgentWuc = new BackBoneAgentsDisseminateWUC(this.sender, 100, builderAgent);
		this.sendEventSelf(disseminateAgentWuc);
	}

	@SuppressWarnings("unused")
	private void processPKTRefuseAgentPacket(RefuseAgentPacket refusePkt) {

		AgentPacket agent = refusePkt.getAgent();
		SortedSet<NeighborGoodness> NeighborGoodness = this.NeighborGoodness.get(agent.getIdentity());

		NeighborGoodness.remove(NeighborGoodness.last());

		NodeId bestBackBoneCandidate = this.getBestBackBoneCandidate(agent);

		if (bestBackBoneCandidate != null) {
			agent.setSender(this.getSender());
			agent.setToForward(bestBackBoneCandidate);

			this.sendPacket(agent);
		} else {
			this.setOnBackbone(false);
			SimulationManager.logNodeState(this.getId(), "BackBone", "int", BackboneNodeState.ERROR + "");
			agent.removeHop();

			RefuseAgentPacket rPkt = new RefuseAgentPacket(this.sender, this.parent, agent);
			this.sendEventSelf(new SendDelayedWUC(this.getSender(), Math.random() * 100, rPkt));
		}
	}

	@SuppressWarnings("unused")
	private void processPKTBbCandidatesGoodnessRequestPacket(BbCandidatesGoodnessRequestPacket requestPacket) {

		if (this.isOnBackbone()) {
			return; // The node cannot participate of more than one Backbone
		}

		AgentPacket agent = requestPacket.getAgent();

		if (this.activeAgents.contains(agent.getIdentity())) {
			return;
		}

		this.activeAgents.add(agent.getIdentity());

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

		this.NeighborGoodness.get(packet.getAgent().getIdentity()).add(NeighborGoodness);

	}

	private Double getGoodnes(BbCandidatesGoodnessRequestPacket requestPacket) {
		Double distance = Position.getDistance(this.getNode().getPosition(), requestPacket.getSenderPosition());

		Double inherentGoodnessFactor = this.goodness - (this.activeAgents.size() * .1);
		Double distanceFactor = distance * 2.5;
		Double cascadingFactor = .5;

		if (this.cascadeOrder < Integer.MAX_VALUE) {
			cascadingFactor = 1d - ((this.cascadeOrder - requestPacket.getSenderOrder()) * .1);
		}

		return (inherentGoodnessFactor * .3) + (distanceFactor * .2) + (cascadingFactor * .5);
	}

	@Override
	public void processWakeUpCall(WakeUpCall wuc) {
		this.processorInvoker("processWUC", wuc);
	}

	public void processWUCBackBoneCreateWUC(BackBoneCreateWUC wuc) {

		BbBuilderAgentPacket builderAgent = wuc.getAgent();

		this.activeAgents.add(builderAgent.getIdentity());
		this.NeighborGoodness.put(builderAgent.getIdentity(), new TreeSet<NeighborGoodness>());

		BbCandidatesGoodnessRequestPacket grPkt = new BbCandidatesGoodnessRequestPacket(this.sender, NodeId.ALLNODES, builderAgent, this.getNode()
				.getPosition(), this.cascadeOrder);
		this.sendPacket(grPkt);
		// WakeUpCall removeGoodnessListWuc = new
		// RemoveGoodnessListWUC(this.sender, 500, builderAgent);
		// this.sendEventSelf(removeGoodnessListWuc);

		WakeUpCall disseminateAgentWuc = new BackBoneAgentsDisseminateWUC(this.sender, 100, builderAgent);
		this.sendEventSelf(disseminateAgentWuc);

	}

	public void processWUCBackBoneAgentsDisseminateWUC(BackBoneAgentsDisseminateWUC wuc) {
		AgentPacket agent = wuc.getAgent();
		agent.addHop();

		NodeId bestBackBoneCandidate = this.getBestBackBoneCandidate(agent);
		this.child = bestBackBoneCandidate;

		if (bestBackBoneCandidate != null) {
			agent.setSender(this.getSender());
			agent.setToForward(bestBackBoneCandidate);

			this.sendPacket(agent);
		} else {
			this.setOnBackbone(false);
			SimulationManager.logNodeState(this.getId(), "BackBone", "int", BackboneNodeState.ERROR + "");
			agent.removeHop();

			if (getSender().getId() == sinkNodeId) {
				System.out.println("=======================> IMPOSSIBLE TO CREATE BACKBONE, NO NODE IS ELIGIBLE!!!");
				return;
			}
			RefuseAgentPacket refusePkt = new RefuseAgentPacket(this.sender, this.parent, agent);
			this.sendEventSelf(new SendDelayedWUC(this.getSender(), Math.random() * 100, refusePkt));
		}

	}

	public void processWUCRemoveGoodnessListWUC(RemoveGoodnessListWUC wuc) {

		this.NeighborGoodness.remove(wuc.getAgent().getIdentity());

	}

	public void processWUCRemoveActiveAgentWUC(RemoveActiveAgentWUC wuc) {

		this.activeAgents.remove(wuc.getAgent().getIdentity());

	}

	public void processWUCSendDelayedWUC(SendDelayedWUC wuc) {
		sendPacket(wuc.pak);
	}

	public void setOnBackbone(boolean onBackbone) {
		this.onBackbone = onBackbone;
	}

	// CASCADING CODE

	public void processWUCProcessCascadeWUC(ProcessCascadeWUC wuc) {
		this.processorInvoker("processPKT", wuc.getPkt());
	}

	public void setOnCascade(boolean onCascade) {
		this.onCascade = onCascade;
	}

	public boolean isOnCascade() {
		return onCascade;
	}

	public void processWUCCascadingWUC(CascadingWUC wuc) {
		if (this.cascadeOrder < Integer.MAX_VALUE) {
			CascadePacket ap = new CascadePacket(this.getSender(), NodeId.ALLNODES, this.cascadeOrder);
			this.sendPacket(ap);
		}
	}

	@SuppressWarnings("unused")
	private void processPKTCascadePacket(CascadePacket pkt) {
		CascadePacket packet = pkt;
		if (this.cascadeOrder > packet.getHopCounter() + 1) {
			this.cascadeOrder = packet.getHopCounter() + 1;
			SimulationManager.logNodeState(this.getNode().getId(), "Cascade", "int", this.cascadeOrder.toString());
			CascadingWUC cswuc = new CascadingWUC(sender, Math.random() * 100);
			sendEventSelf(cswuc);

		}
	}
}
