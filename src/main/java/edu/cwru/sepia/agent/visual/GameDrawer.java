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
package edu.cwru.sepia.agent.visual;

import java.awt.Graphics;

import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

/**
 * Interface for drawing the game board
 */
public interface GameDrawer {
	

	int getTopBarHeight();
	void drawTopBar(DrawingContext context, Graphics g);
	/**
	 * Draw a specific tile, including the ground color and all units and resources on it and all of the tile-based information.
	 * <br>This will be called for all visible tiles but it may or may not be called for tiles outside the visible area.
	 * @param context
	 * @param g
	 * @param x
	 * @param y
	 */
	void drawTile(DrawingContext context, Graphics g, int x, int y);
	/**
	 * Draw the background.  
	 * <br>Called once before any of the tiles have been drawn.
	 * @param context
	 * @param g
	 */
	void drawBackground(DrawingContext context, Graphics g);
	/**
	 * Draw the foreground, with all of the global or inter-tile overlays.  
	 * <br>Called once after all the tiles have been drawn.
	 * @param context
	 * @param g
	 */
	void drawForeground(DrawingContext context, Graphics g);
	
	/**
	 * Update the drawer to use the most recent state and history
	 * @param state
	 * @param history
	 */
	void updateState(StateView state, HistoryView history);
}
