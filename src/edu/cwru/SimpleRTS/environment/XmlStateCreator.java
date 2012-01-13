package edu.cwru.SimpleRTS.environment;

import java.util.Map;

import edu.cwru.SimpleRTS.environment.state.persistence.StateAdapter;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlState;
import edu.cwru.SimpleRTS.model.Template;

public class XmlStateCreator implements StateCreator {
	private static final long serialVersionUID = 1L;
	
	private XmlState state;
	@SuppressWarnings("rawtypes")
	private Map<Integer,Map<Integer,Template>> templates;
	private StateAdapter adapter;
	
	public XmlStateCreator(XmlState state, @SuppressWarnings("rawtypes") Map<Integer,Map<Integer,Template>> templates) {
		this.state = state;
		this.templates = templates;
		adapter = new StateAdapter();
	}
	
	@Override
	public State createState() {		
		try
		{
			return adapter.fromXml(state, templates);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

}
