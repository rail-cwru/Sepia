To test it, simply run test_SimpleRTS in matlab.

It will call the main function in edu.cwru.SimpleRTS.Main, passing proper arguments.
The edu.cwru.SimpleRTS.agent.MatlabAgent is called.

Note that related data files are under "matlab_script/data" rather than "data"

If you wanna write your own Agent with matlab, just implements the four functions:
agent_init -- call when MatlabAgent was created
agent_initialStep -- called at first step of the episode
agent_middleStep -- called at each step of the episode (non-first, non-terminal step)
agent_terminalStep -- called at the terminal step of the episode