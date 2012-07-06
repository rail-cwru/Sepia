package edu.cwru.sepia.util;
/**
 * An alternative to the equals method.  Needed because HashMaps are widely used and are optimized by only comparing the ID field, necessitating that hashCode() and thus equals() conform to that. <br/>
 * It is generic so that it can check at compile-time if the types are right. <br/>
 * This should be primarily used for debugging and testing purposes.
 * @author The Condor
 */
public interface DeepEquatable {
	
	public boolean deepEquals(Object other);
}
