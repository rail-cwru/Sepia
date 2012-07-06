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

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public A a;
	public B b;
	public Pair(A a,B b) {
		this.a = a;
		this.b = b;
	}
	@Override
	public int hashCode() {
		return ((a.hashCode() & 0xFFFF) << 16) | (b.hashCode() & 0xFFFF);
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Pair))
			return false;
		Pair<?,?> p = (Pair<?,?>)o;
		return a.equals(p.a) && b.equals(p.b);
	}
}
