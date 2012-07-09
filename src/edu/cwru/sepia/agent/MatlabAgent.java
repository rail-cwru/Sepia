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
package edu.cwru.sepia.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State.StateView;

/**
 * This is a wrapper class for calling agent implemented in Matlab.
 * @author Feng
 *
 */
public class MatlabAgent extends Agent {

	private static final long serialVersionUID = -1819028685122969080L;
	
	MatlabProxy m_proxy;
	
	public MatlabAgent(int playernum) {
		super(playernum);
		initMatlabCon();
		try {
			m_proxy.feval("agent_init", playernum);
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
	}
	
	protected void initMatlabCon() {
		//Set options for matlab proxy factory
        MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).build();

        //Create a proxy, which we will use to control MATLAB
        MatlabProxyFactory proxyFactory = new MatlabProxyFactory(options);
        try {
            m_proxy = proxyFactory.getProxy();

        } catch (MatlabConnectionException e) {
            System.err.println("Create Matlab Proxy failed!");
            e.printStackTrace();
        }
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate, History.HistoryView statehistory) {
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		try {
			Object[] objects = m_proxy.returningFeval("agent_initialStep", 1, newstate);
			
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
		return builder;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate, History.HistoryView statehistory) {
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		try {
			Object[] objects = m_proxy.returningFeval("agent_middleStep", 1, newstate);
			Map<Double, Action> actionMap = (Map<Double, Action>)objects[0];
			for(double key : actionMap.keySet()) {
				int keyInt = (int)key;
				builder.put(keyInt, actionMap.get(key));
			}
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
		return builder;
	}

	@Override
	public void terminalStep(StateView newstate, History.HistoryView statehistory) {
		try {
			m_proxy.feval("agent_terminalStep", newstate);
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void savePlayerData(OutputStream os) {
		//this agent lacks learning and so has nothing to persist.
		
	}
	@Override
	public void loadPlayerData(InputStream is) {
		//this agent lacks learning and so has nothing to persist.
	}
}
