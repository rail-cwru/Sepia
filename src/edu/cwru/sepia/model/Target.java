package edu.cwru.sepia.model;

import java.io.Serializable;

import edu.cwru.sepia.util.DeepEquatable;

/**
 * An class that signifies that an extending class can be the direct object of an action
 * This requires that they all share an ID scheme
 * @author Tim
 *
 */
public abstract class Target implements Serializable, DeepEquatable {
	public static final long serialVersionUID = 310562678386330058l;
	public final int ID;
	public Target(int ID)
	{
		this.ID = ID;
	}
}
