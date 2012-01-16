package edu.cwru.SimpleRTS.environment;
/**
 * An interface describing the ability to issue unique identification numbers to targets (units or resource nodes) and templates
 * The State class is intended to be the primary implementer of this. 
 * @author The Condor
 *
 */
public interface IDDistributer {
	public int nextTargetID();
	public int nextTemplateID();
}
