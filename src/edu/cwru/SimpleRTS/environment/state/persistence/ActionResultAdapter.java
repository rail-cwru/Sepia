package edu.cwru.SimpleRTS.environment.state.persistence;

import edu.cwru.SimpleRTS.action.ActionResult;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResult;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlActionResultLogger;
import edu.cwru.SimpleRTS.log.ActionResultLogger;

public class ActionResultAdapter {
	public static XmlActionResult toXml(ActionResult actionResult) {
		XmlActionResult toReturn = new XmlActionResult();
		toReturn.setAction(ActionAdapter.toXml(actionResult.getAction()));
		toReturn.setFeedback(actionResult.getFeedback());
		return toReturn;
	}
	
	public static ActionResult fromXml(XmlActionResult xml) {
		return new ActionResult(ActionAdapter.fromXml(xml.getAction()), xml.getFeedback()); 
	}
}
