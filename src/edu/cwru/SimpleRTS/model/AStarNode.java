package edu.cwru.SimpleRTS.model;

import java.util.Arrays;

/**
 * Note: compareTo and equals are not equivalently implemented
 * equals checks xs and ys, but no other fields
 *
 */
public class AStarNode implements Comparable<AStarNode> {
	final int x;
	final int y;
	final int hashCode;
	final int g; //the distance from the start
	final int value; //the distance from the start + the heuristic value
	final Direction directionfromprevious;
	final AStarNode previous;
	public AStarNode(int x, int y, int g, int value, AStarNode previous, Direction directionfromprevious)
	{
		this.x =x;
		this.y=y;
		this.hashCode = Arrays.hashCode(new int[]{x,y});
		this.g = g;
		this.value = value;
		this.directionfromprevious = directionfromprevious;
		this.previous = previous;
	}
	/**
	 * Create a new root node
	 * @param distfromgoal
	 */
	public AStarNode(int x, int y, int distfromgoal)
	{
		this.x=x;
		this.y=y;
		this.hashCode = Arrays.hashCode(new int[]{x,y});
		g = 0;
		value = distfromgoal;
		directionfromprevious = null;
		previous=null;
	}
	public int compareTo(AStarNode other)
	{
		AStarNode o = (AStarNode)other;
		if (o.value == value)
		{
			if(directionfromprevious.toString().length() > o.directionfromprevious.toString().length())
				return 1;
			else if(directionfromprevious.toString().length() < o.directionfromprevious.toString().length())
				return -1;
			else
				return 0;
		}
		else if (value > o.value)
			return 1;
		else
			return -1;
	}
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof AStarNode))
			return false;
		else
		{
			AStarNode o = (AStarNode)other;
			return x==o.x && y==o.y;
		}
	}
	public int hashCode()
	{
		return hashCode;
	}
	@Override
	public String toString() {
		return "AStarNode [x=" + x + ", y=" + y + ", g=" + g + 
				", value=" + value + ", directionfromprevious=" + directionfromprevious + "]";
	}

}
