package edu.cwru.SimpleRTS.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.json.*;

import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public final class UnitTypeLoader {
	private UnitTypeLoader(){}
	public static List<Template> loadFromFile(String filename) throws FileNotFoundException, JSONException {
		List<Template> list = new ArrayList<Template>();
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
			if("Mobile".equals(templateType))
				list.add(handleMobileUnit(template));
			else if("Building".equals(templateType))
				list.add(handleBuilding(template));
			else if("Upgrade".equals(templateType))
				list.add(handleUpgrade(template));
		}
		return list;
	}
	private static Template handleMobileUnit(JSONObject obj) {
		return null;
	}
	private static Template handleBuilding(JSONObject obj) {
		return null;
	}
	private static Template handleUpgrade(JSONObject obj) {
		return null;
	}
}
