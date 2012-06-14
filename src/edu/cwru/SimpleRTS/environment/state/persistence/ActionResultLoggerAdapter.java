package edu.cwru.SimpleRTS.environment.state.persistence;

import java.util.List;
import java.util.Map.Entry;

import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResult;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResultEntry;
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
			for (Entry<Integer,ActionResult> singleResult : actionResultLogger.getActionResults(roundNumber).entrySet())
			{
				XmlActionResultEntry xmlEntry = new XmlActionResultEntry();
				xmlEntry.setUnitID(singleResult.getKey());
				xmlEntry.setActionResult(ActionResultAdapter.toXml(singleResult.getValue()));
				xmlSingleTurn.getActionResultEntry().add(xmlEntry);
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
			for (XmlActionResultEntry xmlSingleResult : xmlSingleTurn.getActionResultEntry())
			{
				
				ActionResult actionResult = ActionResultAdapter.fromXml(xmlSingleResult.getActionResult());
				toReturn.addActionResult(roundNumber, xmlSingleResult.getUnitID(), actionResult);
			}
		}
		return toReturn;
	}
}
