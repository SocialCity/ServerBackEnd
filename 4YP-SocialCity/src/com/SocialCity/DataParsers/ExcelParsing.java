package com.SocialCity.DataParsers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.SocialCity.SocialFactor.SocialFactors;
import com.SocialCity.TwitterAnalysis.TweetByArea;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
//Parses an excel file
public class ExcelParsing {

	public ExcelParsing() {}
	
	//DO NOT CALL THIS UNLESS YOU WANT TO DROP THE CURRENT DATABASE
	public void parse() throws UnknownHostException {
		MongoClient mongoClient;
		Sheet sheet = null;
		String housePrice;
		ArrayList<DBCollection> collWard = new ArrayList<DBCollection>();
		ArrayList<DBCollection> collBorough = new ArrayList<DBCollection>();
		String name;
		HashMap<String, Double> tweetCount = (new TweetByArea()).tweetProportion(null);
		
		try {//set up file reader and databases
			Workbook workbook = Workbook.getWorkbook(new File("resources/wardData.xls"));

			sheet = workbook.getSheet(5); 
			mongoClient = new MongoClient("localhost");
			mongoClient.dropDatabase("areas");
			DB db = mongoClient.getDB( "areas" );
			
			collWard.add(db.createCollection("wards2008", null));
			collWard.add(db.createCollection("wards2009", null));
			collWard.add(db.createCollection("wards2010", null));
			collWard.add(db.createCollection("wards2011", null));
			collWard.add(db.createCollection("wards2012", null));
			
			collBorough.add(db.createCollection("boroughs2008", null));
			collBorough.add(db.createCollection("boroughs2009", null));
			collBorough.add(db.createCollection("boroughs2010", null));
			collBorough.add(db.createCollection("boroughs2011", null));
			collBorough.add(db.createCollection("boroughs2012", null));
			
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		for (int i=3; i<627;i++) {//gets needed factors for every location
			String code = sheet.getCell(0, i).getContents();
			ArrayList<SocialFactors> sF = new ArrayList<SocialFactors>();
			
			sF.add(new SocialFactors(code));
			sF.add(new SocialFactors(code));
			sF.add(new SocialFactors(code));
			sF.add(new SocialFactors(code));
			sF.add(new SocialFactors(code));
			
			sF.get(1).setIncapacityBenefit(Double.parseDouble(sheet.getCell(9, i).getContents()));
			sF.get(2).setIncapacityBenefit(Double.parseDouble(sheet.getCell(10, i).getContents()));
			sF.get(3).setIncapacityBenefit(Double.parseDouble(sheet.getCell(11, i).getContents()));
			sF.get(4).setIncapacityBenefit(Double.parseDouble(sheet.getCell(12, i).getContents()));
			sF.get(5).setIncapacityBenefit(Double.parseDouble(sheet.getCell(13, i).getContents()));
			
			sF.get(1).setUnemploymentRate(Double.parseDouble(sheet.getCell(14, i).getContents()));
			sF.get(2).setUnemploymentRate(Double.parseDouble(sheet.getCell(15, i).getContents()));
			sF.get(3).setUnemploymentRate(Double.parseDouble(sheet.getCell(16, i).getContents()));
			sF.get(4).setUnemploymentRate(Double.parseDouble(sheet.getCell(17, i).getContents()));
			sF.get(5).setUnemploymentRate(Double.parseDouble(sheet.getCell(18, i).getContents()));
			
			sF.get(1).setIncomeSupport(Double.parseDouble(sheet.getCell(19, i).getContents()));
			sF.get(2).setIncomeSupport(Double.parseDouble(sheet.getCell(20, i).getContents()));
			sF.get(3).setIncomeSupport(Double.parseDouble(sheet.getCell(21, i).getContents()));
			sF.get(4).setIncomeSupport(Double.parseDouble(sheet.getCell(22, i).getContents()));
			sF.get(5).setIncomeSupport(Double.parseDouble(sheet.getCell(23, i).getContents()));
			
			sF.get(1).setCrimeRate(Double.parseDouble(sheet.getCell(24, i).getContents()));
			sF.get(2).setCrimeRate(Double.parseDouble(sheet.getCell(25, i).getContents()));
			sF.get(3).setCrimeRate(Double.parseDouble(sheet.getCell(26, i).getContents()));
			sF.get(4).setCrimeRate(Double.parseDouble(sheet.getCell(27, i).getContents()));
			sF.get(5).setCrimeRate(Double.parseDouble(sheet.getCell(28, i).getContents()));

			sF.get(1).setDeliberateFires(Double.parseDouble(sheet.getCell(29, i).getContents()));
			sF.get(2).setDeliberateFires(Double.parseDouble(sheet.getCell(30, i).getContents()));
			sF.get(3).setDeliberateFires(Double.parseDouble(sheet.getCell(31, i).getContents()));
			sF.get(4).setDeliberateFires(Double.parseDouble(sheet.getCell(32, i).getContents()));
			sF.get(5).setDeliberateFires(Double.parseDouble(sheet.getCell(33, i).getContents()));
			
			sF.get(1).setGCSEScore(Double.parseDouble(sheet.getCell(34, i).getContents()));
			sF.get(2).setGCSEScore(Double.parseDouble(sheet.getCell(35, i).getContents()));
			sF.get(3).setGCSEScore(Double.parseDouble(sheet.getCell(36, i).getContents()));
			sF.get(4).setGCSEScore(Double.parseDouble(sheet.getCell(37, i).getContents()));
			sF.get(5).setGCSEScore(Double.parseDouble(sheet.getCell(38, i).getContents()));
			
			sF.get(1).setSchoolAbscences(Double.parseDouble(sheet.getCell(39, i).getContents()));
			sF.get(2).setSchoolAbscences(Double.parseDouble(sheet.getCell(40, i).getContents()));
			sF.get(3).setSchoolAbscences(Double.parseDouble(sheet.getCell(41, i).getContents()));
			sF.get(4).setSchoolAbscences(Double.parseDouble(sheet.getCell(42, i).getContents()));
			sF.get(5).setSchoolAbscences(Double.parseDouble(sheet.getCell(43, i).getContents()));

			sF.get(1).setChildInNoWorkHouse(Double.parseDouble(sheet.getCell(44, i).getContents()));
			sF.get(2).setChildInNoWorkHouse(Double.parseDouble(sheet.getCell(45, i).getContents()));
			sF.get(3).setChildInNoWorkHouse(Double.parseDouble(sheet.getCell(46, i).getContents()));
			sF.get(4).setChildInNoWorkHouse(Double.parseDouble(sheet.getCell(47, i).getContents()));
			sF.get(5).setChildInNoWorkHouse(Double.parseDouble(sheet.getCell(48, i).getContents()));
			
			sF.get(1).setTransportRating(Double.parseDouble(sheet.getCell(49, i).getContents()));
			sF.get(2).setTransportRating(Double.parseDouble(sheet.getCell(50, i).getContents()));
			sF.get(3).setTransportRating(Double.parseDouble(sheet.getCell(51, i).getContents()));
			sF.get(4).setTransportRating(Double.parseDouble(sheet.getCell(52, i).getContents()));
			sF.get(5).setTransportRating(Double.parseDouble(sheet.getCell(53, i).getContents()));
			
			if (sF.get(1).getLocation().get(0).length() == 6) {//checks if location is a ward
				collWard.get(1).insert(sF.get(1).getDBObject());
				collWard.get(2).insert(sF.get(2).getDBObject());
				collWard.get(3).insert(sF.get(3).getDBObject());
				collWard.get(4).insert(sF.get(4).getDBObject());
				collWard.get(5).insert(sF.get(5).getDBObject());
			}
			else if (sF.get(1).getLocation().get(0).equals("00AA")) {//city of london borough must be both ward and borough
				collWard.get(1).insert(sF.get(1).getDBObject());
				collWard.get(2).insert(sF.get(2).getDBObject());
				collWard.get(3).insert(sF.get(3).getDBObject());
				collWard.get(4).insert(sF.get(4).getDBObject());
				collWard.get(5).insert(sF.get(5).getDBObject());
				
				collBorough.get(1).insert(sF.get(1).getDBObject());
				collBorough.get(2).insert(sF.get(2).getDBObject());
				collBorough.get(3).insert(sF.get(3).getDBObject());
				collBorough.get(4).insert(sF.get(4).getDBObject());
				collBorough.get(5).insert(sF.get(5).getDBObject());
			}
			else if (sF.get(1).getLocation().get(0).length() == 4){//checks if locale is a borough
				collBorough.get(1).insert(sF.get(1).getDBObject());
				collBorough.get(2).insert(sF.get(2).getDBObject());
				collBorough.get(3).insert(sF.get(3).getDBObject());
				collBorough.get(4).insert(sF.get(4).getDBObject());
				collBorough.get(5).insert(sF.get(5).getDBObject());
			}
			//System.out.println(sF.getDBObject());
			sF.clear();
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
