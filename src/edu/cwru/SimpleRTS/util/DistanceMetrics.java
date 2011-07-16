package edu.cwru.SimpleRTS.util;

public final class DistanceMetrics {

	private DistanceMetrics(){}
	
	public static double euclideanDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}
	/**
	 * The core distance metric used by wargus for attacks and such
	 */
	public static int chebyshevDistance(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
	}
	
}
