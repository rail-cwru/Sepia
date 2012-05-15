package edu.cwru.SimpleRTS.environment.state.persistence;

import java.util.List;

import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResult;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResultList;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResultLogger;
import edu.cwru.SimpleRTS.log.ActionResultLogger;

public class ActionResultLoggerAdapter {
	public static XmlActionResultLogger toXml(ActionResultLogger actionResultLogger) {
		XmlActionResultLogger toReturn = new XmlActionResultLogger();
		int maxRound = actionResultLogger.getHighestRound();
		for (int roundNumber = 0; roundNumber<=maxRound; roundNumber++)
		{
			XmlActionResultList xmlSingleTurn = new XmlActionResultList();
			xmlSingleTurn.setRoundNumber(roundNumber);
			for (ActionResult singleResult : actionResultLogger.getActionResults(roundNumber))
			{
				xmlSingleTurn.getActionResult().add(ActionResultAdapter.toXml(singleResult));
			}
			toReturn.getActionResultList().add(xmlSingleTurn);
		}
		return toReturn;
	}
	
	public static ActionResultLogger fromXml(XmlActionResultLogger xml) {
		ActionResultLogger toReturn = new ActionResultLogger();
		List<XmlActionResultList> actionResults = xml.getActionResultList();
		for (XmlActionResultList xmlSingleTurn : actionResults)
		{
			int roundNumber = xmlSingleTurn.getRoundNumber();
			for (XmlActionResult xmlSingleResult : xmlSingleTurn.getActionResult())
			{
				toReturn.addActionResult(roundNumber, ActionResultAdapter.fromXml(xmlSingleResult));
			}
		}
		return toReturn;
	}
}
