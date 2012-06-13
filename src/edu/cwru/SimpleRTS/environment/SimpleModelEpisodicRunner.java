package edu.cwru.SimpleRTS.environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.util.Configuration;
import edu.cwru.SimpleRTS.util.ConfigurationValues;

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

	@Override
	public void run() {
		int seed = 6;
		int numEpisodes = ConfigurationValues.ENVIRONMENT_EPISODES.getIntValue(configuration);
		int episodesPerSave = ConfigurationValues.ENVIRONMENT_EPISODES_PER_SAVE.getIntValue(configuration);
		boolean saveAgents = ConfigurationValues.ENVIRONMENT_SAVE_AGENTS.getBooleanValue(configuration);

		SimpleModel model = new SimpleModel(stateCreator.createState(), seed, stateCreator);
		File firstFile = new File("saves");
		firstFile.mkdirs();
		Environment env = new Environment(agents ,model, seed);
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
				model.save("saves/state"+episode+".SRTSsav");
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

}
