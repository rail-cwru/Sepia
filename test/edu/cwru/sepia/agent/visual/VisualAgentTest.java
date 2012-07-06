/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.agent.visual;

import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.SimpleAgent1;
import edu.cwru.sepia.agent.visual.VisualAgent;
import edu.cwru.sepia.environment.Environment;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.State.StateBuilder;
import edu.cwru.sepia.model.SimpleModel;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.resource.ResourceNode;
import edu.cwru.sepia.model.unit.Unit;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.util.TypeLoader;

public class VisualAgentTest {

	static State state;
	static SimpleModel model;
	static VisualAgent visualAgent;
	static SimpleAgent1 simpleAgent;
	static Environment env;
	private static final int player1=0;
	private static final int player2=1;
	
	@BeforeClass
	public static void setup() throws JSONException, IOException {
		StateBuilder builder = new StateBuilder();
		state = builder.build();
		builder.setSize(32, 32);
		{
			List<Template<?>> templates = TypeLoader.loadFromFile("data/unit_templates",player1,state);
			for(Template<?> t : templates)
			{
				builder.addTemplate(t);
			}
		}
		{
			List<Template<?>> templates = TypeLoader.loadFromFile("data/unit_templates",player2,state);
			for(Template<?> t : templates)
			{
				builder.addTemplate(t);
			}
		}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player1, "Peasant");
				Unit u1 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u1,1,1);
				Unit u2 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u2,7,7);
			}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player2, "Footman");
				Unit u1 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u1,20,4);
			}
			
			{
				UnitTemplate ut = (UnitTemplate) builder.getTemplate(player2, "Archer");
				Unit u1 = new Unit(ut,state.nextTargetID());
				builder.addUnit(u1,2,12);
			}
		
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 1, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 1, 2, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 2, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 3, 3, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 0, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 1, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 2, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 3, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 4, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 5, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 6, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 7, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 8, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.TREE, 9, 5, 100,state.nextTargetID()));
		builder.addResource(new ResourceNode(ResourceNode.Type.GOLD_MINE, 12, 2, 100,state.nextTargetID()));
		model = new SimpleModel(state, 6,null);
		model.setVerbose(true);
		visualAgent = new VisualAgent(player1,new String[]{"false","true"});
		simpleAgent = new SimpleAgent1(player2);
		env = new Environment(new Agent[]{visualAgent,simpleAgent}, model, 123456);
	}
	@Test
	public void display() {
		while(true);
	}
	public static void main(String args[]) {
	      org.junit.runner.JUnitCore.main("edu.cwru.sepia.agent.visual.VisualAgentTest");
	}
}
