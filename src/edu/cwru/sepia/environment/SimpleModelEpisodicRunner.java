package edu.cwru.sepia.environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.model.SimpleModel;
import edu.cwru.sepia.util.Configuration;
import edu.cwru.sepia.util.ConfigurationValues;
import edu.cwru.sepia.util.GameMap;

/**
 * A {@code Runner} that runs a number of episodes using {@code edu.cwru.SimpleRTS.model.SimpleModel}.
 * @author Tim
 *
 */
public class SimpleModelEpisodicRunner extends Runner {
	private static final Logger logger = Logger.getLogger(SimpleModelEpisodicRunner.class.getCanonicalName());

	public SimpleModelEpisodicRunner(Configuration configuration, StateCreator stateCreator, Agent[] agents) {
		super(configuration, stateCreator, agents);
		// TODO Auto-generated constructor stub
	}

	
	private int seed;
	private int numEpisodes;
	private int episodesPerSave;
	private boolean saveAgents;
	private Environment env;
	@Override
	public void run() {
		seed = 6;
		numEpisodes = ConfigurationValues.ENVIRONMENT_EPISODES.getIntValue(configuration);
		episodesPerSave = ConfigurationValues.ENVIRONMENT_EPISODES_PER_SAVE.getIntValue(configuration);
		saveAgents = ConfigurationValues.ENVIRONMENT_SAVE_AGENTS.getBooleanValue(configuration);
		
		SimpleModel model = new SimpleModel(stateCreator.createState(), seed, stateCreator);
		File firstFile = new File("saves");
		firstFile.mkdirs();
		env = new Environment(agents ,model, seed);
		for(int episode = 0; episode < numEpisodes; episode++)
		{
			//System.out.println("\n=======> Start running episode " + episode);
			try
			{
				env.runEpisode();
			}
			catch (InterruptedException e)
			{
				logger.log(Level.SEVERE, "Unable to complete episode " + episode + "!", e);
			}
			if(episodesPerSave > 0 && episode % episodesPerSave == 0)
			{
				saveState(new File("saves/state"+episode+".SRTSsav"),env.getModel().getState());
				for(int j = 0; saveAgents && j < agents.length; j++)
				{
					try {
						ObjectOutputStream agentOut = new ObjectOutputStream(new FileOutputStream("saves/agent"+j+"-"+episode));
						agentOut.writeObject(agents[j]);
						agentOut.close();
					}
					catch(Exception ex) {
						System.out.println("Unable to save agent "+j);
					}
				}
			}
		}
		System.exit(0);
	}
	public void loadFull() {
		
	}
	
}
