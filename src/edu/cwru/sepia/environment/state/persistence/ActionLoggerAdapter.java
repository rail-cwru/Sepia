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

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.history.ActionLogger;
import edu.cwru.sepia.environment.model.persistence.generated.XmlActionEntry;
import edu.cwru.sepia.environment.model.persistence.generated.XmlActionList;
import edu.cwru.sepia.environment.model.persistence.generated.XmlActionLogger;

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
