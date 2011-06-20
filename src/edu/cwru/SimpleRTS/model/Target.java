package edu.cwru.SimpleRTS.model;
/**
 * An class that signifies that an extending class can be the direct object of an action
 * This requires that they all share an ID scheme
 * @author Tim
 *
 */
public class Target {
	protected static int nextID = 0;
	protected int ID;
	public Target()
	{
		ID = nextID++;
	}
}
