package edu.cwru.SimpleRTS.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.cwru.SimpleRTS.Log.BirthLog;
import edu.cwru.SimpleRTS.Log.DeathLog;
import edu.cwru.SimpleRTS.Log.EventLogger;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.util.DistanceMetrics;

/**
 * An agent based around the concept of build orders.
 * Limited to a single concurrent goal
 * Cannot fully handle new units that come in doing things 
 * Does not compensate for the possibility that a unit be both a gatherer/worker and a production unit (like a barracks/peasant)
 *
 */
public class ScriptedGoalAgent extends Agent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private String[] commandSource;
	private int commandSourceIterator;
	private boolean outofcommands;
	private PrimitiveAttackCoordinator attackcoordinator;
	private BasicGatheringCoordinator gathercoordinator;
	private BusynessCoordinator busycoordinator;
	private int[] centeroftown;
	private boolean verbose;
	public ScriptedGoalAgent(int playernumber, BufferedReader commandSource, Random r, boolean verbose) {
		super(playernumber);
		this.verbose = verbose;
		{
			ArrayList<String> temp = new ArrayList<String>();
			String t;
			try {
				while ((t=commandSource.readLine())!=null && !t.equals("") && !t.startsWith("END")) {
					temp.add(t.split("//")[0]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.commandSource = temp.toArray(new String[]{});
		}
		commandSourceIterator = 0;
		outofcommands = false;
		nextgoal=null;
		attackcoordinator = new PrimitiveAttackCoordinator(playernumber);
		gathercoordinator = new BasicGatheringCoordinator(playernumber, r);
		busycoordinator = new BusynessCoordinator(playernumber);
		centeroftown=null;
	}
	
	/**
	 * The goal and the means for achieving it.
	 */
	public static class Goal implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
		String commandstring;
		private ScriptedGoalAgent agent;
		public Goal(String command, StateView state, ScriptedGoalAgent agent) {
			this.agent = agent;
			//Split it into arguments and such
			String[] split = command.split(":");
			commandstring = command;
//			if (verbose)
//				System.out.println("Command: \""+command+"\"");
//			for (String s : split)
//				System.out.println(s);
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
				numgatherers=Integer.parseInt(split[1]);
				starttype = GathererTask.valueOf(split[2]);
				endtype = GathererTask.valueOf(split[3]);
			}
			else if (GoalType.Build.toString().equals(split[0])) {
				assert split.length == 4;
				type = GoalType.Build;
				template = state.getTemplate(agent.playernum, split[1]);
				xoffset=Integer.parseInt(split[2]);
				yoffset=Integer.parseInt(split[3]);
			}
			else if (GoalType.Produce.toString().equals(split[0])) {
				assert split.length == 2;
				type = GoalType.Produce;
				template = state.getTemplate(agent.playernum, split[1]);
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
		/**
		 * Query whether the goal can be accomplished given the state and modifications to it.
		 * @param state The state, bringing the units and other such things
		 * @param relstate Relevant parameters
		 * @return
		 */
		private boolean canExecute(StateView state, RelevantStateView relstate) {
			switch (type) {
			case Wait:
			{
				//get the right resource
				int currentval = (waittype == WaitType.Gold)?relstate.ngold:(waittype == WaitType.Wood?relstate.nwood:-1);
				if (currentval < waitvalue)
				{
					if (agent.verbose)
						System.out.println("Need to keep waiting: have " + currentval + " but need "+waitvalue);
					return false;
				}
				return true;
			}
			case Faulty:
				return true;
			case Build:
			case Produce:
				boolean foundaproducer=false;
				if (agent.verbose)
					System.out.println("Checking all of my " + relstate.myUnitIDs.size() + " units");
				for (Integer id : relstate.myUnitIDs) {
					if (agent.busycoordinator.isIdle(id) && 
							(type==GoalType.Produce||agent.gathercoordinator.hasIdleWorker(id)) && 
							state.getUnit(id).getTemplateView().canProduce(template.getID())) {
						foundaproducer=true;
						break;
					}
//					System.out.println(state.getUnit(id).getTemplateView().getUnitName());
//					System.out.println(agent.busycoordinator.isIdle(id)); 
//					System.out.println((type==GoalType.Produce||agent.gathercoordinator.hasIdleWorker(id))) ;
//					System.out.println(state.getUnit(id).getTemplateView().canProduce(template.getID()));
				}
				if (!foundaproducer)
				{
					if (agent.verbose)
						System.out.println("Cannot build/produce, unable to find a producer");
					return false;
				}
					
				//check resources
				if (agent.verbose)
					System.out.println("Considering building or producing");
				if (relstate.ngold < template.getGoldCost())
				{
					if (agent.verbose)
						System.out.println("Cannot build/produce, not enough gold: have " + relstate.ngold + " but need "+template.getGoldCost());
					return false;
				}
				if (relstate.nwood < template.getWoodCost())
				{
					if (agent.verbose)
						System.out.println("Cannot build/produce, not enough wood: have " + relstate.nwood + " but need "+template.getWoodCost());
					return false;
				}
				if (template.getFoodCost() > 0 && relstate.nfoodremaining < template.getFoodCost())
				{
					if (agent.verbose)
						System.out.println("Cannot build/produce, not enough food: have " + relstate.nfoodremaining + " open but need "+template.getFoodCost());
					return false;
				}
				return true;
			case Attack:
				//You can always attack
				return true;
			case Transfer: 
				int workersonsourceresource=-1;
				if (starttype == GathererTask.Gold)
				{
					workersonsourceresource = agent.gathercoordinator.numGoldWorkers();
				}
				else if (starttype == GathererTask.Wood)
				{
					workersonsourceresource = agent.gathercoordinator.numWoodWorkers();
				}
				else if (starttype == GathererTask.Idle) {
					workersonsourceresource = agent.gathercoordinator.numIdleWorkers();
				}
				else //should never hit this
					assert false:"Must have added a gatherer task without changing this";
				
				if (workersonsourceresource >= numgatherers) {
					return true;
				}
				else
				{
					if (agent.verbose)
						System.out.println("Cannot perform transfer, not enough workers on that resource");
					return false;
				}
			}
			return false;
		}
		/**
		 * See if those actions can be done.
		 * This is not a final test, merely the best that can be done without fully simulating it.
		 * This method alters this map by adding actions.
		 * @param state
		 * @param relstate
		 * @param actions A map builder of actions.
		 * @return Assign actions
		 */
		public boolean tryExecute(StateView state, RelevantStateView relstate, Map<Integer,Action> actions) {
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
			{	
				int[] placetobuild = state.getClosestOpenPosition(agent.centeroftown[0]+xoffset,agent.centeroftown[1]+yoffset);
				Integer id = agent.gathercoordinator.getIdleWorker();
					if (state.getUnit(id).getTemplateView().canProduce(template.getID())) {
						Action newact = Action.createCompoundBuild(id, template.getID(),placetobuild[0],placetobuild[1]);
						actions.put(id,newact);
						
						if (agent.gathercoordinator.hasIdleWorker(id) && agent.busycoordinator.isIdle(id))
						{
							agent.gathercoordinator.assignOther(id);
							agent.busycoordinator.assignBusy(id);
						}
						else
						{
							new RuntimeException("Programming error: Tried to build a building with a non idle worker").printStackTrace();
						}
						break;
					}
				
				break;
			}
			case Produce:
				//Find a unit that isn't busy and can produce it, then produce it from that one
				for (Integer id : relstate.myUnitIDs) {
					if (agent.busycoordinator.isIdle(id) && state.getUnit(id).getTemplateView().canProduce(template.getID())) {
						actions.put(id,Action.createCompoundProduction(id, template.getID()));
						agent.busycoordinator.assignBusy(id);
						break;
					}
				}
				break;
			case Attack:
				if (attackwithall) {
					for (Integer i : relstate.myUnitIDs) {
						agent.attackcoordinator.addAttacker(i);
					}
				}
				else {
					for (Integer id : relstate.myUnitIDs) {
						if (!state.getUnit(id).getTemplateView().canGather())
							agent.attackcoordinator.addAttacker(id);
					}
				}
				break;
			case Transfer: 
				//And move them from one to the other
				Integer id=null;
				if (starttype == GathererTask.Gold)	{
					id = agent.gathercoordinator.getGoldWorker();
				}
				else if (starttype == GathererTask.Wood) {
					id = agent.gathercoordinator.getWoodWorker();
				}
				else if (starttype == GathererTask.Idle) {
					id = agent.gathercoordinator.getIdleWorker();
				}
				else //should never hit this
					assert false:"Must have added a GathererTask without changing this";

				if (id==null)
					break;
				boolean reallychanging = true;
					ResourceNode.Type nodetype = endtype == GathererTask.Gold?ResourceNode.Type.GOLD_MINE:ResourceNode.Type.TREE;
					for (int i = 0; i<numgatherers;i++) {
						
						
						
						UnitView worker = state.getUnit(id);
						int workerx=worker.getXPosition();
						int workery=worker.getYPosition();
						//Find the nearest appropriate resource
						List<Integer> resources = state.getResourceNodeIds(nodetype);
						int closestdist=Integer.MAX_VALUE;
						Integer closest = null;
						if (endtype == GathererTask.Gold || endtype == GathererTask.Wood) {
							for (Integer resourceID : resources) {
								ResourceNode.ResourceView node = state.getResourceNode(resourceID);
								int dist = DistanceMetrics.chebyshevDistance(workerx,workery, node.getXPosition(), node.getYPosition());
								
								if (dist < closestdist) {
									closest = resourceID;
									closestdist = dist;
								}
							}
						}
						if (endtype == GathererTask.Idle || closest!=null) {
							switch (endtype) {
							case Idle:
								agent.gathercoordinator.assignIdle(id);
								break;
							case Gold:
								agent.gathercoordinator.assignGold(id);
								break;
							case Wood:
								agent.gathercoordinator.assignWood(id);
								break;
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
	public Map<Integer, Action> initialStep(StateView newstate) {
		//Put all units into the gathering coordinator, that they might 
		gathercoordinator.initialize(newstate);
		busycoordinator.initialize(newstate);
		
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
			return new HashMap<Integer,Action>();
		}
	}
	@Override
	public Map<Integer, Action> middleStep(StateView newstate) {
		
		try {
			return act(newstate);
			}
			catch (IOException e) {
				return new HashMap<Integer,Action>();
			}
	}
	@Override
	public void terminalStep(StateView newstate) {
		
	}
	public Map<Integer, Action> act(StateView state) throws IOException {
		if (verbose)
			System.out.println("ScriptedGoalAgent starting another action");
		EventLogger.EventLoggerView eventlog = state.getEventLog();
		int roundnumber = eventlog.getLastRound();
		List<BirthLog> births = eventlog.getBirths(roundnumber);
		List<DeathLog> deaths = eventlog.getDeaths(roundnumber);
		for (BirthLog birth : births) {
			if (
				state.getUnit(birth.getNewUnitID()).getTemplateView().canGather()) {
				gathercoordinator.assignIdle(birth.getNewUnitID());
			}
			busycoordinator.assignIdle(birth.getNewUnitID());
		}
		for (DeathLog death : deaths) {
			gathercoordinator.removeUnit(death.getDeadUnitID());
			busycoordinator.removeUnit(death.getDeadUnitID());
		}
		gathercoordinator.checkWorkersForIdleness(state);
		busycoordinator.checkForIdleness(state);
		RelevantStateView rsv = new RelevantStateView(playernum, state);
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		//while there are still commands to be found
		boolean done = outofcommands;
		
		while (!done) {
			//if you have no next goal, get one
			
			if (nextgoal==null) {
				String nextCommand = commandSourceIterator >= commandSource.length ? null:commandSource[commandSourceIterator];
				commandSourceIterator++;
				if (nextCommand == null || nextCommand.equals("")) {
					done = true;
					outofcommands = true;
				}
				else {
					nextgoal = new Goal(nextCommand,state,this);
				}
			}
			//if you now have a goal, execute it
			if (nextgoal != null) {
				//See if you can do it now
				if (nextgoal.tryExecute(state, rsv, actions))
				{
					if (verbose)
						System.out.println("Was able to do "+nextgoal.commandstring);
					nextgoal=null;
				}
				else
				{
					if (verbose)
						System.out.println("Can't do "+nextgoal.commandstring);
					done=true;
				}
				
			}
		}
		gathercoordinator.assignActions(state, rsv, actions);
		attackcoordinator.coordinate(state, actions);
		
		return actions;
		
	}
	
	/**
	 * A simple structure storing relevant details of the state view that can be modified to take other actions into account
	 * @author The Condor
	 *
	 */
	static class RelevantStateView {
//		public LinkedList<Integer> idleworkers;
//		public LinkedList<Integer> woodworkers;
//		public LinkedList<Integer> goldworkers;
//		public int notherworkers;
		public int ngold;
		public int nwood;
		public int nfoodremaining;
//		public Set<Integer> unitsWithTasks;
		public List<int[]> spacesoccupiedbynewbuildings;
		List<Integer> myUnitIDs;
		public RelevantStateView (int playernum, StateView state){
			spacesoccupiedbynewbuildings = new LinkedList<int[]>();
			ngold = state.getResourceAmount(playernum, ResourceType.GOLD);
			nwood = state.getResourceAmount(playernum, ResourceType.WOOD);
			nfoodremaining = state.getSupplyCap(playernum) - state.getSupplyAmount(playernum);
//			unitsWithTasks = new HashSet<Integer>();
//			idleworkers = new LinkedList<Integer>();
//			woodworkers = new LinkedList<Integer>();
//			goldworkers = new LinkedList<Integer>();
			myUnitIDs = state.getUnitIds(playernum);
//			for (Integer id : myUnitIDs) {
//				UnitView u = state.getUnit(id);
//					switch (u.getTask()) {
//					case Gold:
//						if (u.getTemplateView().canGather()) 
//							goldworkers.add(id);
//						break;
//					case Wood:
//						if (u.getTemplateView().canGather()) 
//							woodworkers.add(id);
//						break;
//					case Idle:
//						if (u.getTemplateView().canGather()) 
//							idleworkers.add(id);
//						break;
//					case Build:
//						unitsWithTasks.add(id);
//						if (u.getTemplateView().canGather()) 
//							notherworkers++;
//						break;
//					default:
//						if (u.getTemplateView().canGather()) 
//							notherworkers++;
//					}
//				
//			}
		}
	}

	public static String getUsage() {
		// TODO Auto-generated method stub
		return null;
	}
}
