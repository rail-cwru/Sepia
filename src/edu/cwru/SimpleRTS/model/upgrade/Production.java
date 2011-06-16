package edu.cwru.SimpleRTS.model.upgrade;
import edu.cwru.SimpleRTS.model.Template;
public abstract class Production<T extends Template> {
	private int timeleft;
	private T producee;
	public Production(T template, int timetofinish)
	{
		this.producee = template;
		timeleft = timetofinish;
	}
	/**
	 * Decrement time and return the template for what it is trying to produce if you have
	 * @return The template being produced if it is done, or null if it is not.
	 */
	public T decrementTime()
	{
		return --timeleft <= 0?producee:null;
	}
}
