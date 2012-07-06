package edu.cwru.sepia.environment.state.persistence;

import edu.cwru.sepia.environment.History;
import edu.cwru.sepia.environment.PlayerHistory;
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
