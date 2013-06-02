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

import java.io.Serializable;
import java.util.Arrays;

import edu.cwru.sepia.environment.model.state.Tile.TerrainType;
import edu.cwru.sepia.util.DeepEquatableUtil;
import edu.cwru.sepia.util.Util;

/**
 *	Implementation of the world
 *
 */
public class WorldImpl implements World, WorldBuilder, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8836082603757749458L;
	private int xExtent;
	private int yExtent;
	private TerrainType[][] tiles;
	private static final TerrainType defaultTerrain = TerrainType.LAND;
	//Workaround to keep using array and have a generic resize, map from coordinate would be better
	private static final Class<TerrainType> terrainClass = TerrainType.class;
	public WorldImpl() {
		xExtent = 0;
		yExtent = 0;
		tiles = new TerrainType[0][0];
	}
	
	/**
	 * @param xExtent2
	 * @param yExtent2
	 * @param newTiles
	 */
	public WorldImpl(int xExtent, int yExtent, TerrainType[][] tiles) {
		this.xExtent = xExtent;
		this.yExtent = yExtent;
		this.tiles = tiles;
	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getTile(int, int)
	 */
	@Override
	public TerrainType getTerrainType(int x, int y) {
		Util.assertInBounds(this, x, y);
		return tiles[x][y];
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getXExtent()
	 */
	@Override
	public int getXExtent() {
		return xExtent;
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getYExtent()
	 */
	@Override
	public int getYExtent() {
		return yExtent;
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
		WorldImpl o = (WorldImpl)other;
		if (this.xExtent != o.xExtent) {
			return false;
		}
		if (this.yExtent != o.yExtent) {
			return false;
		}
		return DeepEquatableUtil.deepEquals(this.tiles, o.tiles);
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.WorldBuilder#setXExtent(int)
	 */
	@Override
	public void setXExtent(int xExtent) {
		int oldExtent = this.xExtent;
		if (xExtent != oldExtent) {
			this.xExtent = xExtent;
			tiles = Util.getResized2DArray(terrainClass, tiles, this.xExtent, this.yExtent, defaultTerrain);
		}
		
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.WorldBuilder#setYExtent(int)
	 */
	@Override
	public void setYExtent(int yExtent) {
		int oldExtent = this.yExtent;
		if (yExtent != oldExtent) {
			this.yExtent = yExtent;
			tiles = Util.getResized2DArray(terrainClass, tiles, this.xExtent, this.yExtent, defaultTerrain);
		}
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.WorldBuilder#setTileType(int, int, edu.cwru.sepia.environment.model.state.Tile.TerrainType)
	 */
	@Override
	public void setTileType(int x, int y, TerrainType terrainType) {
		Util.assertInBounds(this, x, y);
		this.tiles[x][y] = terrainType;
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#inBounds(int, int)
	 */
	@Override
	public boolean inBounds(int x, int y)
	{
		return x >= 0 && y >= 0 && x < getXExtent() && y < getYExtent(); 
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.WorldBuilder#getWorld()
	 */
	@Override
	public World getWorld() {
		return this;
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.environment.model.state.World#getView()
	 */
	@Override
	public World getView() {
		TerrainType[][] newTiles = new TerrainType[tiles.length][];
		for (int i = 0; i < tiles.length; i++) {
			newTiles[i] = Arrays.copyOf(tiles[i], tiles[i].length);
		}
		return new WorldView(this.xExtent, this.yExtent, newTiles);
	}
	public WorldBuilder clone() {
		TerrainType[][] newTiles = new TerrainType[tiles.length][];
		for (int i = 0; i < tiles.length; i++) {
			newTiles[i] = Arrays.copyOf(tiles[i], tiles[i].length);
		}
		return new WorldImpl(this.xExtent, this.yExtent, newTiles);
	}
}

