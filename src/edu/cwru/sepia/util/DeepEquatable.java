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
