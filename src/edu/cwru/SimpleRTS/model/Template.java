package edu.cwru.SimpleRTS.model;

import edu.cwru.SimpleRTS.model.unit.Unit;

/**
 * Signifies that an implementing class provides generic details about a specific object.
 * Also provides a means for creating factory methods for specific kinds of game objects.
 * @author Tim
 *
 * @param <T>
 */
public abstract class Template<T> {
	/**
	 * A factory method that produces copies of a "default" object
	 * @return
	 */
	public abstract T produceInstance();
	static int nextID=0;
	public Template()
	{
		ID = nextID++;
	}
	protected int ID;
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Template))
			return false;
		return ((Template)o).ID == ID;
	}
}
