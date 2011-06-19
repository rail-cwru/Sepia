package edu.cwru.SimpleRTS.model;

import java.util.HashMap;
import java.util.LinkedList;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.Configuration;

public class SimpleModel implements Model {
	
	private State state;
	private HashMap<Unit, LinkedList<Action>> queuedPrimatives;
	
	@Override
	public void createNewWorld() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void executeActions(Action[] action) {
		for(Action a : action) 
		{
			Unit u = state.getUnit(a.getUnitId());
			int x = u.getxPosition();
			int y = u.getyPosition();
			int xPrime = 0;
			int yPrime = 0;
			if(a instanceof DirectedAction)
			{
				Direction d = ((DirectedAction)a).getDirection();
				xPrime = x + d.xComponent();
				yPrime = y + d.yComponent();
			}
			else if(a instanceof LocatedAction)
			{
				xPrime = x + ((LocatedAction)a).getX();
				yPrime = y + ((LocatedAction)a).getY();
			}
			switch(a.getType())
			{
				case PRIMITIVEMOVE:
					if(u.canMove() && empty(xPrime,yPrime))
					{
						u.setxPosition(xPrime);
						u.setyPosition(yPrime);
					}
					break;
				case PRIMITIVEGATHER:
					Resource resource = state.resourceAt(xPrime, yPrime);
					if(resource == null)
						break;
					if(!u.canGather())
						break;
					int amountToExtract = Integer.parseInt(Configuration.getInstance().get(
																	resource.getType()+"GatherRate"));
					amountToExtract = Math.min(amountToExtract, resource.getAmountRemaining());
					u.pickUpResource(resource.getType(), amountToExtract);
					resource.setAmountRemaining(resource.getAmountRemaining()-amountToExtract);
					break;
			}
		}
	}
	private boolean empty(int x, int y) {
		return state.unitAt(x, y) == null && state.resourceAt(x, y) == null;
	}
	@Override
	public State getState() {
		return state;//TODO: make read-only version of the state
	}

}
