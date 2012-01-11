package edu.cwru.SimpleRTS.environment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlState;

public final class XmlStateUtil {
	
	private XmlStateUtil() {}

	public static Collection<Integer> playerIds(XmlState xml) {
		Set<Integer> playerIds = new HashSet<Integer>();
		for(XmlPlayer player : xml.getPlayer())
		{
			playerIds.add(player.getID());
		}
		return playerIds;
	}
}
