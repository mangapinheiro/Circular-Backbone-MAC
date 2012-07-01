/**
 * 
 */
package br.ufla.dcc.utils;

import br.ufla.dcc.grubix.simulator.NodeId;

/**
 * @author -Manga.-
 * 
 */
public class NeighborGoodness implements Comparable<NeighborGoodness> {

	private NodeId nodeId;
	private Double goodness;

	/**
     * 
     */
	public NeighborGoodness(NodeId nodeId, Double nGnesss) {
		this.setNodeId(nodeId);
		this.setGoodness(nGnesss);
	}

	public int compareTo(NeighborGoodness nGnesss) {
		if (this.goodness < nGnesss.getGoodness()) {
			return -1;
		}

		if (this.goodness > nGnesss.getGoodness()) {
			return 1;
		}

		return 0;
	}

	private void setNodeId(NodeId nodeId) {
		this.nodeId = nodeId;
	}

	public NodeId getNodeId() {
		return nodeId;
	}

	private void setGoodness(Double goodness) {
		this.goodness = goodness;
	}

	public Double getGoodness() {
		return goodness;
	}

}
