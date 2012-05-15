package edu.cwru.SimpleRTS.environment.state.persistence;

import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.PlayerHistory;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlHistory;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayerHistory;

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
