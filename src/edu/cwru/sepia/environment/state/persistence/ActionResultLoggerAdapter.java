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
package edu.cwru.sepia.environment.state.persistence;

import java.util.List;
import java.util.Map.Entry;

import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionResult;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionResultEntry;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionResultList;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionResultLogger;
import edu.cwru.sepia.log.ActionResultLogger;

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
