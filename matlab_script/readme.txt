============================

INPORTANT NOTE:

This only works under Matlab of Windows 7 environment, and doesn't work under Matlab of Unix/Linux/Mac OS X environment.

============================

HOW TO RUN THE SAMPLE CODE:

To test the matlab_script, simply run test_SimpleRTS in matlab.

It will call the main function in edu.cwru.SimpleRTS.Main, passing proper arguments.
The edu.cwru.SimpleRTS.agent.MatlabAgent is called.

=============================

HOW TO WRITE YOUR OWN MATLAB AGENT:

If you want to write your own Agent with matlab, just implements the four functions 
(use exactly the same name for them as indicated):
1. agent_init -- will be called only once when MatlabAgent was created
2. agent_initialStep -- will be called at first step of the episode.
3. agent_middleStep -- called at each step of the episode (non-first, non-terminal step)
4. agent_terminalStep -- called at the terminal step of the episode

=============================

CONTACT:

If you have any question regarding running the sample matlab code, feel free to contact: fxc100@case.edu
However, I would recommend you to use Java rather than this cumbersome matlab interface.

