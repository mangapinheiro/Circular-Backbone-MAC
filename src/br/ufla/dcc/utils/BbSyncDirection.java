package br.ufla.dcc.utils;

public enum BbSyncDirection {
	/**
	 * WakeUp time should increase "from" the source of the packet
	 */
	FROM_SOURCE,
	/**
	 * WakeUp time should increase "to" the source of the packet
	 */
	TO_SOURCE
}
