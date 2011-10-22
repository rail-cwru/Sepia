package edu.cwru.SimpleRTS.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.prerequisite.BuildingPrerequisite;
import edu.cwru.SimpleRTS.model.prerequisite.PrerequisiteHolder;
import edu.cwru.SimpleRTS.model.prerequisite.UpgradePrerequisite;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

/**
 * Signifies that an implementing class provides generic details about a specific object.
 * Also provides a means for creating factory methods for specific kinds of game objects.
 * @author Tim
 *
 * @param <T>
 */
public abstract class Template<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TemplateView view;
	protected int timeCost;
	protected int goldCost;
	protected int woodCost;
	protected int foodCost;
	protected int player;
	protected PrerequisiteHolder prereqs;
	private Set<String> buildPrereq;
	private Set<String> upgradePrereq;
	protected String name;
	public final int ID;
	/**
	 * A factory method that produces copies of a "default" object
	 * @return
	 */
	public abstract T produceInstance();
	private static int nextID=0;
	public Template()
	{
		ID = nextID++;
		buildPrereq = new HashSet<String>();
		upgradePrereq = new HashSet<String>();
	}
	public boolean canProduce(StateView state) {
		return prereqs.isFulfilled(state);
	}
	public int getTimeCost() {
		return timeCost;
	}
	public void setTimeCost(int timeCost) {
		this.timeCost = timeCost;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
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
	public void setPlayer(int playerid) {
		this.player = playerid;
	}
	public int getPlayer() {
		return this.player;
	}
	public void addBuildPrereqItem(String name) {
		buildPrereq.add(name);
	}
	public void addUpgradePrereqItem(String name) {
		upgradePrereq.add(name);
	}
	/**
	 * Turn this template's list of prerequisites and things it produces into their ids
	 */
	public void namesToIds(List<UnitTemplate> untemplates, List<UpgradeTemplate> uptemplates) {
		prereqs = new PrerequisiteHolder();
		for (String s : buildPrereq) {
			for (UnitTemplate template : untemplates) {
				if (template.getName().equals(s)) {
//					System.out.println(getName()+" requires building " + s);
					prereqs.addPrerequisite(new BuildingPrerequisite(getPlayer(), template.ID));
					break;
				}
			}
		}
		for (String s : upgradePrereq) {
			for (UpgradeTemplate template : uptemplates) {
				if (template.getName().equals(s)) {
//					System.out.println(getName()+" requires upgrade " + s);
					prereqs.addPrerequisite(new UpgradePrerequisite(getPlayer(), template.hashCode()));
					break;
				}
			}
		}
	}
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
	public TemplateView getView() {
		if(view == null)
			view = new TemplateView(this);
		return view;
	}
	public static class TemplateView implements Serializable{
		protected Template template;
		public TemplateView(Template template){
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
		public int getID() {
			return template.ID;
		}
		public int getPlayer() {
			return template.getPlayer();
		}
		public boolean canProduce(StateView state) {
			return template.canProduce(state);
		}
	}
}
