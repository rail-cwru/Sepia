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
package edu.cwru.sepia.util;

import java.lang.reflect.Array;
import java.util.Arrays;

import edu.cwru.sepia.environment.model.state.World;

/**
 * Some general utilities
 * <br>If this gets big, consider bringing in guava/apache commons, etc
 *
 */
public class Util {
	
	/**
	 * Make a new square array of a different size that contains as much of the original array as fits.
	 * @param clazz The class of the new array.  Java generics and arrays are mortal enemies, and we shouldn't pull class data from the default, since it might be a different subtype of T
	 * @param oldArray
	 * @param length1 First dimension length of new array
	 * @param length2 Second dimension length of new array
	 * @param defaultValue the default value to put in if there is space
	 * @return
	 */
	public static <T> T[][] getResized2DArray(Class<T> clazz, T[][] oldArray, int length1, int length2, T defaultValue) {
		//Can't do new T[][], which is sad
//		T[][] newArray = (T[][])new Object[length1][length2];
		T[][] newArray = Arrays.copyOf(oldArray, length1); //This makes a copy of one dimension
		for(int i = 0; i < length1; i++) {
			if (oldArray.length > i && oldArray[i] != null) {
				newArray[i] = Arrays.copyOf(oldArray[i], length2); //Copy of the other dimension
			} else {
				//Copy not possible, but we have the class
				newArray[i] = (T[])Array.newInstance(clazz, length2);
			}
			if (i < oldArray.length) {
				//Part of old array, so copy
				if (newArray[i].length > oldArray[i].length) {
					//New one is bigger, so copy as many as possible (which copyof did already), then fill the rest
//					System.arraycopy(oldArray[i], 0, newArray[i], 0, oldArray[i].length);
					Arrays.fill(newArray[i], oldArray[i].length, newArray[i].length, defaultValue);
				} else {
					//Old one is bigger or the same size, only copy the ones that fit, which copyOf did already
//					System.arraycopy(oldArray[i], 0, newArray[i], 0, newArray[i].length);
				}

			} else {
				//Bigger than old array, so fill the row with the default 
				Arrays.fill(newArray[i], defaultValue);
			}
		}
		return newArray;
	}

	/**
	 * An assert that certain coordinates are in bounds.
	 * @param world
	 * @param x
	 * @param y
	 * @throws CoordinatesOutOfBoundsException when the coordinates are not in bounds
	 */
	public static void assertInBounds(World world, int x, int y) {
		if (!world.inBounds(x, y)) {
			throw new CoordinatesOutOfBoundsException("Coordinates " + x + "," + y + " do not fit in a "+world.getXExtent() + " by " + world.getYExtent() + " 0-indexed world.");
		}
	}
}
