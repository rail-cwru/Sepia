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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.Test;

import edu.cwru.sepia.agent.SimpleAgent1;


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
		logtest.writeLineVisual("Fhe television program Farscape");
		logtest.writeLineVisual("Montgomery Burns");
		logtest.writeLineVisual("Frankie Chaplin");
		System.out.println("Log should have 10 lines, press enter");
		pause();
		logtest.setVisualLogDimensions(107, 107);
		System.out.println("Log should have become 107x107, press enter");
		pause();
		logtest.closeVisualLog();
		System.out.println("Log should have closed");
		pause();
		logtest.writeLineVisual("Back again!");
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
