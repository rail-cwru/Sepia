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
import edu.cwru.sepia.util.DeepEquatableUtil;

/**
 * @author The Condor
 *
 */
public class WorldView implements World {
	
	
	
	private final int xExtent;
	private final int yExtent;
	private final TerrainType[][] tiles;
	@Override
	public World getView() {
		//It's immutable, so just return it
		return this;
	}
	
	
	
	/**
	 * @param xExtent2
	 * @param yExtent2
	 * @param newTiles
	 */
	public WorldView(int xExtent, int yExtent, TerrainType[][] newTiles) {
		this.xExtent = xExtent;
		this.yExtent = yExtent;
		this.tiles = newTiles;
	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.util.DeepEquatable#deepEquals(java.lang.Object)
	 */
	@Override
	public boolean deepEquals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || this.getClass() != other.getClass()) {
			return false;
		}
		WorldView o = (WorldView)other;
		if (this.xExtent != o.xExtent) {
			return false;
		}
		if (this.yExtent != o.yExtent) {
			return false;
		}
		return DeepEquatableUtil.deepEquals(this.tiles, o.tiles);
	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getTile(int, int)
	 */
	@Override
	public TerrainType getTerrainType(int x, int y) {
		if (inBounds(x,y))
			return tiles[x][y];
		throw new IllegalArgumentException("Coordinates out of bounds");
	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getXExtent()
	 */
	@Override
	public int getXExtent() {
		// TODO Auto-generated method stub
		return yExtent;
	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getYExtent()
	 */
	@Override
	public int getYExtent() {
		// TODO Auto-generated method stub
		return xExtent;
	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#inBounds(int, int)
	 */
	@Override
	public boolean inBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < getXExtent() && y < getYExtent();
	}

	

}
