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

import edu.cwru.sepia.environment.model.state.Tile.TerrainType;

/**
 * The interface for editing the world part.
 * <br>This is an attempt at a more natural protection of data  
 */
public interface WorldBuilder extends Cloneable {
	/**
	 * Get the x extent (width) of the world
	 * @param xExtent
	 */
	void setXExtent(int xExtent);
	/**
	 * Get the y extent (height) of the world
	 * @param yExtent
	 */
	void setYExtent(int yExtent);
	/**
	 * Set the type of the tile in a particular location
	 * @param x
	 * @param y
	 * @param terrainType
	 * @throws CoordinatesOutOfBoundsException when the coordinates are outside of the map
	 */
	void setTileType(int x, int y, TerrainType terrainType);
	/**
	 * @return
	 */
	WorldBuilder clone();
	/**
	 * Get a reference to the world that this object is mutating
	 * @return
	 */
	World getWorld();
}
