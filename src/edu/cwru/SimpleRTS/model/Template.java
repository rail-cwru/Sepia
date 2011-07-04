package edu.cwru.SimpleRTS.model;

import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.unit.Unit;

/**
 * Signifies that an implementing class provides generic details about a specific object.
 * Also provides a means for creating factory methods for specific kinds of game objects.
 * @author Tim
 *
 * @param <T>
 */
public abstract class Template<T> implements Serializable{
	private TemplateView<T> view;
	protected int timeCost;
	protected int goldCost;
	protected int woodCost;
	protected int foodCost;
	/**
	 * A factory method that produces copies of a "default" object
	 * @return
	 */
	public abstract T produceInstance();
	static int nextID=0;
	public Template()
	{
		ID = nextID++;
	}

	public int getTimeCost() {
		return timeCost;
	}
	public void setTimeCost(int timeCost) {
		this.timeCost = timeCost;
	}
	public int getGoldCost() {
		return goldCost;
	}
	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}
	public int getWoodCost() {
		return woodCost;
	}
	public void setWoodCost(int woodCost) {
		this.woodCost = woodCost;
	}
	public int getFoodCost() {
		return foodCost;
	}
	public void setFoodCost(int foodCost) {
		this.foodCost = foodCost;
	}
	protected int ID;
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Template))
			return false;
		return ((Template)o).ID == ID;
	}
	public TemplateView<T> getView() {
		if(view == null)
			view = new TemplateView<T>(this);
		return view;
	}
	public static class TemplateView<T> implements Serializable{
		protected Template<T> template;
		public TemplateView(Template<T> template){
			this.template = template;
		}
		public int getTimeCost() {
			return template.timeCost;
		}
		public int getGoldCost() {
			return template.goldCost;
		}
		public int getWoodCost() {
			return template.woodCost;
		}
		public int getFoodCost() {
			return template.foodCost;
		}
	}
}
