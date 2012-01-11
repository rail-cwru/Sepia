package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cwru.SimpleRTS.Log.EventLogger;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class PlayerState implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public final int playerNum;
	private HashMap<Integer,Unit> units;
	@SuppressWarnings("rawtypes")
	private Map<Integer,Template> templates;
	private Set<Integer> upgrades;
	private Map<ResourceType,Integer> currentResources;
	private int currentSupply;
	private int currentSupplyCap;
	private StateView view;
	private EventLogger logger;
	private int[][] canSee;
	
	@SuppressWarnings("rawtypes")
	public PlayerState(int id) {
		this.playerNum = id;
		units = new HashMap<Integer,Unit>();
		templates = new HashMap<Integer,Template>();
		upgrades = new HashSet<Integer>();
		currentResources = new EnumMap<ResourceType,Integer>(ResourceType.class);	
		logger = new EventLogger();
	}
	
	public Unit getUnit(int id) {
		return units.get(id);
	}
	
	public Map<Integer,Unit> getUnits() {
		return units;
	}
	
	public void addUnit(Unit unit) {
		units.put(unit.ID, unit);
	}
	
	@SuppressWarnings("rawtypes")
	public Template getTemplate(int id) {
		return templates.get(id);
	}

	@SuppressWarnings("rawtypes")
	public Template getTemplate(String name) {
		for(Template t : templates.values())
		{
			if(name.equals(t.getName()))
			{
				return t;
			}
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<Integer,Template> getTemplates() {
		return templates;
	}
	
	public void addTemplate(@SuppressWarnings("rawtypes") Template template) {
		templates.put(template.ID, template);
	}
	
	public Set<Integer> getUpgrades() {
		return upgrades;
	}
	
	public int getCurrentResourceAmount(ResourceType type) {
		Integer amount = currentResources.get(type);
		if(amount != null)
		{
			return amount;
		}
		else
		{
			return 0;
		}
	}
	
	public void setCurrentResourceAmount(ResourceType type, int amount) {
		currentResources.put(type, amount);
	}
	
	public void addToCurrentResourceAmount(ResourceType type, int increase) {
		setCurrentResourceAmount(type, getCurrentResourceAmount(type) + increase);
	}
	
	public int getCurrentSupply() {
		return currentSupply;
	}
	
	public void setCurrentSupply(int supply) {
		currentSupply = supply;
	}
	
	public void addToCurrentSupply(int increase) {
		setCurrentSupply(getCurrentSupply() + increase);
	}
	
	public int getCurrentSupplyCap() {
		return currentSupplyCap;
	}
	
	public void setCurrentSupplyCap(int supply) {
		currentSupplyCap = supply;
	}

	public void addToCurrentSupplyCap(int increase) {
		setCurrentSupplyCap(getCurrentSupplyCap() + increase);
	}
	
	public StateView getView() {
		return view;
	}
	
	public void setStateView(StateView view) {
		this.view = view;
	}
	
	public EventLogger getEventLogger() {
		return logger;
	}
	
	public void setEventLogger(EventLogger logger) {
		this.logger = logger;
	}
	
	public int[][] getVisibilityMatrix() {
		return canSee;
	}
	
	public void setVisibilityMatrix(int[][] matrix) {
		canSee = matrix;
	}
	
	@Override
	protected Object clone() {
		PlayerState copy = new PlayerState(playerNum);
		for(Unit u : units.values())
		{
			copy.addUnit(u);
		}
		for(@SuppressWarnings("rawtypes") Template t : templates.values())
		{
			copy.addTemplate(t);
		}
		for(Integer i : upgrades)
		{
			copy.getUpgrades().add(i);
		}
		for(ResourceType type : currentResources.keySet())
		{
			copy.setCurrentResourceAmount(type, currentResources.get(type));
		}
		copy.setCurrentSupply(currentSupply);
		copy.setCurrentSupplyCap(currentSupplyCap);
		return copy;
	}
	
	public PlayerState copyOf() {
		return (PlayerState)clone();
	}
}
