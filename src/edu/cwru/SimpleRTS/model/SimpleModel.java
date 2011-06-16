package edu.cwru.SimpleRTS.model;

import java.util.HashMap;
import java.util.LinkedList;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.Preferences;

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
			Unit u = getUnitFromID(a.getActer());
			int x = u.getxPosition();
			int y = u.getyPosition();
			switch(a.getType())
			{
				case PRIMATIVEMOVE:
				{
					if (u.canMove())
					{
						DirectedAction act = (DirectedAction)a;
						int xPrime = x + act.getDirection().xComponent();
						int yPrime = y + act.getDirection().yComponent();
						if(empty(xPrime,yPrime))
							u.move(act.getDirection());
					}
					break;
				}
				case PRIMATIVEGATHER:
				{
						if(u.canGather())
						{
							DirectedAction act = (DirectedAction)a;
							int xPrime = x + act.getDirection().xComponent();
							int yPrime = y + act.getDirection().yComponent();
							Resource resource = state.resourceAt(xPrime, yPrime);
							if(resource == null)
								break;
							int amountToExtract = Integer.parseInt(Preferences.getInstance().getPreference(resource.getType()+"GatherRate"));
							amountToExtract = Math.min(amountToExtract, resource.getAmountRemaining());
							u.pickUpResource(resource.getType(), amountToExtract);
							resource.setAmountRemaining(resource.getAmountRemaining()-amountToExtract);
						}
						break;
				}
			}
		}
	}
	private boolean empty(int x, int y) {
		return state.unitAt(x, y) == null && state.resourceAt(x, y) == null;
	}
	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

}
