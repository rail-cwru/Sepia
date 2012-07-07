/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.environment.model;

import java.util.Arrays;

import edu.cwru.sepia.model.Direction;

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
	final int durativesteps;
	final AStarNode previous;
	/**
	 * Create a nonroot node with fields so for backtracking.
	 * @param x
	 * @param y
	 * @param g
	 * @param value
	 * @param previous
	 * @param directionfromprevious
	 */
	public AStarNode(int x, int y, int g, int value, AStarNode previous, Direction directionfromprevious)
	{
		this.x =x;
		this.y=y;
		this.hashCode = Arrays.hashCode(new int[]{x,y});
		this.g = g;
		this.value = value;
		this.directionfromprevious = directionfromprevious;
		this.previous = previous;
		this.durativesteps = 1;
	}
	/**
	 * Create a nonroot node with fields so for backtracking.
	 * @param x
	 * @param y
	 * @param g
	 * @param value
	 * @param previous
	 * @param directionfromprevious
	 * @param durativesteps
	 */
	public AStarNode(int x, int y, int g, int value, AStarNode previous, Direction directionfromprevious, int durativesteps)
	{
		this.x =x;
		this.y=y;
		this.hashCode = Arrays.hashCode(new int[]{x,y});
		this.g = g;
		this.value = value;
		this.directionfromprevious = directionfromprevious;
		this.previous = previous;
		this.durativesteps = durativesteps;
	}
	/**
	 * Create a new root node
	 * @param x
	 * @param y
	 * @param distfromgoal
	 */
	public AStarNode(int x, int y, int distfromgoal)
	{
		this.x=x;
		this.y=y;
		this.hashCode = Arrays.hashCode(new int[]{x,y});
		g = 0;
		value = distfromgoal;
		//there is no previous node, so no need to store backtracking related quantities;
		directionfromprevious = null;
		durativesteps = -1;
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
				", value=" + value + ", directionfromprevious=" + directionfromprevious +", duration=" + durativesteps +"]";
	}

}
