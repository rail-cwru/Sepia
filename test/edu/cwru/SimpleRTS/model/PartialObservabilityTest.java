package edu.cwru.SimpleRTS.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.json.JSONException;
import org.junit.Test;

import edu.cwru.SimpleRTS.Log.EventLogger;
import edu.cwru.SimpleRTS.Log.EventLogger.EventLoggerView;
import edu.cwru.SimpleRTS.Log.RevealedResourceLog;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.DirectedAction;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.ResourceView;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class PartialObservabilityTest {
  
	@Test
	public void checkReveal()
	{
		int player = 0;
		Random r = new Random();
		State state = new State();
		state.setSize(40, 40);
		state.addPlayer(player);
		boolean correctrevealedness=r.nextBoolean();
		state.setRevealedResources(correctrevealedness);
		
		EventLoggerView e = state.getView(player).getEventLog();
		EventLoggerView obs = state.getView(Agent.OBSERVER_ID).getEventLog();
		Map<Pair,Pair> actualpositioning=new HashMap<Pair,Pair>();
		//Repeatedly add things, changing the revealedness every so often 
		for (int i = 0; i<100;i++)
		{
			ResourceNode newres = new ResourceNode(r.nextBoolean()?ResourceNode.Type.GOLD_MINE:ResourceNode.Type.TREE,r.nextInt(state.getXExtent()),r.nextInt(state.getYExtent()),r.nextInt());
			state.addResource(newres);
			Pair pos = new Pair(newres.getxPosition(), newres.getyPosition());
			Pair prevnum = actualpositioning.get(pos);
			
			int nummine = newres.getType()==ResourceNode.Type.GOLD_MINE?1:0;
			int numtree = newres.getType()==ResourceNode.Type.TREE?1:0;
			if (prevnum!=null)
			{	
				nummine+=prevnum.i1;
				numtree+=prevnum.i2;
			}
			actualpositioning.put(pos, new Pair(nummine,numtree));
			revealedStatusChecker(i, e,actualpositioning,correctrevealedness);
			revealedStatusChecker(i, obs,actualpositioning,correctrevealedness);
			correctrevealedness = r.nextDouble() < 0.9?correctrevealedness:!correctrevealedness;
			state.setRevealedResources(correctrevealedness);
		}
	}
	
	
@SuppressWarnings("rawtypes")
@Test
/**
 * Do a long random walk and check the sight ranges to see if they are right
 * Also check that the getUnit and getResourceNode match the sight
 * Check that the getAllUnit and getAllResource match the sight/get 
 */
public void sightTest() throws FileNotFoundException, JSONException {
	//Set up the state
	
	int player = 0;
	int otherplayer = 1;
	List<Template> templates = TypeLoader.loadFromFile("data/unit_templates",player);
	List<Template> templates2 = TypeLoader.loadFromFile("data/unit_templates",otherplayer);
	
	State state=new State();
	state.setSize(20, 20);
	ResourceNode[][] nodegrid = new ResourceNode[state.getXExtent()][state.getYExtent()];
	Unit[][] unitgrid = new Unit[state.getXExtent()][state.getYExtent()];
	for (Template t : templates)
		state.addTemplate(t);
	for (Template t2 : templates2)
		state.addTemplate(t2);
	UnitTemplate template = ((UnitTemplate)state.getTemplate(player, "Footman"));
	UnitTemplate enemytemplate = ((UnitTemplate)state.getTemplate(otherplayer, "Footman"));
	Unit u = new Unit(template);
	state.addUnit(u, 0, 0);
	
	for (int i = 0; i < state.getXExtent(); i++)
	{
		for (int j = 0; j < state.getYExtent(); j++)
		{
			nodegrid[i][j]=new ResourceNode(ResourceNode.Type.GOLD_MINE,i,j,2344);
			unitgrid[i][j]=new Unit(enemytemplate);
			state.addUnit(unitgrid[i][j], i, j);
			state.addResource(nodegrid[i][j]);
		}
	}
	
	Random r = new Random();
	int numsteps = 5000;
	StateView view = state.getView(player);
	
	for (int n = 0;n<numsteps;n++)
	{
		
		Direction d = Direction.values()[r.nextInt(Direction.values().length)];
		if (state.inBounds(u.getxPosition()+d.xComponent(), u.getyPosition()+d.yComponent()))
			state.moveUnit(u, d);
		
		System.out.println("Step: "+n);
		System.out.println(printView(u.getxPosition(), u.getyPosition(), view));
		List<Integer> allresources = view.getAllResourceIds();
		List<Integer> allunits = view.getAllUnitIds();
		for (int i = 0; i<state.getXExtent(); i++)
			for (int j = 0; j<state.getYExtent(); j++)
			{
				assertTrue("Can't see properly in fully observable case",view.canSee(i, j) == true);
				
			}
		for (int i = 0; i<unitgrid.length;i++)
			for (int j = 0; j<unitgrid[i].length;j++)
				assertTrue(view.getUnit(unitgrid[i][j].ID).getID() == unitgrid[i][j].ID);//view.canSee(unitgrid[i][j].getxPosition(),unitgrid[i][j].getyPosition()))
		for (int i = 0; i<nodegrid.length;i++)
			for (int j = 0; j<nodegrid[i].length;j++)
				assertTrue(view.getResourceNode(nodegrid[i][j].ID).getID() == nodegrid[i][j].ID);//view.canSee(unitgrid[i][j].getxPosition(),unitgrid[i][j].getyPosition()))
		//check that you can actually get (and by the previous tests, see) all of the ids you are given
		for (Integer id : allunits)
		{
			UnitView unit = view.getUnit(id);
			assertTrue("Couldn't get unit that was listed in getAllUnits",unit!=null);
			assertTrue("It got the wrong unit (this is odd and bad beyond the partial observability)",unit.getID() == id);
		}
		for (Integer id : allresources)
		{
			ResourceView resource = view.getResourceNode(id);
			assertTrue("Couldn't get unit that was listed in getAllUnits",resource!=null);
			assertTrue("It got the wrong unit (this is odd and bad beyond the partial observability)",resource.getID() == id);
		}
		//check that you are given all the ones that you can see
		for (int i = 0; i < unitgrid.length; i++)
			for (int j = 0; j < unitgrid[i].length; j++)
			{
				int x = unitgrid[i][j].getxPosition();
				int y = unitgrid[i][j].getyPosition();
				boolean cansee = view.canSee(x, y);
				assertTrue(cansee == allunits.contains(unitgrid[i][j].ID) );
			}
		for (int i = 0; i < nodegrid.length; i++)
			for (int j = 0; j < nodegrid[i].length; j++)
			{
				int x = nodegrid[i][j].getxPosition();
				int y = nodegrid[i][j].getyPosition();
				//x and y should just be i and j
				boolean cansee = view.canSee(x, y);
				assertTrue(cansee == allresources.contains(nodegrid[i][j].ID) );
			}
		
		//Check the unitat and resourceat abilities
		//Note that you put something at each position
		for (int i = 0; i<view.getXExtent(); i++)
			for (int j = 0; j<view.getYExtent(); j++)
			{
				boolean cansee = view.canSee(i, j);
				boolean seeunitthere = view.unitAt(i, j)!=null;
				boolean seeresourcethere = view.resourceAt(i, j)!=null;
				assertTrue(cansee == seeunitthere);
				assertTrue(cansee == seeresourcethere);
			}
	}
	
	state.setFogOfWar(true);
	for (int n = 0;n<numsteps;n++)
	{
		
		
		Direction d = Direction.values()[r.nextInt(Direction.values().length)];
		if (state.inBounds(u.getxPosition()+d.xComponent(), u.getyPosition()+d.yComponent()))
			state.moveUnit(u, d);
		
		
		System.out.println("Step: "+n);
		System.out.println(printView(u.getxPosition(), u.getyPosition(), view));
		List<Integer> allresources = view.getAllResourceIds();
		List<Integer> allunits = view.getAllUnitIds();
		for (int i = 0; i<state.getXExtent(); i++)
			for (int j = 0; j<state.getYExtent(); j++)
			{
				boolean cansee = view.canSee(i, j);
				boolean inrange = DistanceMetrics.chebyshevDistance(i, j, u.getxPosition(), u.getyPosition())<=u.getTemplate().getSightRange();
				assertTrue("Step "+n+":"+(cansee?"Can":"Can't") + " see "+i+","+j+", but it "+(inrange?"is":"isn't")+" in range "+DistanceMetrics.chebyshevDistance(i, j, u.getxPosition(), u.getyPosition())+" away",cansee == inrange);
			}
		
		
		for (int i = 0; i<unitgrid.length;i++)
			for (int j = 0; j<unitgrid[i].length;j++)
			{
				boolean cansee = view.canSee(unitgrid[i][j].getxPosition(),unitgrid[i][j].getyPosition());
				UnitView unitseen = view.getUnit(unitgrid[i][j].ID);
				Integer idseen = unitseen==null?null:unitseen.getID();
				assertTrue(idseen == null && !cansee || idseen == unitgrid[i][j].ID && cansee);
			}
		for (int i = 0; i<nodegrid.length;i++)
			for (int j = 0; j<nodegrid[i].length;j++)
			{
				boolean cansee = view.canSee(nodegrid[i][j].getxPosition(),nodegrid[i][j].getyPosition());
				ResourceView nodeseen = view.getResourceNode(nodegrid[i][j].ID);
				Integer idseen = nodeseen==null?null:nodeseen.getID();
				assertTrue(idseen == null && !cansee || idseen == nodegrid[i][j].ID && cansee);
			}
		//check that you can actually get (and by the previous tests, see) all of the ids you are given
		for (Integer id : allunits)
		{
			UnitView unit = view.getUnit(id);
			assertTrue("Couldn't get unit that was listed in getAllUnits",unit!=null);
			assertTrue("It got the wrong unit (this is odd and bad beyond the partial observability)",unit.getID() == id);
		}
		for (Integer id : allresources)
		{
			ResourceView resource = view.getResourceNode(id);
			assertTrue("Couldn't get resource that was listed in getAllResources",resource!=null);
			assertTrue("It got the wrong resource (this is odd and bad beyond the partial observability)",resource.getID() == id);
		}
		//check that you are given all the ones that you can see
		for (int i = 0; i < unitgrid.length; i++)
			for (int j = 0; j < unitgrid[i].length; j++)
			{
				int x = unitgrid[i][j].getxPosition();
				int y = unitgrid[i][j].getyPosition();
				boolean cansee = view.canSee(x, y);
				assertTrue(cansee == allunits.contains(unitgrid[i][j].ID) );
			}
		for (int i = 0; i < nodegrid.length; i++)
			for (int j = 0; j < nodegrid[i].length; j++)
			{
				int x = nodegrid[i][j].getxPosition();
				int y = nodegrid[i][j].getyPosition();
				//x and y should just be i and j
				boolean cansee = view.canSee(x, y);
				assertTrue(cansee == allresources.contains(nodegrid[i][j].ID) );
			}
		
		//Check the unitat and resourceat abilities
		//Note that you put something at each position
		for (int i = 0; i<view.getXExtent(); i++)
			for (int j = 0; j<view.getYExtent(); j++)
			{
				boolean cansee = view.canSee(i, j);
				boolean seeunitthere = view.unitAt(i, j)!=null;
				boolean seeresourcethere = view.resourceAt(i, j)!=null;
				assertTrue(cansee == seeunitthere);
				assertTrue(cansee == seeresourcethere);
			}
		
	}
	
}
public String printView(int unitx, int unity,StateView v)
{
	String s="";
	for (int i = 0; i<v.getXExtent();i++)
	{
		for (int j = 0; j<v.getYExtent();j++)
		{
			s+=(i==unitx&&j==unity)?"|u":v.canSee(i, j)?"|x":"| ";
			
		}
		s+="|\n";
	}
	return s;
}

/**
 * A repeated call from checkReveal
 */
private void revealedStatusChecker(int step, EventLoggerView e, Map<Pair,Pair> actualpositioning, boolean shouldberevealed)
{
	System.out.println("Step "+step + ": " + (shouldberevealed?"revealed":"hidden"));
	List<RevealedResourceLog> revealedResources= e.getRevealedResources();
	if (!shouldberevealed)
	{
		assertTrue("Step " + step + ": Resources were revealed when they should have been hidden",revealedResources.size()==0);
	}
	else //They should be revealed
	{
		Map<Pair, Pair> revealedResourcePositioning=new HashMap<Pair, Pair>();;
		//Make it a map
		for (RevealedResourceLog log : revealedResources)
		{
			Pair pos = new Pair(log.getResourceNodeXPosition(), log.getResourceNodeYPosition());
			int numgoldalreadythere = 0;
			int numtreealreadythere = 0;
			if (revealedResourcePositioning.containsKey(pos))
			{
				Pair alreadythere = revealedResourcePositioning.get(pos);
				numgoldalreadythere = alreadythere.i1;
				numtreealreadythere = alreadythere.i2;
			}
			if (log.getResourceNodeType()==ResourceNode.Type.GOLD_MINE)
				numgoldalreadythere++;
			else if (log.getResourceNodeType()==ResourceNode.Type.TREE)
				numtreealreadythere++;
			revealedResourcePositioning.put(pos,new Pair(numgoldalreadythere,numtreealreadythere));
		}
		System.out.println("Actual:");
		System.out.println(actualpositioning);
		System.out.println("Seen:");
		System.out.println(revealedResourcePositioning);
		//Check to see that they are the same
		for (Entry<Pair,Pair> real : actualpositioning.entrySet())
		{
			Pair seen = revealedResourcePositioning.get(real.getKey());
			assertTrue("Step " + step + "Less something seen than there are",seen != null);
			assertTrue("Step " + step + "More gold mines seen than there are",seen.i1 >= real.getValue().i1);
			assertTrue("Step " + step + "Less gold mines seen than there are",seen.i1 <= real.getValue().i1);
			assertTrue("Step " + step + "More gold mines seen than there are",seen.i2 >= real.getValue().i2);
			assertTrue("Step " + step + "Less gold mines seen than there are",seen.i2 <= real.getValue().i2);
		}
		for (Entry<Pair,Pair> seen : revealedResourcePositioning.entrySet())
		{
			Pair real = actualpositioning.get(seen.getKey());
			assertTrue("Step " + step + "More something seen than there are",real != null);
		}
	}
}
//need to test adding and removing units
//old unit view isn't updated between events
//same with templates and resources
//check some of the boolean things like occupied
}
class Pair
{
	public final int i1;
	public final int i2;
	public Pair(int i1, int i2)
	{
		this.i1=i1;
		this.i2=i2;
	}
	public int hashCode()
	{
		return i1+i2 + i1*i2;
	}
	public boolean equals(Object other)
	{
		if (!other.getClass().equals(this.getClass()))
			return false;
		Pair pairother = (Pair)other;
		return pairother.i1 == i1 && pairother.i2 == i2;
	}
	public String toString()
	{
		return i1 + " " + i2;
	}
}