package edu.cwru.SimpleRTS.agent;


import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;

/**
 * This is a simple agent to test the "produce peasant action".
 * @author Feng
 *
 */
public class MyAgent extends Agent {

	private static final long serialVersionUID = -4047208702628325380L;

	public MyAgent(int playernum) {
		super(playernum);
	}

	StateView currentState;
	
	@Override
	public Builder<Integer, Action> initialStep(StateView newstate) {
		return middleStep(newstate);
	}

	@Override
	public ImmutableMap.Builder<Integer,Action> middleStep(StateView newState) {
		ImmutableMap.Builder<Integer,Action> builder = new ImmutableMap.Builder<Integer,Action>();
		currentState = newState;
		System.out.println("All units: " + currentState.getAllUnitIds());
		//TemplateView townhallTemplate2 = currentState.getTemplate(playernum, "TownHall");
		//int townhallID2 = townhallTemplate2.getID();
		//System.out.println("townhadd id: " + townhallID2);
//		int unitId = 1;
//			Unit.UnitView unit = currentState.getUnit(unitId);
//			System.out.println("fuck unit: " + unit.getXPosition() + " " + unit.getYPosition());
//			int dir = (int)(Math.random()*8);
//			//Action a = new DirectedAction(unitId, ActionType.PRIMITIVEMOVE, Direction.values()[dir]);
//			Action b = new LocatedAction(unitId, ActionType.COMPOUNDMOVE, 1, 1);
//			builder.put(unitId, b);
		if(currentState.getResourceAmount(playernum, ResourceType.GOLD)>=400) {
			System.out.println("already have enough");
			
			TemplateView peasanttemplate = currentState.getTemplate(playernum, "Peasant");
			//TemplateView townhallTemplate = currentState.getTemplate(playernum, "Townhall");
			int peasanttemplateID = peasanttemplate.getID();
			//int townhallID = townhallTemplate.getID();
			builder.put(0, Action.createCompoundProduction(2, peasanttemplateID));
			System.out.println("supply amount: " + currentState.getSupplyAmount(0));
			System.out.println("supply cap: " + currentState.getSupplyCap(0));
		}
		else {
			int peasantId = 1;
			Unit.UnitView peasant = currentState.getUnit(peasantId);
			int townhallId = 0;
			//Action a = new DirectedAction(unitId, ActionType.PRIMITIVEMOVE, Direction.values()[dir]);
			Action b = null;
			if(currentState.getUnit(peasantId).getCargoType() == ResourceType.GOLD && currentState.getUnit(peasantId).getCargoAmount()>0)
				b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
			else {
				List<Integer> resourceIds = currentState.getResourceNodeIds(Type.GOLD_MINE);
				b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
			}
			builder.put(peasantId, b);
		}
		return builder;
	}

	@Override
	public void terminalStep(StateView newstate) {
	}
	
	public static String getUsage() {
		return "None";
	}

}
