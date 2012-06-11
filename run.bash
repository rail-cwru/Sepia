java -cp bin:lib/* edu.cwru.SimpleRTS.Main --config data/defaultConfig.xml data/durcom_4f1ax8.xml --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam false --agentparam true

#Usage: java [-cp <path to your agent's class file>];SimpleRTS.jar] edu.cwru.SimpleRTS.Main [--config configurationFile] <map file name> [[--agent <agent class name> <player number> [--agentparam otherparameter]* [--loadfrom <serialized agent file name>]] ...] 

#Example: --config data/defaultConfig.xml "data/com_4f4a2kv4f4a2k.xml" --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam false --agentparam true --agent edu.cwru.SimpleRTS.agent.SimpleAgent1 0
