package edu.cwru.SimpleRTS.util;

public class Pair<A, B> {
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
}
