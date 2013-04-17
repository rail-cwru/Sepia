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

/**
 * Context for the drawing of the game map
 * <br>Contains scaling, boundries, etc
 */
public class DrawingContext {
	private final int playerNumber;
	private final int topBarHeight;
	private final int gameWorldTopLeftX;
	private final int gameWorldTopLeftY;
	private final int pixelWidth;
	private final int pixelHeight;
	private final int scalingFactor;
	
	/**
	 * @param gameWorldTopLeftX
	 * @param gameWorldTopLeftY
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param scalingFactor
	 */
	public DrawingContext(int playerNumber, int topBarHeight, int gameWorldTopLeftX, int gameWorldTopLeftY,
			int pixelWidth, int pixelHeight, int scalingFactor) {
		this.playerNumber = playerNumber;
		this.topBarHeight = topBarHeight;
		this.gameWorldTopLeftX = gameWorldTopLeftX;
		this.gameWorldTopLeftY = gameWorldTopLeftY;
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		this.scalingFactor = scalingFactor;
	}

	
	/**
	 * @return the playerNumber
	 */
	public int getPlayerNumber() {
		return playerNumber;
	}


	/**
	 * @return the gameWorldTopLeftX
	 */
	public int getGameWorldTopLeftX() {
		return gameWorldTopLeftX;
	}

	/**
	 * @return the gameWorldTopLeftY
	 */
	public int getGameWorldTopLeftY() {
		return gameWorldTopLeftY;
	}

	/**
	 * @return the pixelWidth
	 */
	public int getPixelWidth() {
		return pixelWidth;
	}

	/**
	 * @return the pixelHeight
	 */
	public int getPixelHeight() {
		return pixelHeight;
	}

	/**
	 * @return the scalingFactor
	 */
	public int getScalingFactor() {
		return scalingFactor;
	}
	
    //TODO: make this non-redundant with GamePanel
	public int convertGameWorldToPixelX(int gameWorldX) {
		return (gameWorldX-gameWorldTopLeftX)*scalingFactor;
	}
	public int convertGameWorldToPixelY(int gameWorldY) {
		return (gameWorldY-gameWorldTopLeftY)*(scalingFactor)+topBarHeight;
	}
	public int convertPixelToGameWorldX(int pixelX) {
		return pixelX/scalingFactor+gameWorldTopLeftX;
	}
	public int convertPixelToGameWorldY(int pixelY) {
		return (pixelY-topBarHeight)/scalingFactor+gameWorldTopLeftY;
	}


	/**
	 * @return the topBarHeight
	 */
	public int getTopBarHeight() {
		return topBarHeight;
	}
}
