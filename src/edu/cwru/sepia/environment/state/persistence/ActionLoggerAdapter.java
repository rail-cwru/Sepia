package edu.cwru.sepia.environment.state.persistence;
import java.util.List;
import java.util.Map.Entry;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.state.persistence.generated.XmlAction;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionEntry;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionList;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionLogger;
import edu.cwru.sepia.log.ActionLogger;

public class ActionLoggerAdapter {
	public static XmlActionLogger toXml(ActionLogger actionLogger) {
		XmlActionLogger toReturn = new XmlActionLogger();
		int maxRound = actionLogger.getHighestRound();
		for (int roundNumber = 0; roundNumber<=maxRound; roundNumber++)
		{
			XmlActionList xmlSingleTurn = new XmlActionList();
			xmlSingleTurn.setRoundNumber(roundNumber);
			for (Entry<Integer,Action> single : actionLogger.getActions(roundNumber).entrySet())
			{
				XmlActionEntry xmlEntry = new XmlActionEntry();
				xmlEntry.setUnitID(single.getKey());
				xmlEntry.setAction(ActionAdapter.toXml(single.getValue()));
				xmlSingleTurn.getActionEntry().add(xmlEntry);
			}
			toReturn.getActionList().add(xmlSingleTurn);
		}
		return toReturn;
	}
	
	public static ActionLogger fromXml(XmlActionLogger xml) {
		ActionLogger toReturn = new ActionLogger();
		List<XmlActionList> actions = xml.getActionList();
		for (XmlActionList xmlSingleTurn : actions)
		{
			int roundNumber = xmlSingleTurn.getRoundNumber();
			for (XmlActionEntry xmlSingle : xmlSingleTurn.getActionEntry())
			{
				
				Action action = ActionAdapter.fromXml(xmlSingle.getAction());
				toReturn.addAction(roundNumber, xmlSingle.getUnitID(), action);
			}
		}
		return toReturn;
	}
}
