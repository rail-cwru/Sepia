package edu.cwru.SimpleRTS.agent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.BackingStoreException;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cwru.SimpleRTS.Main;
import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.agent.visual.VisualLog;
import edu.cwru.SimpleRTS.agent.visual.GamePanel;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.model.SimplePlanner;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;


public class AgentLogTest {
	
	@Test
	public void test() {
		
		
		SimpleAgent1 logtest = new SimpleAgent1(0);
		System.out.println("Log should not have appeared, press enter");
		pause();
		logtest.writeLineVisual("Here are some things you may enjoy:");
		logtest.writeLineVisual("Toast");
		logtest.writeLineVisual("Stamp Collecting");
		logtest.writeLineVisual("Porridge");
		logtest.writeLineVisual("The Sugar Act");
		logtest.writeLineVisual("Slide Rules");
		logtest.writeLineVisual("Toasters");
		logtest.writeLineVisual("The television program Farscape");
		logtest.writeLineVisual("Indian Burns");
		logtest.writeLineVisual("Charlie Chaplin");
		System.out.println("Log should have 10 lines, press enter");
		pause();
		logtest.clearVisualLog();
		System.out.println("Log should have cleared, press enter");
		pause();
		logtest.writeLineVisual("Here are some things that begin with the letter f or m:");
		logtest.writeLineVisual("Faust");
		logtest.writeLineVisual("Faun Collecting");
		logtest.writeLineVisual("Mooseburgers");
		logtest.writeLineVisual("The Molasses Act");
		logtest.writeLineVisual("Martial Rules");
		logtest.writeLineVisual("Monsters");
		logtest.writeLineVisual("The television program Farscape");
		logtest.writeLineVisual("French Burns");
		logtest.writeLineVisual("Frankie Chaplin");
		System.out.println("Log should have 10 lines, press enter");
		pause();
		logtest.setVisualLogDimensions(107, 107);
		System.out.println("Log should have become 107x107, press enter");
		pause();
		logtest.closeVisualLog();
		System.out.println("Log should have closed");
		pause();
		logtest.writeLineVisual("Here's Johnny!");
		System.out.println("Log should have reopened");
		pause();
	}
	private void pause()
	{
		try {
			new BufferedReader(new InputStreamReader(System.in)).read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
