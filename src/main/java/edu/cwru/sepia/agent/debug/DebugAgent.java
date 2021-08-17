package edu.cwru.sepia.agent.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

public class DebugAgent extends Agent {
	private static final Logger log = Logger.getLogger(DebugAgent.class.getCanonicalName());

	protected InputThread inputThread;

	// tracks if the game is paused
	private boolean paused = false;

	public DebugAgent(int playernum) {
		super(playernum);
		inputThread = new InputThread();
		inputThread.start();
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate, HistoryView statehistory) {
		return null;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate, HistoryView statehistory)
	{
		inputThread.newstate = newstate;
		while (paused)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				// do nothing
			}
		}

		return null;

	}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory) {

	}

	@Override
	public void savePlayerData(OutputStream os) {

	}

	@Override
	public void loadPlayerData(InputStream is) {

	}

	// resources the player ha
	public String listPlayerResources(StateView state)
	{
		int currentGold = state.getResourceAmount(0, ResourceType.GOLD);
		int currentWood = state.getResourceAmount(0, ResourceType.WOOD);
		int currentFood = state.getSupplyAmount(playernum);
		StringBuffer sb = new StringBuffer();
		sb.append("Player has:\n");
		sb.append(String.format("GOLD=%d, WOOD=%d, FOOD=%d\n", currentGold, currentWood, currentFood));
		return sb.toString();

	}

	// lists unit information
	public String listUnits(StateView state)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Player | Unit | Type |  Pos  | HP | carrying\n");
		sb.append("--------------------------------------------\n");

		// iterate through each player's  uni
		for  (int player : state.getPlayerNumbers())
		{
			List<UnitView> units = state.getUnits(player);
			for (UnitView unit : units)
			{
				sb.append(String.format("%6d | %4d | %4c | %5s | %2d | %d %s\n",
						player, unit.getID(), unit.getTemplateView().getCharacter(), String.format("%d,%d",
						unit.getXPosition(), unit.getYPosition()), unit.getHP(), unit.getCargoAmount(), unit.getCargoType()));
			}
		}
		return sb.toString();
	}

	// the number of turn
	public String getTurn(StateView state)
	{
		return "Current Turn:\n"+" " + state.getTurnNumber()+"\n";

	}

	public class InputThread extends Thread {

		StateView newstate;

		@Override
		public void run() {

			System.out.println("Press return to pause and enter command-mode.");
			System.out.println("Use the \"help\" command to list available commands.");

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try
			{
				String text = null;
				while ((text = reader.readLine()) != null)
				{
					log.log(Level.INFO, "user entered: {0}", text);
					switch(text)
					{
					case "":
						paused = true;
						// empty string; enters command-mode
						System.out.println("Paused");
						break;
					case "help":
						System.out.println("Available commands:");
						System.out.println("  continue");
						System.out.println("  help");
						System.out.println("  listplayerresources");
						System.out.println("  listunits");
						break;
					case "continue":
						paused = false;
						break;
					case "listplayerresources":
						System.out.println(listPlayerResources(newstate));
						break;
					case "listunits":
						System.out.println(listUnits(newstate));
						break;
					default:
						System.out.println("unknown command");
					}
				}
			}
			catch (IOException e)
			{
				log.log(Level.WARNING, "Exception on input thread", e);
			}
		}

	}
}
