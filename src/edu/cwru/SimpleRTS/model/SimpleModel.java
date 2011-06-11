package edu.cwru.SimpleRTS.model;

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.resource.Resource;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.mobile.MobileUnit;
import edu.cwru.SimpleRTS.model.unit.mobile.MobileUnitTemplate;
import edu.cwru.SimpleRTS.util.Preferences;

public class SimpleModel implements Model {
	
	private State state;
	
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
			PrimitiveAction primitive = null;
			if(a instanceof CompoundAction)
				primitive = ((CompoundAction)a).getNextAction();
			else
				primitive = (PrimitiveAction)a;
			Unit u = a.getUnit();
			int x = u.getxPosition();
			int y = u.getyPosition();
			int xPrime = 0;
			int yPrime = 0;
			if(primitive.getType() != PrimitiveActionType.UPGRADE)
			{
				xPrime = x + primitive.getDirection().xComponent();
				yPrime = y + primitive.getDirection().yComponent();
			}
			switch(primitive.getType())
			{
				case MOVE:
					if(u instanceof MobileUnit && empty(xPrime,yPrime))
						((MobileUnit)u).move(primitive.getDirection());
					break;
				case GATHER:
					if(!(u instanceof MobileUnit))
						break;
					Resource resource = state.resourceAt(xPrime, yPrime);
					if(resource == null)
						break;
					if(!((MobileUnitTemplate)((MobileUnit)u).getTemplate()).canGather())
						break;
					int amountToExtract = Integer.parseInt(Preferences.getInstance().getPreference(
																	resource.getType()+"GatherRate"));
					amountToExtract = Math.min(amountToExtract, resource.getAmountRemaining());
					((MobileUnit)u).pickUpResource(resource.getType(), amountToExtract);
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
		// TODO Auto-generated method stub
		return null;
	}

}
