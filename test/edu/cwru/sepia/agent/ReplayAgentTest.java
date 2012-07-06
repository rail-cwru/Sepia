package edu.cwru.sepia.agent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.BackingStoreException;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.sepia.Main;
import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.model.SimpleModel;
import edu.cwru.sepia.model.SimplePlanner;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.unit.Unit;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.util.TypeLoader;


public class ReplayAgentTest {
	@BeforeClass
	public static void loadTemplates() throws Exception {
		
	
	}
	
	public void setUp() throws Exception {
	}
	
//	@Test
//	public void rngtest() throws IOException, InterruptedException, BackingStoreException {
//		String[] params = {"--config","data/replayCombatTestConfig.xml","data/combattest.map",
//				"--agent","edu.cwru.SimpleRTS.agent.CombatAgent","0","--agentparam","1","--agentparam","false","--agentparam","true",
//				"--agent","edu.cwru.SimpleRTS.agent.CombatAgent","1","--agentparam","0","--agentparam","false","--agentparam","true",
//				"--agent","edu.cwru.SimpleRTS.agent.visual.VisualAgent","0","--agentparam","false","--agentparam","true"};
//		Main.main(params);
//		
//		String[] params2 = {"--config","data/replayCombatTestConfig.xml","data/combattest.map",
//				"--agent","edu.cwru.SimpleRTS.agent.ReplayAgent","0","--agentparam","saves/state0.SRTSsav","--agentparam","true",
//				"--agent","edu.cwru.SimpleRTS.agent.ReplayAgent","1","--agentparam","saves/state0.SRTSsav","--agentparam","true",
//				"--agent","edu.cwru.SimpleRTS.agent.visual.VisualAgent","0","--agentparam","false","--agentparam","true"};
//		Main.main(params2);
//		
//	}
	@Test
	public void test() throws IOException, InterruptedException, BackingStoreException {
		String para = "--config data/midasConfig.xml data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.RCAgent 0";
		Main.main(para.split(" +"));
		
		para = "--config data/midasConfig.xml data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.ReplayAgent 0 --agentparam saves/state0.SRTSsav --agentparam true --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam false --agentparam true";
		Main.main(para.split(" +"));
	}
	
}
