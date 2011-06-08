package edu.cwru.SimpleRTS.model;
/**
 * Signifies that an implementing class provides generic details about a specific object.
 * Also provides a means for creating factory methods for specific kinds of game objects.
 * @author Tim
 *
 * @param <T>
 */
public interface Template<T> {
	/**
	 * A factory method that produces copies of a "default" object
	 * @return
	 */
	public T produceInstance();
}
