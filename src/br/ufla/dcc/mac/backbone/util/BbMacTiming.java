package br.ufla.dcc.mac.backbone.util;

import java.util.Random;

import br.ufla.dcc.grubix.simulator.kernel.Configuration;

public class BbMacTiming {
	private static final double AWAKE_CYCLE_SECONDS = 0.2;
	private static final double CARRIER_SENSING_SECONDS = 0.05;
	private static final double SLEEP_CYCLE_SECONDS = 2;
	private static final Random RANDOM = new Random();

	// /** contains to the simulationsteps converted value of SIFS. */
	// private final double sifs;
	// /** contains to the simulationsteps converted value of DIFS. */
	// private final double pifs;
	// /** contains to the simulationsteps converted value of PIFS. */
	// private final double difs;
	// /** contains to the simulationSteps converted value of SlotTime. */
	// private double slotTime;
	//
	// /** define the time of a long preamble. */
	// private final double longPreamble;
	// /** define the time of a short preamble @ 2 MBit. */
	// private final double shortPreamble;
	// /** define the time of a short preamble @ 54 MBit. */
	// private final double ofdmPreamble;
	// /** contains the precomputed duration of a wlan header PLCP-Preamble/header in simulation steps. */
	// private double syncDuration;
	// /** contains the to us converted value of the syncDuration. */
	// private int syncLength;

	private final double _carrierSersingSize;
	private final double _awakeCycleSize;
	private final double _sleepCycleSize;
	private final Configuration _configuration;

	/**
	 * default constructor to initialize the timing parameters.
	 * 
	 * Use isB==false, isLongPreamble==false for 802.11g only.
	 * 
	 * @param isB
	 *            is true, if 802.11b is requested.
	 * @param isLongPreamble
	 *            is true, if a long preamble is requested.
	 */
	// BbMacTiming(boolean isB) {
	public BbMacTiming() {
		_configuration = Configuration.getInstance();

		_carrierSersingSize = _configuration.getSimulationSteps(CARRIER_SENSING_SECONDS);
		_sleepCycleSize = _configuration.getSimulationSteps(SLEEP_CYCLE_SECONDS);
		_awakeCycleSize = _configuration.getSimulationSteps(AWAKE_CYCLE_SECONDS);

		// if (isB) {
		// slotTime = _configuration.getSimulationSteps(0.000020);
		// } else {
		// slotTime = _configuration.getSimulationSteps(0.000009);
		// }

		// sifs = _configuration.getSimulationSteps(0.000010);
		// pifs = sifs + slotTime;
		// difs = sifs + 2.0 * slotTime;
		//
		// int longPre = 192;
		// int shortPre = 96;
		// int ofdmPre = 26;
		//
		// longPreamble = _configuration.getSimulationSteps(0.000192);
		// shortPreamble = _configuration.getSimulationSteps(0.000096);
		// ofdmPreamble = _configuration.getSimulationSteps(0.000026); // 16 + 4 + 6

		// int count = 4;
		//
		// if (!isB) {
		// count = 8;
		// if (isLongPreamble) {
		// count = 12;
		// }
		// }
		//
		// bps = new double[count];
		// maxBitrateIDX = count - 1;
		//
		// double mbit = 1000000.0 / _configuration.getSimulationSteps(1.0);
		//
		// if (isB) {
		// bps[0] = mbit;
		// bps[1] = 2 * mbit;
		// bps[2] = 5.5 * mbit;
		// bps[3] = 11 * mbit;
		// this.maxBitrateIDX = 3;
		//
		// if (isLongPreamble) {
		// syncDuration = longPreamble;
		// syncLength = longPre;
		// } else {
		// syncDuration = shortPreamble;
		// syncLength = shortPre;
		// }
		// } else {
		// if (isLongPreamble) { // compatibility mode for b and g devices
		// bps[0] = mbit;
		// bps[1] = 2 * mbit;
		// bps[2] = 5.5 * mbit;
		// bps[3] = 6 * mbit;
		// bps[4] = 9 * mbit;
		// bps[5] = 11 * mbit;
		// bps[6] = 12 * mbit;
		// bps[7] = 18 * mbit;
		// bps[8] = 24 * mbit;
		// bps[9] = 36 * mbit;
		// bps[10] = 48 * mbit;
		// bps[11] = 54 * mbit;
		//
		// syncDuration = longPreamble;
		// syncLength = longPre;
		// this.maxBitrateIDX = 11;
		// } else {
		// bps[0] = 6 * mbit;
		// bps[1] = 9 * mbit;
		// bps[2] = 12 * mbit;
		// bps[3] = 18 * mbit;
		// bps[4] = 24 * mbit;
		// bps[5] = 36 * mbit;
		// bps[6] = 48 * mbit;
		// bps[7] = 54 * mbit;
		//
		// syncDuration = ofdmPreamble;
		// syncLength = ofdmPre;
		// this.maxBitrateIDX = 7;
		// }
		// }
	}

	public double getSleepCycleSize() {
		return _sleepCycleSize;
	}

	public double getAwakeCycleSize() {
		return _awakeCycleSize;
	}

	public double getCarrierSensingSize() {
		return _carrierSersingSize;
	}

	public double getContentionTime() {
		return RANDOM.nextDouble() * getAwakeCycleSize() / 2;
	}

	// /** @return the difs. */
	// public final double getDifs() {
	// return difs;
	// }
	//
	// /** @return the pifs. */
	// public final double getPifs() {
	// return pifs;
	// }
	//
	// /** @return the sifs. */
	// public final double getSifs() {
	// return sifs;
	// }
	//
	// /** @return the slotTime. */
	// public final double getSlotTime() {
	// return slotTime;
	// }
	//
	// /** @return the longPreamble. */
	// public final double getLongPreamble() {
	// return longPreamble;
	// }
	//
	// /** @return the ofdmPreamble. */
	// public final double getOfdmPreamble() {
	// return ofdmPreamble;
	// }
	//
	// /** @return the shortPreamble. */
	// public final double getShortPreamble() {
	// return shortPreamble;
	// }
	//
	// /** @return the syncDuration in simulation steps. */
	// public final double getSyncDuration() {
	// return syncDuration;
	// }
	//
	// /** @return the syncLength in us. */
	// public final int getSyncLength() {
	// return syncLength;
	// }
}
