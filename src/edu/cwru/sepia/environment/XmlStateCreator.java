package edu.cwru.sepia.environment;

import edu.cwru.sepia.environment.state.persistence.StateAdapter;
import edu.cwru.sepia.environment.state.persistence.generated.XmlState;

public class XmlStateCreator implements StateCreator {
	private static final long serialVersionUID = 1L;
	
	private XmlState state;
	private StateAdapter adapter;
	
	public XmlStateCreator(XmlState state) {
		this.state = state;
		adapter = new StateAdapter();
	}
	
	@Override
	public State createState() {		
		try
		{
			return adapter.fromXml(state);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

}
