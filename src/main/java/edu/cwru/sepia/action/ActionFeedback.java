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
package edu.cwru.sepia.action;

public enum ActionFeedback {
	/**
	 * Indicates that the action has been successfully completed
	 */
	COMPLETED, 
	/**
	 * Indicates that the action is incomplete
	 */
	INCOMPLETE,
	/**
	 * Indicates that the action cannot be completed
	 */
	FAILED,
	/**
	 * Indicates that the action is incomplete and had to recalculate multiple times, indicating the possibility of being stuck
	 */
	INCOMPLETEMAYBESTUCK,
	/**
	 * Indicates that the action was issued to a unit not controlled by the player
	 */
	INVALIDCONTROLLER,
	/**
	 * Indicates that the action was mapped to a unit other than the one that the action refers to
	 */
	INVALIDUNIT,
	/**
	 * Indicates that the action was improperly constructed
	 */
	INVALIDTYPE
	
}
