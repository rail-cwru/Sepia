package edu.cwru.SimpleRTS.model;

import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.IDDistributer;

/**
 * An class that signifies that an extending class can be the direct object of an action
 * This requires that they all share an ID scheme
 * @author Tim
 *
 */
public class Target implements Serializable {
	public final long serialVersionUID = 3105626783863300558l;
	public final int ID;
	public Target(int ID)
	{
		this.ID = ID;
	}
}
