package edu.cwru.SimpleRTS.model;

import java.io.Serializable;

/**
 * An class that signifies that an extending class can be the direct object of an action
 * This requires that they all share an ID scheme
 * @author Tim
 *
 */
public class Target implements Serializable {
	
	protected static int nextID = 0;
	public final int ID;
	public Target()
	{
		ID = nextID++;
	}
}
