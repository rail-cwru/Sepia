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
import edu.cwru.sepia.util.DeepEquatable;

/**
 * The interface for being the source of information about the world.
 * <br>This covers only the physical world: its size, physical terrain features
 * <br>This does not cover abstract values(technology progress, amounts of gathered resources, or templates), the resource nodes (mines and trees) or any of the buildings or other units that may be controlled by the player 
 *
 */
public interface World extends DeepEquatable {
	/**
	 * Get the terrain type at a location in the world
	 * @param x
	 * @param y
	 * @return
	 * @throws CoordinatesOutOfBoundsException when the coordinates are outside of the map
	 */
	public TerrainType getTerrainType(int x, int y);
	/**
	 * Set the y extent of the world
	 * @return
	 */
	public int getXExtent();
	/**
	 * Get the y extent of the world
	 * @return
	 */
	public int getYExtent();
	/**
	 * A convenience method for whether the tile is on the map
	 * @param x
	 * @param y
	 * @return
	 */
	boolean inBounds(int x, int y);

	/**
	 * Return an unlinked view of the world
	 * @return
	 */
	World getView();
}
