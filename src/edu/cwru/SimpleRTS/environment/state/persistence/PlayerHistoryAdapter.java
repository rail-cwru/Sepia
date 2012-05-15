package edu.cwru.SimpleRTS.environment.state.persistence;

import edu.cwru.SimpleRTS.environment.History;
import edu.cwru.SimpleRTS.environment.PlayerHistory;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlHistory;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayerHistory;

public class PlayerHistoryAdapter {
	public static XmlPlayerHistory toXml(PlayerHistory history) {
		XmlPlayerHistory xml = new XmlPlayerHistory();
		xml.setCommandResults(ActionResultLoggerAdapter.toXml(history.getActionProgress()));
		xml.setPrimitivesExecuted(ActionLoggerAdapter.toXml(history.getActionsExecuted()));
		xml.setCommandsIssued(ActionLoggerAdapter.toXml(history.getCommandsIssued()));
		xml.setEventLogger(EventLoggerAdapter.toXml(history.getEventLogger()));
		xml.setPlayerNumber(history.getPlayerNumber());
		return xml;
	}
	
	public static PlayerHistory fromXml(XmlPlayerHistory xml) {
		PlayerHistory ph = new PlayerHistory(xml.getPlayerNumber());
		ph.setActionProgress(ActionResultLoggerAdapter.fromXml(xml.getCommandResults()));
		ph.setActionsExecuted(ActionLoggerAdapter.fromXml(xml.getPrimitivesExecuted()));
		ph.setCommandsIssued(ActionLoggerAdapter.fromXml(xml.getCommandsIssued()));
		ph.setEventLogger(EventLoggerAdapter.fromXml(xml.getEventLogger()));
		return ph;
	}
}
