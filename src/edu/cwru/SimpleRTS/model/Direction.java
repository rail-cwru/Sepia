package edu.cwru.SimpleRTS.model;
/**
 * A basic enumeration of the eight directions in which a primitive action can be taken.
 * Directions are treated the same as screen coordinates: East is positive x, South is positive y.
 * @author Tim
 *
 */
public enum Direction {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
	
	public int xComponent() {
		switch(this)
		{
			case NORTHEAST:
			case EAST:
			case SOUTHEAST:
				return 1;
			case NORTHWEST:
			case WEST:
			case SOUTHWEST:
				return -1;
			default:
				return 0;
		}
	}
	public int yComponent() {
		switch(this)
		{
			case NORTHWEST:
			case NORTH:
			case NORTHEAST:
				return -1;
			case SOUTHWEST:
			case SOUTH:
			case SOUTHEAST:
				return 1;
			default:
				return 0;
		}
	}
}
