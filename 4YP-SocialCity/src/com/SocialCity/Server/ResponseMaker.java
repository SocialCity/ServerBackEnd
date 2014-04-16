package com.SocialCity.Server;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.SocialCity.Area.BoundaryMap;
import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.DataParsers.CollectionReader;
import com.SocialCity.SocialFactor.AreaWords;
import com.SocialCity.SocialFactor.SocialFactors;
import com.SocialCity.TwitterAnalysis.HashTag;
import com.SocialCity.TwitterAnalysis.TweetByArea;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
//Forms any query responses and returns the JSON of it
public class ResponseMaker {

	private double factorConstant = 0.25;
	
	public String oneFactor(int factorNumber, boolean useWards, boolean combine, boolean all, String year) {
		ArrayList<SocialFactors> listOfData = createFactors(factorNumber, useWards, combine, year);
		Gson gson = new Gson();
		
		if (all) {
			return gson.toJson(listOfData);//convert list to JSON for return
		}
		else {
			return gson.toJson(singleFactorOnly(listOfData, factorNumber));
		}
		
	}
	
	public String twoFactors(int factorNumber1, int factorNumber2, boolean useWards, boolean combine, boolean all, String year) {
		ArrayList<SocialFactors> listOfData = createFactors(factorNumber1, useWards, combine, year);
		Gson gson = new Gson();
		
		if (all) {
			return gson.toJson(listOfData);
		}
		else {
			return gson.toJson(twoFactorsOnly(listOfData, factorNumber1, factorNumber2));
		}
	}
	
	public ArrayList<SocialFactors> createFactors(int factorNumber, boolean useWards, boolean combine, String year) {
		
		MongoClient mongoClient;
		HashMap<String, ArrayList<String>> nameMap = BoundaryMap.returnWardMap(useWards);//gets either the ward or borough location data
		ArrayList<SocialFactors> listOfData = new ArrayList<SocialFactors>();//stores the info to return
		ArrayList<String> neighbours;
		DBCollection coll = null;
		DBCursor locales;
		SocialFactors sF;
		SocialFactors friend;
		BasicDBObject query;
		HashSet<String> checked = new HashSet<String>();
		HashSet<String> toCheck = new HashSet<String>();
		HashSet<String> tempChecked = new HashSet<String>();
		boolean recheck = true;
		
		if (!year.equals("2008")&&!year.equals("2009")&&!year.equals("2010")&&!year.equals("2011")&&!year.equals("2012")) {
			year = "2012";
		}
		
		try {
			//gets the database
			mongoClient = new MongoClient("localhost");
			//mongoClient.dropDatabase("areas");
			DB db = mongoClient.getDB( "areas" );
			
			if (useWards){//get ward info
				coll = db.getCollection("wards" + year);
			}
			else {//get borough info
				coll = db.getCollection("boroughs" + year);
				System.out.println(coll.getCount());
			}
			
			locales = coll.find();
			
			while(locales.hasNext()){//go through every location
				sF = new SocialFactors(locales.next());
				if (!(checked.contains(sF.getLocation().get(0)))) {//make sure location hasnt been checked yet
					//System.out.println("everybody gets one");
					neighbours = nameMap.get(sF.getLocation().get(0));
					tempChecked.addAll(neighbours);//add bordering locales for checking
					while (recheck) {//means locations where combined and new neighbours need to be checked
						recheck = false;
						for (String n : neighbours) {
							//check the neighbour is not the current location or in checked
							if (!(n.equals(sF.getLocation().get(0))) && !(checked.contains(n))) {
								//gets new neighbour
								query = new BasicDBObject("locations", new BasicDBObject("ward0", n));
								friend = new SocialFactors (coll.findOne(query));
								//check if the two locations need to be combined
								if (combine) {
									if (combine(sF, friend, factorNumber)){
										recheck = true;
										//System.out.println("here?");
										//add new neighbours to list to check next iteration
										toCheck.addAll(nameMap.get(friend.getLocation().get(0)));
									}
								}
							}
						}
						
						//remove any neighbours that where just checked
						toCheck.removeAll(tempChecked);
						neighbours.clear();
						neighbours.addAll(toCheck);//get new list to check
						tempChecked.addAll(toCheck);
						toCheck.clear();
						checked.addAll(sF.getLocation());//make sure all locations combined are not checked again
					}
					recheck = true;
					listOfData.add(sF);
					neighbours.clear();
					tempChecked.clear();
				}
			}
			mongoClient.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listOfData;
	}
	
	//filter social factors lists to return only single factor
	private ArrayList<HashMap<String, Object>> singleFactorOnly (ArrayList<SocialFactors> listOfData, int factorNumber) {
		ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> currentEntry;
		
		for (SocialFactors sF : listOfData) {
			currentEntry = new HashMap<String,Object>();
			currentEntry.put("location", sF.getLocation());
			currentEntry.put(sF.getFactorName(factorNumber), sF.getFactorValue(factorNumber));
			newList.add(currentEntry);
		}
		
		return newList;	
	}
	
	//filter social factors list to return only 2 factors, one used for combination and the other for comparison
	private ArrayList<HashMap<String, Object>> twoFactorsOnly(ArrayList<SocialFactors> listOfData, int factorNumber1, int factorNumber2) {
		ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> currentEntry;
		
		for (SocialFactors sF : listOfData) {
			currentEntry = new HashMap<String,Object>();
			currentEntry.put("location", sF.getLocation());
			currentEntry.put(sF.getFactorName(factorNumber1), sF.getFactorValue(factorNumber1));
			currentEntry.put(sF.getFactorName(factorNumber2), sF.getFactorValue(factorNumber2));
			newList.add(currentEntry);
		}
		
		return newList;	
	}

	//Case statement to decide which factor to use. Use these numbers in URL requests
	private boolean combine(SocialFactors sF, SocialFactors friend, int factorNumber) {
		boolean combined = false;
		
		switch (factorNumber) {
		case 0: if ((sF.getCrimeRate()*(1-factorConstant) < friend.getCrimeRate()) && (sF.getCrimeRate() > friend.getCrimeRate()) || 
						(sF.getCrimeRate() * (1+factorConstant) > friend.getCrimeRate()) && (sF.getCrimeRate() < friend.getCrimeRate())  || 
						(friend.getCrimeRate()*(1-factorConstant) < sF.getCrimeRate()) && (sF.getCrimeRate() < friend.getCrimeRate()) || 
						(friend.getCrimeRate() * (1+factorConstant) > sF.getCrimeRate()) && (sF.getCrimeRate() > friend.getCrimeRate())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 1: if ((sF.getHousePrice()*(1-factorConstant) < friend.getHousePrice()) && (sF.getHousePrice() > friend.getHousePrice()) || 
						(sF.getHousePrice() * (1+factorConstant) > friend.getHousePrice()) && (sF.getHousePrice() < friend.getHousePrice())  || 
						(friend.getHousePrice()*(1-factorConstant) < sF.getHousePrice()) && (sF.getHousePrice() < friend.getHousePrice()) || 
						(friend.getHousePrice() * (1+factorConstant) > sF.getHousePrice()) && (sF.getHousePrice() > friend.getHousePrice())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 2: if ((sF.getGCSEScore()*(1-factorConstant) < friend.getGCSEScore()) && (sF.getGCSEScore() > friend.getGCSEScore()) || 
						(sF.getGCSEScore() * (1+factorConstant) > friend.getGCSEScore()) && (sF.getGCSEScore() < friend.getGCSEScore())  || 
						(friend.getGCSEScore()*(1-factorConstant) < sF.getGCSEScore()) && (sF.getGCSEScore() < friend.getGCSEScore()) || 
						(friend.getGCSEScore() * (1+factorConstant) > sF.getGCSEScore()) && (sF.getGCSEScore() > friend.getGCSEScore())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 3: if ((sF.getTransportRating()*(1-factorConstant) < friend.getTransportRating()) && (sF.getTransportRating() > friend.getTransportRating()) || 
						(sF.getTransportRating() * (1+factorConstant) > friend.getTransportRating()) && (sF.getTransportRating() < friend.getTransportRating())  || 
						(friend.getTransportRating()*(1-factorConstant) < sF.getTransportRating()) && (sF.getTransportRating() < friend.getTransportRating()) || 
						(friend.getTransportRating() * (1+factorConstant) > sF.getTransportRating()) && (sF.getTransportRating() > friend.getTransportRating())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 4: if ((sF.getSchoolAbscences()*(1-factorConstant) < friend.getSchoolAbscences()) && (sF.getSchoolAbscences() > friend.getSchoolAbscences()) || 
						(sF.getSchoolAbscences() * (1+factorConstant) > friend.getSchoolAbscences()) && (sF.getSchoolAbscences() < friend.getSchoolAbscences())  || 
						(friend.getSchoolAbscences()*(1-factorConstant) < sF.getSchoolAbscences()) && (sF.getSchoolAbscences() < friend.getSchoolAbscences()) || 
						(friend.getSchoolAbscences() * (1+factorConstant) > sF.getSchoolAbscences()) && (sF.getSchoolAbscences() > friend.getSchoolAbscences())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 5: if ((sF.getIncomeSupport()*(1-factorConstant) < friend.getIncomeSupport()) && (sF.getIncomeSupport() > friend.getIncomeSupport()) || 
				(sF.getIncomeSupport() * (1+factorConstant) > friend.getIncomeSupport()) && (sF.getIncomeSupport() < friend.getIncomeSupport())  || 
				(friend.getIncomeSupport()*(1-factorConstant) < sF.getIncomeSupport()) && (sF.getIncomeSupport() < friend.getIncomeSupport()) || 
				(friend.getIncomeSupport() * (1+factorConstant) > sF.getIncomeSupport()) && (sF.getIncomeSupport() > friend.getIncomeSupport())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 6: if ((sF.getUnemploymentRate()*(1-factorConstant) < friend.getUnemploymentRate()) && (sF.getUnemploymentRate() > friend.getUnemploymentRate()) || 
						(sF.getUnemploymentRate() * (1+factorConstant) > friend.getUnemploymentRate()) && (sF.getUnemploymentRate() < friend.getUnemploymentRate())  || 
						(friend.getUnemploymentRate()*(1-factorConstant) < sF.getUnemploymentRate()) && (sF.getUnemploymentRate() < friend.getUnemploymentRate()) || 
						(friend.getUnemploymentRate() * (1+factorConstant) > sF.getUnemploymentRate()) && (sF.getUnemploymentRate() > friend.getUnemploymentRate())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 7: if ((sF.getChildInNoWorkHouse()*(1-factorConstant) < friend.getChildInNoWorkHouse()) && (sF.getChildInNoWorkHouse() > friend.getChildInNoWorkHouse()) || 
						(sF.getChildInNoWorkHouse() * (1+factorConstant) > friend.getChildInNoWorkHouse()) && (sF.getChildInNoWorkHouse() < friend.getChildInNoWorkHouse())  || 
						(friend.getChildInNoWorkHouse()*(1-factorConstant) < sF.getChildInNoWorkHouse()) && (sF.getChildInNoWorkHouse() < friend.getChildInNoWorkHouse()) || 
						(friend.getChildInNoWorkHouse() * (1+factorConstant) > sF.getChildInNoWorkHouse()) && (sF.getChildInNoWorkHouse() > friend.getChildInNoWorkHouse())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 8: if ((sF.getDeliberateFires()*(1-factorConstant) < friend.getDeliberateFires()) && (sF.getDeliberateFires() > friend.getDeliberateFires()) || 
				(sF.getDeliberateFires() * (1+factorConstant) > friend.getDeliberateFires()) && (sF.getDeliberateFires() < friend.getDeliberateFires())  || 
				(friend.getDeliberateFires()*(1-factorConstant) < sF.getDeliberateFires()) && (sF.getDeliberateFires() < friend.getDeliberateFires()) || 
				(friend.getDeliberateFires() * (1+factorConstant) > sF.getDeliberateFires()) && (sF.getDeliberateFires() > friend.getDeliberateFires())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 9: if ((sF.getIncapacityBenefit()*(1-factorConstant) < friend.getIncapacityBenefit()) && (sF.getIncapacityBenefit() > friend.getIncapacityBenefit()) || 
				(sF.getIncapacityBenefit() * (1+factorConstant) > friend.getIncapacityBenefit()) && (sF.getIncapacityBenefit() < friend.getIncapacityBenefit())  || 
				(friend.getIncapacityBenefit()*(1-factorConstant) < sF.getIncapacityBenefit()) && (sF.getIncapacityBenefit() < friend.getIncapacityBenefit()) || 
				(friend.getIncapacityBenefit() * (1+factorConstant) > sF.getIncapacityBenefit()) && (sF.getIncapacityBenefit() > friend.getIncapacityBenefit())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		}
		return combined;
	}

	//get the social factors for a hash tag
	public String hashTags(String tag1, String time) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection hashTagInfo;
		
		if (time == null) {
			hashTagInfo = db.getCollection(CollectionReader.returnName("tagInfo"));
		}
		else {
			hashTagInfo = db.getCollection("tagInfo_" + time);
		}
		
		BasicDBObject query = new BasicDBObject("locations", new BasicDBObject("ward0", tag1));
		SocialFactors tag1Factors = new SocialFactors(hashTagInfo.findOne(query));
		
		ArrayList<SocialFactors> listOfData = new ArrayList<SocialFactors>();
		listOfData.add(tag1Factors);
		
		Gson gson = new Gson();
		mongoClient.close();
		return gson.toJson(listOfData);
	}

	//return all hash tags lists
	public String hashTagList(String time) throws UnknownHostException {
			return HashTag.getTagList(time);
	}

	//return proportions of devices for a chosen borough
	public String devicesForBorough(String code, String time) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll;
		
		if (time == null) {
			coll = db.getCollection(CollectionReader.returnName("devicesForBoroughs"));
		}
		else {
			coll = db.getCollection("devicesForBoroughs_" + time);
		}
		
		Gson gson = new Gson();
		
		BasicDBObject query = new BasicDBObject("borough", code);
		String ret = gson.toJson(coll.find(query).next().toString());
		mongoClient.close();
		return ret;
	}
	
	//get all devices stored in time stamp
	public String getDevice(String time) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll;
		
		if (time == null) {
			coll = db.getCollection(CollectionReader.returnName("deviceList"));
		}
		else {
			coll = db.getCollection("deviceList_" + time);
		}
		
		Gson gson = new Gson();
		String ret = gson.toJson(coll.findOne().get("list"));
		mongoClient.close();
	
		return ret;
	}

	//get social factors for a device
	public String getDeviceFactors(String deviceName, String time) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll ;
		
		if (time == null) {
			coll = db.getCollection(CollectionReader.returnName("deviceFactors"));
		}
		else {
			coll = db.getCollection("deviceFactors_"+time);
		}
		
		BasicDBObject query = new BasicDBObject("locations", new BasicDBObject("ward0", deviceName));
		
		Gson gson = new Gson();
		String ret = gson.toJson(coll.find(query).next().toString());
		mongoClient.close();
		return ret;
	}

	//return explanation of factor names
	public String getFactorList() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> entry = new HashMap<String, String>();
		entry.put("id", "0");
		entry.put("factor", "Crime Rate");
		entry.put("measure", "Recorded offenses per 1000 people");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "1");
		entry.put("factor", "House Price");
		entry.put("measure", "Pounds");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "2");
		entry.put("factor", "GCSE Score");
		entry.put("measure", "Average number of GCSE points");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "3");
		entry.put("factor", "Transport Rating");
		entry.put("measure", "Average Public Transport Accessibility score");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "4");
		entry.put("factor", "School Abscences");
		entry.put("measure", "Percentage of unauthorised school abscences for all schools");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "5");
		entry.put("factor", "Income Support");
		entry.put("measure", "Income support rate index");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "6");
		entry.put("factor", "Unemployment Rate");
		entry.put("measure", "Percentage of population unemployed");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "7");
		entry.put("factor", "Dependent children in out-of-work families");
		entry.put("measure", "Number of children living in a house with no one employed");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "8");
		entry.put("factor", "Deliberate Fires");
		entry.put("measure", "Number of deliberate fires per 1000 people");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "9");
		entry.put("factor", "Incapacity Benefit");
		entry.put("measure", "Incapacity claimant rate index");
		list.add(entry);
		
		Gson gson = new Gson();
		return gson.toJson(list);
	}

	//get all time stamps within database
	public String getTimes() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "updates" );
		DBCollection coll = db.getCollection("times");
		
		DBCursor dbc = coll.find();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		String time;
		while(dbc.hasNext()) {
			entry = new HashMap<String, String>();
			time = (String) dbc.next().get("date");
			entry.put("date",time);
			list.add(entry);
		}
		
		Gson gson = new Gson();
		mongoClient.close();
		return gson.toJson(list);
	}
	
	//return the sentiment scores for a given area, tag or device
	public String getSentiment(String code, String time, String collection)throws UnknownHostException{
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll ;
		Gson gson = new Gson();
		
		HashMap<String, Object> map = new HashMap<String, Object>();

		if (time == null) {
			coll = db.getCollection(new CollectionReader().returnName(collection));
		}
		else {
			coll = db.getCollection(collection +"_"+time);
		}
		
		BasicDBObject query = new BasicDBObject("code", code);
		
		DBCursor dbc = coll.find(query);

		//System.out.println(coll.find().next());
		BasicDBObject dbo = (BasicDBObject) dbc.next();
		
		map.put("code", dbo.get("code"));
		map.put("activation", dbo.get("activation"));
		map.put("imagery", dbo.get("imagery"));
		map.put("pleasantness", dbo.get("pleasantness"));
		map.put("frequency", dbo.get("frequency"));
		mongoClient.close();
		return gson.toJson(map);
	}
	
	//get the word lists and their sentiment for given area, tag or device
	public String getWords(int wordCode, String code, String time, String collection) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll ;
		Gson gson = new Gson();
		
		HashMap<String, Object> result = new HashMap<String, Object>();

		if (time == null) {
			coll = db.getCollection(new CollectionReader().returnName(collection));
		}
		else {
			coll = db.getCollection(collection +"_"+time);
		}
		
		BasicDBObject query = new BasicDBObject("code", code);
		
		DBCursor dbc = coll.find(query);
		BasicDBObject dbo = (BasicDBObject) dbc.next();
		System.out.println("About to make");
		AreaWords aw = new AreaWords(dbo);
		System.out.println("Made the aW");
		
		result.put("locations", aw.getCode());
		
		switch (wordCode) {
		case 0:
			result.put("nouns", aw.getNouns());
			result.put("adjective", aw.getAdjective());
			result.put("verb", aw.getVerb());
			result.put("DAL", aw.getDAL());
			result.put("Catagories", aw.getCatagories());
			break;
		case 1:
			result.put("nouns", aw.getNouns());
			break;
		case 2:
			result.put("adjective", aw.getAdjective());
			break;
		case 3:
			result.put("verb", aw.getVerb());
			break;
		case 4:
			result.put("DAL", aw.getDAL());
			break;
		case 5:
			result.put("Catagories", aw.getCatagories());
			break;
		}
		
		mongoClient.close();
		return gson.toJson(result);
	}

	//return social factors for a single area
	public String getAreaFactors(String code, String year) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DBCollection coll;
		DB db = mongoClient.getDB( "areas" );
		BasicDBObject query = new BasicDBObject("locations", new BasicDBObject("ward0", code));
		
		if (!year.equals("2008")&&!year.equals("2009")&&!year.equals("2010")&&!year.equals("2011")&&!year.equals("2012")) {
			year = "2012";
		}
		
		if (code.length() > 4){//get ward info
			coll = db.getCollection("wards" + year);
		}
		else {//get borough info
			coll = db.getCollection("boroughs" + year);
		}
		
		SocialFactors sF = new SocialFactors(coll.findOne(query));
		mongoClient.close();
		return new Gson().toJson(sF);
	}
	//get a hashtag list for an area
	public String getAreaTags(String code, String time) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll ;
		Gson gson = new Gson();
		
		if (time == null) {
			coll = db.getCollection(new CollectionReader().returnName("areaHashtags"));
		}
		else {
			coll = db.getCollection("areaHashtags_"+time);
		}
		
		BasicDBObject query = new BasicDBObject("code", code);
		String ret = gson.toJson(coll.find(query).next().toString());
		mongoClient.close();
		return ret;
	}
	//get a device list for every area without the proportions
	public String getAreaDevices(String code, String time) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll ;
		Gson gson = new Gson();
		
		if (time == null) {
			coll = db.getCollection(new CollectionReader().returnName("areaDevices"));
		}
		else {
			coll = db.getCollection("areaDevices_"+time);
		}
		
		BasicDBObject query = new BasicDBObject("code", code);
		String ret = gson.toJson(coll.find(query).next().toString());
		mongoClient.close();
		return ret;
	}


}
