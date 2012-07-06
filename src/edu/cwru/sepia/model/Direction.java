package edu.cwru.sepia.model;
/**
 * A basic enumeration of the eight directions in which a primitive action can be taken.
 * Directions are treated the same as screen coordinates: East is positive x, South is positive y.
 * @author Tim
 *
 */
public enum Direction {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
	
	/**
	 * @return x component of the direction: -1, 0, 1
	 */
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
	
	/**
	 * @return y component of the direction: -1, 0, 1
	 */
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
	/**
	 * Get a direction for an x and y direction.
	 * @param x
	 * @param y
	 * @return
	 */
	public static Direction getDirection(int x, int y)
	{
		//If the design stabilizes more or this proves slow, then replace with a switch or an index of this.values[]
		if (x > 0) //EAST
		{
			if (y > 0) //SOUTH
			{
				return SOUTHEAST;
			}
			else if (y<0) //NORTH
			{
				return NORTHEAST;
			}
			else //y==0
			{
				return EAST;
			}
		}
		else if (x<0) //WEST
		{
			if (y > 0) //SOUTH
			{
				return SOUTHWEST;
			}
			else if (y<0) //NORTH
			{
				return NORTHWEST;
			}
			else //y==0
			{
				return WEST;
			}
		}
		else //x==0
		{
			if (y > 0) //SOUTH
			{
				return SOUTH;
			}
			else if (y<0) //NORTH
			{
				return NORTH;
			}
			else //y==0
			{
				throw new IllegalArgumentException("No direction for going nowhere");
			}
		}
			
	}
}
