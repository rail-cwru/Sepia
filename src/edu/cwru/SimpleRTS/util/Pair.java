package edu.cwru.SimpleRTS.util;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
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
