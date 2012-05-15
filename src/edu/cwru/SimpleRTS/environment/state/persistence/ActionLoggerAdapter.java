package edu.cwru.SimpleRTS.environment.state.persistence;

import java.util.List;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlAction;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionList;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionLogger;
import edu.cwru.SimpleRTS.log.ActionLogger;

public class ActionLoggerAdapter {
	public static XmlActionLogger toXml(ActionLogger actionLogger) {
		XmlActionLogger toReturn = new XmlActionLogger();
		int maxRound = actionLogger.getHighestRound();
		for (int roundNumber = 0; roundNumber<=maxRound; roundNumber++)
		{
			XmlActionList xmlSingleTurn = new XmlActionList();
			xmlSingleTurn.setRoundNumber(roundNumber);
			for (Action single : actionLogger.getActions(roundNumber))
			{
				xmlSingleTurn.getAction().add(ActionAdapter.toXml(single));
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
			for (XmlAction xmlSingleAction : xmlSingleTurn.getAction())
			{
				toReturn.addAction(roundNumber, ActionAdapter.fromXml(xmlSingleAction));
			}
		}
		return toReturn;
	}
}
