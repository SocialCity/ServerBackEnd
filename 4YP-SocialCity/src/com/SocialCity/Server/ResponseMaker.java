package com.SocialCity.Server;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.SocialCity.Area.BoundaryMap;
import com.SocialCity.DataParsers.CollectionReader;
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
	
	public String oneFactor(int factorNumber, boolean useWards, boolean combine, boolean all) {
		ArrayList<SocialFactors> listOfData = createFactors(factorNumber, useWards, combine);
		Gson gson = new Gson();
		
		if (all) {
			return gson.toJson(listOfData);//convert list to JSON for return
		}
		else {
			return gson.toJson(singleFactorOnly(listOfData, factorNumber));
		}
		
	}
	
	public String twoFactors(int factorNumber1, int factorNumber2, boolean useWards, boolean combine, boolean all) {
		ArrayList<SocialFactors> listOfData = createFactors(factorNumber1, useWards, combine);
		Gson gson = new Gson();
		
		if (all) {
			return gson.toJson(listOfData);
		}
		else {
			return gson.toJson(twoFactorsOnly(listOfData, factorNumber1, factorNumber2));
		}
	}
	
	public ArrayList<SocialFactors> createFactors(int factorNumber, boolean useWards, boolean combine) {
		
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
		
		try {
			//gets the database
			mongoClient = new MongoClient("localhost");
			//mongoClient.dropDatabase("areas");
			DB db = mongoClient.getDB( "areas" );
			
			if (useWards){//get ward info
				coll = db.getCollection(CollectionReader.returnName("wardsCollection"));
			}
			else {//get borough info
				coll = db.getCollection(CollectionReader.returnName("boroughsCollection"));
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
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listOfData;
	}
	
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
		case 2: if ((sF.getEducationRating()*(1-factorConstant) < friend.getEducationRating()) && (sF.getEducationRating() > friend.getEducationRating()) || 
						(sF.getEducationRating() * (1+factorConstant) > friend.getEducationRating()) && (sF.getEducationRating() < friend.getEducationRating())  || 
						(friend.getEducationRating()*(1-factorConstant) < sF.getEducationRating()) && (sF.getEducationRating() < friend.getEducationRating()) || 
						(friend.getEducationRating() * (1+factorConstant) > sF.getEducationRating()) && (sF.getEducationRating() > friend.getEducationRating())){
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
		case 4: if ((sF.getMeanAge()*(1-factorConstant) < friend.getMeanAge()) && (sF.getMeanAge() > friend.getMeanAge()) || 
						(sF.getMeanAge() * (1+factorConstant) > friend.getMeanAge()) && (sF.getMeanAge() < friend.getMeanAge())  || 
						(friend.getMeanAge()*(1-factorConstant) < sF.getMeanAge()) && (sF.getMeanAge() < friend.getMeanAge()) || 
						(friend.getMeanAge() * (1+factorConstant) > sF.getMeanAge()) && (sF.getMeanAge() > friend.getMeanAge())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 5: if ((sF.getDrugRate()*(1-factorConstant) < friend.getDrugRate()) && (sF.getDrugRate() > friend.getDrugRate()) || 
						(sF.getDrugRate() * (1+factorConstant) > friend.getDrugRate()) && (sF.getDrugRate() < friend.getDrugRate())  || 
						(friend.getDrugRate()*(1-factorConstant) < sF.getDrugRate()) && (sF.getDrugRate() < friend.getDrugRate()) || 
						(friend.getDrugRate() * (1+factorConstant) > sF.getDrugRate()) && (sF.getDrugRate() > friend.getDrugRate())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 6: if ((sF.getEmploymentRate()*(1-factorConstant) < friend.getEmploymentRate()) && (sF.getEmploymentRate() > friend.getEmploymentRate()) || 
						(sF.getEmploymentRate() * (1+factorConstant) > friend.getEmploymentRate()) && (sF.getEmploymentRate() < friend.getEmploymentRate())  || 
						(friend.getEmploymentRate()*(1-factorConstant) < sF.getEmploymentRate()) && (sF.getEmploymentRate() < friend.getEmploymentRate()) || 
						(friend.getEmploymentRate() * (1+factorConstant) > sF.getEmploymentRate()) && (sF.getEmploymentRate() > friend.getEmploymentRate())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 7: if ((sF.getVoteTurnout()*(1-factorConstant) < friend.getVoteTurnout()) && (sF.getVoteTurnout() > friend.getVoteTurnout()) || 
						(sF.getVoteTurnout() * (1+factorConstant) > friend.getVoteTurnout()) && (sF.getVoteTurnout() < friend.getVoteTurnout())  || 
						(friend.getVoteTurnout()*(1-factorConstant) < sF.getVoteTurnout()) && (sF.getVoteTurnout() < friend.getVoteTurnout()) || 
						(friend.getVoteTurnout() * (1+factorConstant) > sF.getVoteTurnout()) && (sF.getVoteTurnout() > friend.getVoteTurnout())){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 8: break;
		
		}
		return combined;
	}

	public String hashTags(String tag1, String tag2) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection hashTagInfo = db.getCollection(CollectionReader.returnName("tagInfo"));
		
		
		BasicDBObject query = new BasicDBObject("locations", new BasicDBObject("ward0", tag1));
		SocialFactors tag1Factors = new SocialFactors(hashTagInfo.findOne(query));
		
		query = new BasicDBObject("locations", new BasicDBObject("ward0", tag2));
		SocialFactors tag2Factors = new SocialFactors(hashTagInfo.findOne(query));
		
		ArrayList<SocialFactors> listOfData = new ArrayList<SocialFactors>();
		listOfData.add(tag1Factors);
		listOfData.add(tag2Factors);
		
		Gson gson = new Gson();
		return gson.toJson(listOfData);
	}

	public String hashTagList() throws UnknownHostException {
			return HashTag.getTagList();
	}

	public String devicesForBorough(String code) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll;
		coll = db.getCollection(CollectionReader.returnName("devicesForBoroughs"));
		Gson gson = new Gson();
		
		BasicDBObject query = new BasicDBObject("borough", code);
		
		return gson.toJson(coll.find(query).next().toString());
	}

	public String getDevice() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll = db.getCollection(CollectionReader.returnName("deviceList"));
		Gson gson = new Gson();
		return gson.toJson(coll.findOne().get("list"));
	}

	public String getDeviceFactors(String deviceName) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll = db.getCollection(CollectionReader.returnName("deviceFactors"));
		
		BasicDBObject query = new BasicDBObject("locations", new BasicDBObject("ward0", deviceName));
		
		DBCursor d = coll.find();
		while (d.hasNext()) {System.out.println(d.next());}
		Gson gson = new Gson();
		return gson.toJson(coll.find(query).next().toString());
	}

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
		entry.put("factor", "Education Rating");
		entry.put("measure", "Percentage of people with at least level 4 qualifications");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "3");
		entry.put("factor", "Transport Rating");
		entry.put("measure", "Average Public Transport Accessibility score");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "4");
		entry.put("factor", "Mean Age");
		entry.put("measure", "Years");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "5");
		entry.put("factor", "Drug Rate");
		entry.put("measure", "Estimated number of problem drug users (crack and or opiates) per 1000 of the population");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "6");
		entry.put("factor", "Employment Rate");
		entry.put("measure", "Percentage of population in employment");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "7");
		entry.put("factor", "Vote Turnout");
		entry.put("measure", "Percentage of voter turnout at mayoral elections");
		list.add(entry);
		
		entry = new HashMap<String, String>();
		entry.put("id", "8");
		entry.put("factor", "Tweet Proportion");
		entry.put("measure", "Ratio of tweets from borough compared to the whole of London");
		list.add(entry);
		
		Gson gson = new Gson();
		return gson.toJson(list);
	}

}
