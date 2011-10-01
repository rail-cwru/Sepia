package edu.cwru.SimpleRTS.util;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

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
		boolean done = false;
		int iterator = -1;
		while (!done)
		{
			String[] nextcommand;
			if (!usestdin && ++iterator < commands.length) {
				nextcommand = commands[iterator].split(" ");
			}
			else {
				nextcommand = reader.readLine().split(" ");
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
							addPlayer(s, player, templatefile);
						Template template =  s.getTemplate(player, unitname);
						if (template!=null && template instanceof UnitTemplate)
						{
							
							Unit u = new Unit((UnitTemplate)template);
							//u.setPlayer(player);
							u.setxPosition(x);
							u.setyPosition(y);
							s.addUnit(u);
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
							ResourceNode r = new ResourceNode(ResourceNode.Type.valueOf(nextcommand[1].toUpperCase()),x,y,Integer.parseInt(nextcommand[4]));
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
		GameMap g = new GameMap(s.build());
		GameMap.storeMap(outputfile, g);
	}
	static void addPlayer(State.StateBuilder state, int player, String templatefile) {
		List<Template> templates=null;
		try {
			templates = TypeLoader.loadFromFile(templatefile,player);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Template t : templates)
			state.addTemplate(t, player);
	}
}
