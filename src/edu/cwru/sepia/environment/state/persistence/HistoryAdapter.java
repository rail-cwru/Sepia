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

public class HistoryAdapter {

		
	public static XmlHistory toXml(History history) {
		XmlHistory xml =new XmlHistory();
		xml.setFogOfWar(history.hasFogOfWar());
		
		for (PlayerHistory ph : history.getPlayerHistories()) {
			xml.getPlayerHistories().add(PlayerHistoryAdapter.toXml(ph));
		}
		xml.setObserverHistory(PlayerHistoryAdapter.toXml(history.getObserverHistory()));
		return xml;
	}
	
	public static History fromXml(XmlHistory xml) {
		History h=new History();
		h.setFogOfWar(xml.isFogOfWar());
		for (XmlPlayerHistory xph : xml.getPlayerHistories())
		{
			PlayerHistory ph = PlayerHistoryAdapter.fromXml(xph);
			h.setPlayerHistory(ph);
		}
		h.setObserverHistory(PlayerHistoryAdapter.fromXml(xml.getObserverHistory()));
		return h;
	}

}
