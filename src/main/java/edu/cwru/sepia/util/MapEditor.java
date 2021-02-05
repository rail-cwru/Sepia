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
package edu.cwru.sepia.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.json.JSONException;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;

public class MapEditor {
	public static void main(String[] args) throws IOException, JSONException {
		 
		System.out.println("Usage: MapEditor inputfile outputfile commands");
		System.out.println("\tcommands is optional and semicolon seperated");
		if(args.length < 2)
			return;
		boolean usestdin = false;
		String templatefile = args[0];
		
		String outputfile = args[1];
		String[] commands = null;
		BufferedReader reader = null;
		boolean alreadysetsize = false;
		if (args.length <= 2) {
			usestdin = true;
			reader = new BufferedReader(new InputStreamReader(System.in));
		}
		else {
			usestdin = false;
			commands = args[2].split(";");
		}
		State.StateBuilder s = new State.StateBuilder();
		State state = s.build();
		boolean done = false;
		int iterator = -1;
		while (!done)
		{
			String[] nextcommand;
			if (!usestdin && ++iterator < commands.length) {
				nextcommand = commands[iterator].split(" ");
			}
			else {
				String line = reader.readLine();
				nextcommand = line != null ? line.split(" ") : new String[]{""};
			}
			
			if (nextcommand.length == 1 && (nextcommand[0].equals("q") || nextcommand[0].equals("quit"))) {
				done=true;
			}
			else if (nextcommand.length == 1 && (nextcommand[0].equals("h") || nextcommand[0].equals("help"))) {
				System.out.println("Help for MapEditor");
				System.out.println("\tq or quit to quit");
				System.out.println("\taddUnit unitname x y player");
				System.out.println("\taddResource resourcetype x y amount");
				System.out.println("\tsetSize xsize ysize");
				System.out.println();
			}
			else if (nextcommand.length == 5 && nextcommand[0].equals("addUnit")) {
				if (alreadysetsize) {
					int x;
					int y;
					int player;
					try {
						x = Integer.parseInt(nextcommand[2]);
						y = Integer.parseInt(nextcommand[3]);
						player = Integer.parseInt(nextcommand[4]);
					}
					catch(Exception ex) {
						System.out.println("Unable to parse command arguments.");
						continue;
					}
					if (s.positionAvailable(x,y))
					{
						String unitname = nextcommand[1];
						if (!s.hasTemplates(player));
							addPlayer(state, player, templatefile);
						Template<?> template =  s.getTemplate(player, unitname);
						if (template!=null && template instanceof UnitTemplate)
						{
							
							Unit u = new Unit((UnitTemplate)template,state.nextTargetID());
							//u.setPlayer(player);
							s.addUnit(u,x,y);
						}
					}
					else
						System.out.println("Position {"+x +"," + y + "}already taken");
				}
				else
					System.out.println("Must set size before placing objects");
			}
			else if (nextcommand.length == 3 && nextcommand[0].equals("setSize")){
				if (!alreadysetsize)
				{
					s.setSize(Integer.parseInt(nextcommand[1]),Integer.parseInt(nextcommand[2]));
					alreadysetsize=true;
				}
				else
					System.out.println("Size already set, cannot reset it"); //Because it might otherwise strand units outside the map
			}
			else if (nextcommand.length == 5 && nextcommand[0].equals("addResource")) {
				try {
					int x = Integer.parseInt(nextcommand[2]);
					int y = Integer.parseInt(nextcommand[3]);
					if (alreadysetsize){
						if (s.positionAvailable(x,y)) {
							ResourceNode r = new ResourceNode(ResourceNode.Type.valueOf(nextcommand[1].toUpperCase()),x,y,Integer.parseInt(nextcommand[4]),state.nextTargetID());
							s.addResource(r);
						}
						else
							System.out.println("Position {"+x +"," + y + "}already taken");
					}
					else
						System.out.println("Must set size before placing objects");
				}
				catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
			System.out.println(s.getTextString());
		}
		GameMap.storeState(outputfile, state);
	}
	static void addPlayer(State state, int player, String templatefile) {
		List<Template<?>> templates=null;
		try {
			templates = TypeLoader.loadFromFile(templatefile,player,state);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(templates != null)
			for (Template<?> t : templates)
				state.addTemplate(t);
	}
}
