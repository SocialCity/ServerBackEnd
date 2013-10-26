package com.SocialCity.DataParsers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.SocialCity.SocialFactor.SocialFactors;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
//Parses an excel file
public class ExcelParsing {

	public ExcelParsing() {}
	
	//DO NOT CALL THIS UNLESS YOU WANT TO DROP THE CURRENT DATABASE
	public void parse() {
		MongoClient mongoClient;
		Sheet sheet = null;
		String housePrice;
		DBCollection coll = null;
		DBCollection collBorough = null;
		
		try {//set up file reader and databases
			Workbook workbook = Workbook.getWorkbook(new File("resources/ward-profiles-excel-version.xls"));
			sheet = workbook.getSheet(1); 
			mongoClient = new MongoClient("localhost");
			mongoClient.dropDatabase("areas");
			DB db = mongoClient.getDB( "areas" );
			db.createCollection("wards", null);
			coll = db.getCollection("wards");
			db.createCollection("boroughs", null);
			collBorough = db.getCollection("boroughs");
			
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		for (int i=1; i<sheet.getRows()-3;i++) {//gets needed factors for every location
			SocialFactors sF = new SocialFactors(sheet.getCell(1, i).getContents());
			
			sF.setCrimeRate(Double.parseDouble(sheet.getCell(55, i).getContents()));
			
			housePrice = (sheet.getCell(26,i).getContents()).substring(3);
			housePrice = housePrice.replace(",", "");
			sF.setHousePrice(Double.parseDouble(housePrice));
			
			sF.setEducationRating(Double.parseDouble(sheet.getCell(54, i).getContents()));
			
			sF.setTransportRating(Double.parseDouble(sheet.getCell(64, i).getContents()));
			
			sF.setMeanAge(Double.parseDouble(sheet.getCell(9, i).getContents()));
			sF.setDrugRate(Double.parseDouble(sheet.getCell(60, i).getContents()));
			sF.setEmploymentRate(Double.parseDouble(sheet.getCell(22, i).getContents()));
			sF.setVoteTurnout(Double.parseDouble(sheet.getCell(65, i).getContents()));
			
			if (sF.getLocation().get(0).length() == 6) {//checks if location is a ward
				coll.insert(sF.getDBObject());
			}
			else if (sF.getLocation().get(0).equals("00AA")) {//city of london borough must be both ward and borough
				coll.insert(sF.getDBObject());
				collBorough.insert(sF.getDBObject());
			}
			else if (sF.getLocation().get(0).length() == 4){//checks if locale is a borough
				collBorough.insert(sF.getDBObject());
				//System.out.println("heyo");
			}
			//System.out.println(sF.getDBObject());
		}
	}

	//gets a map of names to ward/borough codes.
	public static HashMap<String, String> nameMap() {
		HashMap<String, String> names = new HashMap<String, String>();
		
		Sheet sheet = null;

		try {
			Workbook workbook = Workbook.getWorkbook(new File("resources/ward-profiles-excel-version.xls"));
			sheet = workbook.getSheet(1); 
			for (int i=1; i<sheet.getRows()-3;i++) {
				String name = sheet.getCell(0, i).getContents();
				if (name.indexOf("-") != -1) {
					name = name.substring(name.indexOf("-")+2);
				}
				name = name.replace("&", "and");
				names.put(name,sheet.getCell(1, i).getContents());
				//System.out.println(name);
			}
			
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return names;
	}
}
