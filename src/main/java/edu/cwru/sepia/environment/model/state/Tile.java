/**
 * 	Strategy Engine for Programming Intelligent Agents (SEPIA)
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
package edu.cwru.sepia.environment.model.state;

import edu.cwru.sepia.util.DeepEquatable;

/**
 * An individual tile in the world.  It is associated with the terrain in that position
 *
 */
public class Tile {
	public enum TerrainType implements DeepEquatable {
		//TODO: use the map file to generate this, instead of an enum
		/**
		 * Basic walkable area
		 */
		LAND,
		/**
		 * Water.  It's wet.  Get a boat.
		 */
		WATER,
		/**
		 * Watery land.  Or perhaps landy water.
		 */
		SHALLOWS,
		/**
		 * A cliff, should be impassible by most properly configured units
		 */
		CLIFF, 
		/**
		 * An extra terrain, in case it is needed.
		 */
		STUFF;

		/* (non-Javadoc)
		 * @see edu.cwru.sepia.util.DeepEquatable#deepEquals(java.lang.Object)
		 */
		@Override
		public boolean deepEquals(Object other) {
			return other != null && other.getClass() == this.getClass() && other == this;
		}
	}
	
	//Note, the World's clone may assume that this is immutable, check that before altering
	final TerrainType terrainType;
	/**
	 * Create a tile
	 * @param terrainType
	 */
	public Tile(TerrainType terrainType) {
		this.terrainType = terrainType;
	}
}
