package com.SocialCity.Area;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.SocialCity.DataParsers.CvsParsing;
import com.SocialCity.DataParsers.ExcelParsing;


public class BoundaryMap {
	public static HashMap<String, ArrayList<String>>returnWardMap(boolean useWards) {
		HashMap<String, ArrayList<String>> boundaryMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> neighbours;
		String fileName;
		
		if (useWards) {
			fileName = "resources/bordersNamesCodes.txt";
		}
		else {
			fileName = "resources/boroughsBordersNamesCodes.txt";
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			String subject="";
			boolean newSegment = true;
			neighbours = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (newSegment) {
					newSegment = false;
					subject = line.replace("\n", "");
					//System.out.println(subject);
				}
				else if (!line.equals("---")) {
					neighbours.add(line.replace("\n",""));
				}
				else if (line.equals("---")) {
					boundaryMap.put(subject, neighbours);
					//System.out.println(subject);
					//System.out.println(neighbours);
					//System.out.println("------");
					neighbours = new ArrayList<String>();
					newSegment = true;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return boundaryMap;
		
	}
	
	public static void makeBoundaryMap(){
		CvsParsing.boundsParse();
		String prev = "";
		try {
			FileWriter write = new FileWriter(new File("resources/bordersNamesCodes.txt"));
			boolean city = true;
			String subject = "";
			HashMap<String,String> names = ExcelParsing.nameMap();
			BufferedReader br = new BufferedReader(new FileReader("resources/bordersNames.txt"));
			String line;
			ArrayList<String> cityNeigh = new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				if (!line.equals("---") && names.get(line.replace("\n", "")) != null){
				  write.write(names.get(line.replace("\n", ""))+"\n");
				}
				else if (line.equals("---")) {
					write.write(line+"\n"); 
					city = true;
				}
				else if (prev.equals("---") && names.get(line.replace("\n", "")) == null){
					while (!line.equals("---")) {
						line = br.readLine();
					}
				}
				else if (names.get(line.replace("\n", "")) == null && city) {
					write.write(names.get("City of London")+"\n");
					cityNeigh.add(names.get(subject));
					city = false;
				}
				if (prev.equals("---") || prev==null) {
					subject = line.replace("\n", "");
				}
				prev = line;
			}
			
			write.write("00AA\n");
			for (String n: cityNeigh) {
				write.write(n + "\n");
			}
			write.write("---");
			
			br.close();
			write.close();
			
			br = new BufferedReader(new FileReader("resources/boroughs.txt"));
			write = new FileWriter(new File("resources/boroughsBordersNamesCodes.txt"));
			while ((line = br.readLine()) != null) {
				if (!line.equals("---") && names.get(line.replace("\n", "")) != null){
					  write.write(names.get(line.replace("\n", ""))+"\n");
				}
				else if (line.equals("---")) {
					write.write(line+"\n"); 
				}
			}
			br.close();
			write.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
