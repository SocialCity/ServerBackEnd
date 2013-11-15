package com.SocialCity.Area;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeNameMap {
	
	HashMap<String, ArrayList<String>> codeNameMap;
	HashMap<String, String> nameMap;
	HashMap<String, String> codeMap;
	
	public CodeNameMap() {
		codeNameMap = new HashMap<String, ArrayList<String>>();
		makeMap();
		nameMap = getBaseMap(false);
		codeMap = getBaseMap(true);
	}
	
	public ArrayList<String> getNames(String boroughCode) {
		return codeNameMap.get(boroughCode);
	}
	
	public String getName(String boroughCode) {
		return nameMap.get(boroughCode);
	}	
	
	public String getCode(String boroughName) {
		return codeMap.get(boroughName);
	}
	
	public HashMap<String, String> getBaseMap(boolean code) {
		String boroughFile = "resources/BoroughsToCodes.txt";
		String wardFile = "resources/WardsToCodes.txt";
		HashMap <String, String>  map= new HashMap<String, String>();
		try {
			fillMap(boroughFile, map, code);
			fillMap(wardFile, map, code);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	private void makeMap() {
		HashMap<String, String> map = getBaseMap(false);
		ArrayList<String> values;
		String key;
		
		for (String e : map.keySet()) {
			key = e.substring(0, 4);
			if (!codeNameMap.containsKey(key)) {
				values = new ArrayList<String>();
				values.add(map.get(e));
				codeNameMap.put(key, values);
			}
			else {
				values = codeNameMap.get(key);
				values.add(map.get(e));
			}
		}
	}
	
	private void fillMap (String filePath,HashMap<String, String> map, boolean code) throws IOException {
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null) {
			String[] entry = line.split(cvsSplitBy);
			if (code) {map.put(entry[0], entry[1]);}
			else {map.put(entry[1], entry[0]);}
		}
		br.close();
	}

}
