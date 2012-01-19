# sample terminal command to run the java package.

#java -cp SimpleRTSv3.1.jar edu.cwru.SimpleRTS.Main --config data/midasConfig.xml data/rc_3m5t.xml --agent edu.cwru.SimpleRTS.agent.RCAgent 0
java -cp SimpleRTSv3.1.jar edu.cwru.SimpleRTS.Main --config data/midasConfig.xml data/rc_3m5t.xml --agent edu.cwru.SimpleRTS.agent.RCAgent 0 --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam true --agentparam true