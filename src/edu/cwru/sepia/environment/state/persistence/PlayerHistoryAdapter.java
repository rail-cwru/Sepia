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

import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.PlayerHistory;
import edu.cwru.sepia.environment.state.persistence.generated.XmlHistory;
import edu.cwru.sepia.environment.state.persistence.generated.XmlPlayerHistory;

public class PlayerHistoryAdapter {
	public static XmlPlayerHistory toXml(PlayerHistory history) {
		XmlPlayerHistory xml = new XmlPlayerHistory();
		xml.setCommandFeedback(ActionResultLoggerAdapter.toXml(history.getCommandFeedback()));
		xml.setPrimitiveFeedback(ActionResultLoggerAdapter.toXml(history.getPrimitiveFeedback()));
		xml.setCommandsIssued(ActionLoggerAdapter.toXml(history.getCommandsIssued()));
		xml.setEventLogger(EventLoggerAdapter.toXml(history.getEventLogger()));
		xml.setPlayerNumber(history.getPlayerNumber());
		return xml;
	}
	
	public static PlayerHistory fromXml(XmlPlayerHistory xml) {
		PlayerHistory ph = new PlayerHistory(xml.getPlayerNumber());
		ph.setCommandFeedback(ActionResultLoggerAdapter.fromXml(xml.getCommandFeedback()));
		ph.setPrimitivesExecuted(ActionResultLoggerAdapter.fromXml(xml.getPrimitiveFeedback()));
		ph.setCommandsIssued(ActionLoggerAdapter.fromXml(xml.getCommandsIssued()));
		ph.setEventLogger(EventLoggerAdapter.fromXml(xml.getEventLogger()));
		return ph;
	}
}
