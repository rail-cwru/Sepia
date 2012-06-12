package edu.cwru.SimpleRTS.agent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.agent.CombatAgent;
import edu.cwru.SimpleRTS.environment.History.HistoryView;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
/**
 * Takes advantage of a hole in the structure circa revision 270 to turn footmen into unstoppable killing machines 
 * @author The Condor
 *
 */
public class CheatyTestAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CombatAgent slave;
	
	public CheatyTestAgent(int playernum, String[] notused) {
		super(playernum);
		slave = new CombatAgent(playernum, new String[]{"1","true","false"});
	}
	public CheatyTestAgent(int playernum) {
		super(playernum);
		slave = new CombatAgent(playernum, new String[]{"1","true","false"});
	}
	@Override
	public Map<Integer, Action> initialStep(StateView newstate, HistoryView statehistory) {
		newstate.getClosestOpenPosition(1, 1);
		// TODO Auto-generated method stub
		try {
//			newstate.getClass().getDeclaredField("this$0").setAccessible(true);
			
			System.out.println(Arrays.toString(newstate.getClass().getDeclaredFields()));
			System.out.println(newstate.getClass().getDeclaredField("state").isAccessible());
			Field field = newstate.getClass().getDeclaredField("state");
			field.setAccessible(true);
			System.out.println(field.isAccessible());
			edu.cwru.SimpleRTS.environment.State state = (edu.cwru.SimpleRTS.environment.State)field.get(newstate);
			UnitTemplate template = (UnitTemplate)state.getTemplate(playernum, "Footman");
			template.setArmor(100);
			template.setCharacter((char)0x2042);//0x2605
			template.setName("Terminator");
			template.setBaseHealth(100);
			template.setPiercingAttack(100);
//			template.setSightRange(100); //doing this would cause a bug, sight range changes mess things up
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return slave.initialStep(newstate, statehistory);
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate, HistoryView statehistory) {
		return slave.initialStep(newstate, statehistory);
	}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory) {

	}
	public static String getUsage() {
		return "What is going on";
	}

}
