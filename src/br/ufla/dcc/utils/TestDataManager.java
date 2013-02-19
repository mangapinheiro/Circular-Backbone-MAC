package br.ufla.dcc.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import br.ufla.dcc.grubix.simulator.kernel.SimulationManager;
import br.ufla.dcc.mac.test.packet.ThroughoutPacket;

public class TestDataManager {
	private static TestDataManager INSTANCE;

	private final Queue<String/* TODO Report data */> _reportData;

	private String _fileName = null;

	private TestDataManager() {
		try {
			_fileName = "testReport" + new Date().toString() + ".txt";
			FileWriter fstream = new FileWriter(_fileName);
			BufferedWriter out = new BufferedWriter(fstream);

			String columnHeaders = String.format("%s, %s, %s, %s", "Sender", "Receiver", "#hops", "Total Time");
			out.write(columnHeaders);
			out.close();
		} catch (Exception e) {

		}

		_reportData = new LinkedList<String>();

	}

	public static TestDataManager getInstance() {

		if (INSTANCE == null) {
			synchronized (TestDataManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new TestDataManager();
				}
			}
		}

		return INSTANCE;
	}

	public void saveReport() {
		try {
			// Create file
			FileWriter fstream = new FileWriter(_fileName, true);
			BufferedWriter out = new BufferedWriter(fstream);

			for (String reportData : _reportData) {
				out.newLine();
				out.append(reportData);
			}

			_reportData.clear();
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void appendDataFromPacket(ThroughoutPacket/* TODO ReportData */throughoutPacket) {

		// report data format "sender, receiver, #hops, totalTime"
		String reportDataFormatted = String.format("%d, %d, %d, %f", throughoutPacket.getSender().getId().asInt(), throughoutPacket
				.getDestinationNode().getId().asInt(), throughoutPacket.getNumberOfHops(), throughoutPacket.getTotalTime());

		_reportData.add(reportDataFormatted);

		if (_reportData.size() % 10 == 0) {
			saveReport();
		}

		if (_reportData.size() == 100) {
			saveReport();
			SimulationManager.getInstance().sendSimulationCompletedSignal();
		}
	}
}
