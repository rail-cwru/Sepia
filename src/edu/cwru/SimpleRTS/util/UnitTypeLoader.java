package edu.cwru.SimpleRTS.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.json.*;

import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public final class UnitTypeLoader {
	private UnitTypeLoader(){}
	
	public static List<Template> loadFromFile(String filename) throws FileNotFoundException, JSONException {
		List<Template> templates = new ArrayList<Template>();
		List<UpgradeTemplate> uptemplates  = loadUpgradesFromFile(filename);
		for (Template t : uptemplates)
			templates.add(t);
		List<UnitTemplate> untemplates  = loadUnitsFromFile(filename);
		for (Template t : untemplates)
			templates.add(t);
		for (Template t : templates) {
			t.turnTemplatesToStrings(templates);
		}
		return templates;
	}
	public static List<UpgradeTemplate> loadUpgradesFromFile(String filename) throws FileNotFoundException, JSONException {
		List<UpgradeTemplate> list = new ArrayList<UpgradeTemplate>();
		StringBuilder sb = new StringBuilder();
		Scanner in = new Scanner(new File(filename));
		while(in.hasNextLine())
		{
			sb.append(in.nextLine());
			sb.append("\n");
		}
		in.close();
		JSONObject templateFile = new JSONObject(sb.toString());
		String[] keys = JSONObject.getNames(templateFile);
		for(String key : keys)
		{
			JSONObject template = templateFile.getJSONObject(key);
			String templateType = template.getString("TemplateType");
			if(templateType.equals("Upgrade"))
				list.add(handleUpgrade(template,key));
		}
		return list;
	}
	public static List<UnitTemplate> loadUnitsFromFile(String filename) throws FileNotFoundException, JSONException {
		List<UnitTemplate> list = new ArrayList<UnitTemplate>();
		StringBuilder sb = new StringBuilder();
		Scanner in = new Scanner(new File(filename));
		while(in.hasNextLine())
		{
			sb.append(in.nextLine());
			sb.append("\n");
		}
		in.close();
		JSONObject templateFile = new JSONObject(sb.toString());
		String[] keys = JSONObject.getNames(templateFile);
		for(String key : keys)
		{
			JSONObject template = templateFile.getJSONObject(key);
			String templateType = template.getString("TemplateType");
			if(templateType.equals("Unit"))
				list.add(handleUnit(template,key));
		}
		return list;
	}
	private static UnitTemplate handleUnit(JSONObject obj, String name) throws JSONException {
		UnitTemplate template = new UnitTemplate();
		template.setName(obj.getString("Name"));
		if(obj.has("Mobile"))
			template.setCanMove(obj.getBoolean("Mobile"));
		if(obj.has("Builder"))
			template.setCanBuild(obj.getBoolean("Builder"));
		if(obj.has("Gatherer"))
			template.setCanGather(obj.getBoolean("Gatherer"));
		template.setBaseHealth(obj.getInt("HitPoints"));
		template.setArmor(obj.getInt("Armor"));
		template.setCharacter(obj.getString("Character").charAt(0));
		if(obj.has("BasicAttackLow"))
			template.setBasicAttackLow(obj.getInt("BasicAttackLow"));
		if(obj.has("BasicAttackDiff"))
			template.setBasicAttackDiff(obj.getInt("BasicAttackDiff"));
		if(obj.has("Piercing"))
			template.setPiercingAttack(obj.getInt("Piercing"));
		if(obj.has("Range"))
			template.setRange(obj.getInt("Range"));
		template.setSightRange(obj.getInt("SightRange"));
		template.setTimeCost(obj.getInt("TimeCost"));
		if(obj.has("FoodCost"))
			template.setFoodCost(obj.getInt("FoodCost"));
		template.setGoldCost(obj.getInt("GoldCost"));
		template.setWoodCost(obj.getInt("WoodCost"));
		
		if(obj.has("Produces"))
		{
			JSONArray produces = obj.getJSONArray("Produces");
			for(int i = 0; i < produces.length(); i++)
				template.addProductionItem(produces.getString(i));		
		}
		template.setUnitName(name);
		return template;
	}
	private static UpgradeTemplate handleUpgrade(JSONObject obj, String name) throws JSONException {
		
		UpgradeTemplate template = new UpgradeTemplate(false, null);
		template.setName(obj.getString("Name"));
		return null;
	}
}
