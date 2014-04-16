package com.SocialCity.Area;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CodeNameMap {
	
	HashMap<String, ArrayList<String>> codeNameMap;
	private HashMap<String, String> nameMap;
	private HashMap<String, String> codeMap;
	
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
	
	public HashMap<String,String> getNameMap() {
		return nameMap;
	}
	
	public String getCode(String boroughName) {
		return codeMap.get(boroughName);
	}
	
	public Set<String> getBoroughCodes() {
		Set<String> codes = codeNameMap.keySet();
		Set<String> returnSet = new HashSet<String>();
		
		for (String s : codes) {
			if (s.length() <= 4) {
				//System.out.println(s);
				returnSet.add(s);
			}
		}
		
		return returnSet;
	}
	
	//creates map that places either codes as key and names as values or vice versa
	//code boolean decides order - true for code to be key, false for other way around
	private HashMap<String, String> getBaseMap(boolean code) {
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
	
	//fills up map as needed
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
