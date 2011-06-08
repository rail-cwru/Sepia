package edu.cwru.SimpleRTS.agent;

public abstract class Agent {
	private static int nextID = 0;
	protected final int ID;
	
	protected Agent() {
		ID = nextID++;
	}
	
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Agent))
			return false;
		return ID == ((Agent)o).ID;
	}
}
