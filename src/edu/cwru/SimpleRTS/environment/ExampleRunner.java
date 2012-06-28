package edu.cwru.SimpleRTS.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.model.LessSimpleModel;
import edu.cwru.SimpleRTS.model.Model;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.util.Configuration;
import edu.cwru.SimpleRTS.util.ConfigurationValues;
import edu.cwru.SimpleRTS.util.GameMap;

/**
 * A {@code Runner} that runs a number of episodes using {@code edu.cwru.SimpleRTS.model.LessSimpleModel}.
 * <br> Demonstrates some 
 *
 */
public class ExampleRunner extends Runner {
	private static final Logger logger = Logger.getLogger(ExampleRunner.class.getCanonicalName());

	public ExampleRunner(Configuration configuration, StateCreator stateCreator, Agent[] agents) {
		super(configuration, stateCreator, agents);
	}

	
	private int seed;
	private int numEpisodes;
	private int episodesPerReplaySave;
	private boolean saveReplays;
	private boolean saveAgents;
	private Environment env;
	private int episodesPerAgentSave;
	private File baseReplayDirectory;
	private File baseAgentDirectory;
	@Override
	public void run() {
		seed = 6;
		numEpisodes = ConfigurationValues.ENVIRONMENT_EPISODES.getIntValue(configuration);
		numEpisodes = configuration.getInt("environment.NumEpisodes", 12);
		episodesPerReplaySave = configuration.getInt("experiment.episodesperreplaysave",4);
		episodesPerAgentSave = configuration.getInt("experiment.episodesperagentsave",4);
		baseReplayDirectory = new File(configuration.getString("experiment.save.replaydirectory", "saves/replays"));
		baseAgentDirectory = new File(configuration.getString("experiment.save.agentdirectory", "saves/agents"));
		saveAgents = (episodesPerAgentSave>=1);
		saveReplays = (episodesPerReplaySave>=1);
		Model model = new LessSimpleModel(stateCreator.createState(), seed, stateCreator);
		
		env = new Environment(agents, model, seed);
		for(int episode = 0; episode < numEpisodes; episode++)
		{
			
			boolean saveAgentsThisEpisode = episodesPerAgentSave > 0 ? saveAgents && episode % episodesPerAgentSave == 0 : false;
			boolean saveReplayThisEpisode = episodesPerReplaySave > 0 ? saveReplays && episode % episodesPerReplaySave == 0 : false;
			
			State initialStateCopy=null;
			//System.out.println("\n=======> Start running episode " + episode);
			try
			{
				//Manually implement the run
				
				//Make a new state and clear the history
				env.forceNewEpisode();
					if (saveReplayThisEpisode) {
						//copy the initialstate in case the state creator is stochastic
						try {
						initialStateCopy = env.getModel().getState().getView(Agent.OBSERVER_ID).getStateCreator().createState();
						}
						catch (IOException e) {
							saveReplayThisEpisode=false;
							e.printStackTrace();
							logger.warning("Unable to save replay, error while saaving state "+e.getMessage());
						}
					}
				//Run steps until it returns true, indicating that it is terminated
				while(!env.step()) ;
				//Run the terminal step
				env.terminalStep();
				
					if (saveReplayThisEpisode) {
						saveReplay(new File(baseReplayDirectory,"ep"+episode),initialStateCopy, env.getModel().getHistory());
					}
			}
			catch (InterruptedException e)
			{
				logger.log(Level.SEVERE, "Unable to complete episode " + episode + "!", e);
			}
			if(saveAgentsThisEpisode)
			{
				
				for(int j = 0; saveAgents && j < agents.length; j++)
				{
					try {
						saveAgentData(new File(baseAgentDirectory,"ep"+episode), j);
					}
					catch(FileNotFoundException ex) {
						System.err.println("Unable to save agent "+j);
						ex.printStackTrace();
					}
				}
			}
		}
		System.exit(0);
	}
}
