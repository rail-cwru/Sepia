package edu.cwru.SimpleRTS.agent;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.Configuration;
import edu.cwru.SimpleRTS.util.ConfigurationValues;
import edu.cwru.SimpleRTS.util.PreferencesConfigurationLoader;

/**
 * This agent will first collect gold to produce a peasant,
 * then the two peasants will collect gold and wood separately until reach goal.
 * @author Feng
 *
 */
public class ResourceCollectionAgent extends Agent {
	private static final long serialVersionUID = -4047208702628325380L;
	private static final Logger logger = Logger.getLogger(ResourceCollectionAgent.class.getCanonicalName());

	private int goldRequired;
	private int woodRequired;
	
	private int step;
	
	public ResourceCollectionAgent(int playernum) {
		super(playernum);
		Configuration config = PreferencesConfigurationLoader.loadConfiguration();
		goldRequired = ConfigurationValues.MODEL_REQUIRED_GOLD.getIntValue(config);
		woodRequired = ConfigurationValues.MODEL_REQUIRED_WOOD.getIntValue(config);
	}

	StateView currentState;
	
	@Override
	public Map<Integer, Action> initialStep(StateView newstate, History.HistoryView statehistory) {
		step = 0;
		return middleStep(newstate, statehistory);
	}

	@Override
	public Map<Integer,Action> middleStep(StateView newState, History.HistoryView statehistory) {
		step++;
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("=> Step: " + step);
		}
		
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		currentState = newState;
		
		int currentGold = currentState.getResourceAmount(0, ResourceType.GOLD);
		int currentWood = currentState.getResourceAmount(0, ResourceType.WOOD);
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Current Gold: " + currentGold);
		}
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Current Wood: " + currentWood);
		}
		List<Integer> allUnitIds = currentState.getAllUnitIds();
		List<Integer> peasantIds = new ArrayList<Integer>();
		List<Integer> townhallIds = new ArrayList<Integer>();
		for(int i=0; i<allUnitIds.size(); i++) {
			int id = allUnitIds.get(i);
			UnitView unit = currentState.getUnit(id);
			String unitTypeName = unit.getTemplateView().getName();
			if(unitTypeName.equals("TownHall"))
				townhallIds.add(id);
			if(unitTypeName.equals("Peasant"))
				peasantIds.add(id);
		}
		
		if(peasantIds.size()>=2) {  // collect resources
			if(currentWood<woodRequired) {
				int peasantId = peasantIds.get(1);
				int townhallId = townhallIds.get(0);
				Action b = null;
				if(currentState.getUnit(peasantId).getCargoAmount()>0)
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
				else {
					List<Integer> resourceIds = currentState.getResourceNodeIds(Type.TREE);
					b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
				}
				builder.put(peasantId, b);
			}
			if(currentGold<goldRequired) {
				int peasantId = peasantIds.get(0);
				int townhallId = townhallIds.get(0);
				Action b = null;
				if(currentState.getUnit(peasantId).getCargoType() == ResourceType.GOLD && currentState.getUnit(peasantId).getCargoAmount()>0)
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
				else {
					List<Integer> resourceIds = currentState.getResourceNodeIds(Type.GOLD_MINE);
					b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
				}
				builder.put(peasantId, b);
			}
		}
		else {  // build peasant
			if(currentGold>=400) {
				if(logger.isLoggable(Level.FINE))
				{
					logger.fine("already have enough gold to produce a new peasant.");
				}
				TemplateView peasanttemplate = currentState.getTemplate(playernum, "Peasant");
				int peasanttemplateID = peasanttemplate.getID();
				if(logger.isLoggable(Level.FINE))
				{
					logger.fine(String.valueOf(peasanttemplate.getID()));
				}
				int townhallID = townhallIds.get(0);
				builder.put(townhallID, Action.createCompoundProduction(townhallID, peasanttemplateID));
			} else {
				int peasantId = peasantIds.get(0);
				int townhallId = townhallIds.get(0);
				Action b = null;
				if(currentState.getUnit(peasantId).getCargoType() == ResourceType.GOLD && currentState.getUnit(peasantId).getCargoAmount()>0)
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
				else {
					List<Integer> resourceIds = currentState.getResourceNodeIds(Type.GOLD_MINE);
					b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
				}
				builder.put(peasantId, b);
			}
		}
		return builder;
	}

	@Override
	public void terminalStep(StateView newstate, History.HistoryView statehistory) {
		step++;
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("=> Step: " + step);
		}
		
		int currentGold = newstate.getResourceAmount(0, ResourceType.GOLD);
		int currentWood = newstate.getResourceAmount(0, ResourceType.WOOD);
		
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Current Gold: " + currentGold);
		}
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Current Wood: " + currentWood);
		}
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Congratulations! You have finished the task!");
		}
	}
	
	public static String getUsage() {
		return "None";
	}
	@Override
	public void savePlayerData(OutputStream os) {
		//this agent lacks learning and so has nothing to persist.
		
	}
	@Override
	public void loadPlayerData(InputStream is) {
		//this agent lacks learning and so has nothing to persist.
	}
}
