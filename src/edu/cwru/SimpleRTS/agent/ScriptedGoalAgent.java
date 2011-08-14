package edu.cwru.SimpleRTS.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTask;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

/**
 * An agent based around the concept of build orders.
 * Limited to a single concurrent goal, 
 * @author The Condor
 *
 */
public class ScriptedGoalAgent extends Agent implements Serializable {
	//A scripted agent that pulls from a file 
	//	Transfer:Oldresource:Newresource
		//oldresource may be Food/Wood/Idle
		//as can new resource
	//	Attack:All
		//send everything into an attack
	//	Attack
		//Send military units in an attack
	//	Build:Templatename:xoffset:yoffset
		//template is a building
	private Goal nextgoal;
	private BufferedReader commandSource;
	private boolean outofcommands;
	private PrimativeAttackCoordinator attackcoordinator;
	private int[] centeroftown;
	public ScriptedGoalAgent(int playernumber, BufferedReader commandSource) {
		super(playernumber);
		this.commandSource = commandSource;
		outofcommands = false;
		nextgoal=null;
		attackcoordinator = new PrimativeAttackCoordinator(playernumber);
		centeroftown=null;
	}
	
	/**
	 * The goal and the means for achieving it.
	 * TODO: This should probably be severed into subclasses with a static build method
	 * @author The Condor
	 *
	 */
	public class Goal {
		GoalType type;
		boolean attackwithall;
		int numgatherers;
		GathererTask starttype;
		GathererTask endtype;
		TemplateView template;
		WaitType waittype;
		int xoffset;
		int yoffset;
		int waitvalue;
		public Goal(String command, StateView state) {
			//Split it into arguments and such
			String[] split = command.split(":");
			if (GoalType.Attack.toString().equals(split[0])) {
				assert split.length == 1 || split.length ==2;
				type = GoalType.Attack;
				//Attacking, may have argument All, anything 
				if (split.length < 1 || !split[1].equals("All")) {
					//Attack with only military units
					attackwithall = false;
				}
				else {
					//Attack with everything
					attackwithall = true;
				}
			}
			else if (GoalType.Transfer.toString().equals(split[0])) {
				assert split.length == 3;
				type = GoalType.Transfer;
				starttype = GathererTask.valueOf(split[1]);
				endtype = GathererTask.valueOf(split[2]);
			}
			else if (GoalType.Build.toString().equals(split[0])) {
				assert split.length == 4;
				type = GoalType.Build;
				template = state.getTemplate(playernum, split[1]);
				xoffset=Integer.parseInt(split[2]);
				yoffset=Integer.parseInt(split[3]);
			}
			else if (GoalType.Produce.toString().equals(split[0])) {
				assert split.length == 2;
				type = GoalType.Produce;
				template = state.getTemplate(playernum, split[1]);
			}
			else if (GoalType.Wait.toString().equals(split[0])) {
				assert split.length == 3;
				type = GoalType.Wait;
				waittype = WaitType.valueOf(split[1]);
				waitvalue = Integer.parseInt(split[2]);
			}
			else {
				type = GoalType.Faulty;
				assert false : "Invalid Goal";
			}
		}
		private boolean canExecute(StateView state, RelevantStateView relstate) {
			switch (type) {
			case Wait:
			{
				//get the right resource
				int currentval = (waittype == WaitType.Gold)?relstate.ngold:(waittype == WaitType.Wood?relstate.nwood:-1);
				if (currentval < waitvalue)
					return false;
				return true;
			}
			case Faulty:
				return true;
			case Build:
				//check resources
				if (relstate.ngold >= template.getGoldCost() && relstate.nwood >= template.getWoodCost() && relstate.nfoodremaining >= template.getFoodCost())
					return true;
				return false;
			case Attack:
				//You can always attack
				return true;
			case Transfer: 
				int workersonsourceresource=-1;
				if (starttype == GathererTask.Gold)
				{
					workersonsourceresource = relstate.goldworkers.size();
				}
				else if (starttype == GathererTask.Wood)
				{
					workersonsourceresource = relstate.woodworkers.size();
				}
				else if (starttype == GathererTask.Idle) {
					workersonsourceresource = relstate.idleworkers.size();
				}
				else //should never hit this
					assert false:"Must have added a gatherer task without changing this";
				
				if (workersonsourceresource >= numgatherers) {
					return true;
				}
				else
					return false;
			}
			return false;
		}
		public boolean tryExecute(StateView state, RelevantStateView relstate, Builder<Integer,Action> actions) {
			//See if it can execute it
			if (!canExecute(state, relstate))
				return false;
			
			//Actually execute it
			switch (type) {
			case Wait:
				break;
			case Faulty:
				break;
			case Build:
				//Find a place to build it
				
				int[] placetobuild = state.getClosestOpenPosition(centeroftown[0]+xoffset,centeroftown[1]+xoffset);
				
				for (Integer id : relstate.myUnitIDs) {
					if (!relstate.unitsWithTasks.contains(id)&&state.getUnit(id).getTemplateView().canProduce(template.getID())) {
						actions.put(id,Action.createCompoundBuild(id, template.getID(),placetobuild[0],placetobuild[1]));
						break;
					}
				}
				
				break;
			case Produce:
				//Find a unit that isn't busy and can produce it, then produce it from that one
				for (Integer id : relstate.myUnitIDs) {
					if (!relstate.unitsWithTasks.contains(id)&&state.getUnit(id).getTemplateView().canProduce(template.getID())) {
						actions.put(id,Action.createCompoundProduction(id, template.getID()));
						break;
					}
				}
				
			case Attack:
				if (attackwithall) {
					for (Integer i : relstate.myUnitIDs) {
						attackcoordinator.addAttacker(i);
					}
				}
				else {
					for (Integer id : relstate.myUnitIDs) {
						if (!state.getUnit(id).getTemplateView().canGather())
							attackcoordinator.addAttacker(id);
					}
				}
				break;
			case Transfer: 
				LinkedList<Integer> workersource=null;
				LinkedList<Integer> workerdest=null;
				if (starttype == GathererTask.Gold)	{
					workersource = relstate.goldworkers;
				}
				else if (starttype == GathererTask.Wood) {
					workersource = relstate.woodworkers;
				}
				else if (starttype == GathererTask.Idle) {
					workersource = relstate.idleworkers;
				}
				else //should never hit this
					assert false:"Must have added a GathererTask without changing this";
				if (endtype == GathererTask.Gold)	{
					workerdest = relstate.goldworkers;
				}
				else if (endtype == GathererTask.Wood) {
					workerdest = relstate.woodworkers;
				}
				else if (endtype == GathererTask.Idle) {
					workerdest = relstate.idleworkers;
				}
				else //should never hit this
					assert false:"Must have added a GathererTask without changing this";
				//And move them from one to the other
				
				if (endtype == GathererTask.Gold || endtype == GathererTask.Wood) {
					ResourceNode.Type nodetype = endtype == GathererTask.Gold?ResourceNode.Type.GOLD_MINE:ResourceNode.Type.TREE;
					for (int i = 0; i<numgatherers;i++) {
						Integer id = workersource.removeFirst();
						
						UnitView worker = state.getUnit(id);
						int workerx=worker.getXPosition();
						int workery=worker.getYPosition();
						//Find the nearest appropriate resource
						List<Integer> resources = state.getResourceNodeIds(nodetype);
						int closestdist=Integer.MAX_VALUE;
						Integer closest = null;
						for (Integer resourceID : resources) {
							ResourceNode.ResourceView node = state.getResourceNode(resourceID);
							int dist = DistanceMetrics.chebyshevDistance(workerx,workery, node.getXPosition(), node.getYPosition());
							
							if (dist < closestdist) {
								closest = resourceID;
								closestdist = dist;
							}
						}
						if (closest!=null) {
							actions.put(id, Action.createCompoundGather(id,closest));
							workerdest.add(id);
						}
						else {
							workersource.add(id);
						}
						
					}
				}
				
			}
			return true;
			
		}
		
	}
	enum GathererTask {Wood, Gold, Idle;}
	enum WaitType {Wood, Gold}
	enum GoalType {
		Transfer, Attack, Produce, Build, Wait, Faulty; /*Faulty marks a bad argument into the goal*/
	}
	@Override
	public Builder<Integer, Action> initialStep(StateView newstate) {
		//Find the center of units, to use as a baseline
		List<Integer> myunits = newstate.getUnitIds(playernum);
		int xsum=0;
		int ysum=0;
		for (Integer id : myunits) {
			UnitView u = newstate.getUnit(id);
			xsum += u.getXPosition();
			ysum += u.getYPosition();
		}
		centeroftown = new int[]{xsum/myunits.size(), ysum/myunits.size()};
		try {
		return act(newstate);
		}
		catch (IOException e) {
			return new ImmutableMap.Builder<Integer,Action>();
		}
	}
	@Override
	public Builder<Integer, Action> middleStep(StateView newstate) {
		try {
			return act(newstate);
			}
			catch (IOException e) {
				return new ImmutableMap.Builder<Integer,Action>();
			}
	}
	@Override
	public void terminalStep(StateView newstate) {
		// TODO Auto-generated method stub
		
	}
	public Builder<Integer, Action> act(StateView state) throws IOException {
		
		RelevantStateView rsv = new RelevantStateView(playernum, state);
		ImmutableMap.Builder<Integer,Action> actions = new ImmutableMap.Builder<Integer,Action>();
		//while there are still commands to be found
		boolean done = outofcommands;
		while (!done) {
			//if you have no next goal, get one
			if (nextgoal==null) {
				String nextCommand = commandSource.readLine();
				if (nextCommand == null || nextCommand.equals("")) {
					done = true;
					outofcommands = true;
				}
				else {
					nextgoal = new Goal(nextCommand,state);
				}
			}
			//if you now have a goal, execute it
			if (nextgoal != null) {
				//See if you can do it now
				nextgoal.tryExecute(state, rsv, actions);
			}
			done = true;
		}
		attackcoordinator.coordinate(state, actions);
		
		
		return actions;
		
	}
	
	/**
	 * A simple structure storing relevant details of the state view that can be modified to take other actions into account
	 * @author The Condor
	 *
	 */
	class RelevantStateView {
		public LinkedList<Integer> idleworkers;
		public LinkedList<Integer> woodworkers;
		public LinkedList<Integer> goldworkers;
		public int notherworkers;
		public int ngold;
		public int nwood;
		public int nfoodremaining;
		public Set<Integer> unitsWithTasks;
		public List<int[]> spacesoccupiedbynewbuildings;
		List<Integer> myUnitIDs;
		public RelevantStateView (int playernum, StateView state){
			spacesoccupiedbynewbuildings = new LinkedList<int[]>();
			ngold = state.getResourceAmount(playernum, ResourceType.GOLD);
			nwood = state.getResourceAmount(playernum, ResourceType.WOOD);
			nfoodremaining = state.getSupplyCap(playernum) - state.getSupplyAmount(playernum);
			unitsWithTasks = new HashSet<Integer>();
			idleworkers = new LinkedList<Integer>();
			woodworkers = new LinkedList<Integer>();
			goldworkers = new LinkedList<Integer>();
			myUnitIDs = state.getUnitIds(playernum);
			for (Integer id : myUnitIDs) {
				UnitView u = state.getUnit(id);
				
					switch (u.getTask()) {
					case Gold:
						if (u.getTemplateView().canGather()) 
							goldworkers.add(id);
						break;
					case Wood:
						if (u.getTemplateView().canGather()) 
							woodworkers.add(id);
						break;
					case Idle:
						if (u.getTemplateView().canGather()) 
							idleworkers.add(id);
						break;
					case Build:
						unitsWithTasks.add(id);
						if (u.getTemplateView().canGather()) 
							notherworkers++;
						break;
					default:
						if (u.getTemplateView().canGather()) 
							notherworkers++;
					}
				
			}
		}
	}
}
