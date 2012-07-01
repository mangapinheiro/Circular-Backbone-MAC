package br.ufla.dcc.mac.backbone.packet;

import br.ufla.dcc.grubix.simulator.Address;
import br.ufla.dcc.grubix.simulator.NodeId;
import br.ufla.dcc.grubix.simulator.event.user.WlanFramePacket;
import br.ufla.dcc.mac.backbone.Schedule;

public class SchedulePacket extends WlanFramePacket {
	private final Schedule schedule;

	public SchedulePacket(Address sender, NodeId receiver, Schedule schedule) {
		this(sender, receiver, schedule, PacketType.CONTROL, 6);
	}

	public SchedulePacket(Address sender, NodeId receiver, Schedule schedule, PacketType type, double signalStrength) {
		super(sender, receiver, type, signalStrength);
		this.schedule = schedule;
	}

	public Schedule getSchedule() {
		return schedule;
	}

}
