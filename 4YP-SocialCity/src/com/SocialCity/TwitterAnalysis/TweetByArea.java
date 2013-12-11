package com.SocialCity.TwitterAnalysis;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bson.BasicBSONObject;

import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.SocialFactor.SocialFactors;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class TweetByArea {

	CodeNameMap codeNameMap;
	/**
	 * @param args
	 */
	public TweetByArea(){
		 codeNameMap = new CodeNameMap();
	}
	
	//will return either all the tweets for a borough or devices used
	public ArrayList<String> getTweetsForBorough(String boroughCode, boolean device) throws UnknownHostException {
		BasicDBObject query;
		String name = codeNameMap.getName(boroughCode);
		System.out.println(name);
		ArrayList<String> tweetText = new ArrayList<String>();
		
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = null;
		coll = db.getCollection("tweets");
		
		System.out.println(name);
		query = new BasicDBObject("place.name", name);
		DBCursor tweets = coll.find(query);
		//System.out.println(tweets.count());
		int count = 0;
		for (DBObject dbo : tweets) {
			if (!device) {
				tweetText.add((String) dbo.get("text"));
			}
			else {
				tweetText.add((String) dbo.get("source"));
			}
		}

		
		return tweetText;
	}
	
	//calculates how much each borough contributes to the total tweet count available
	public HashMap<String, Double> tweetProportion() throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<String> list;
		HashMap<String, Double> tweetCount = new HashMap<String, Double>();
		int total = 0;
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {//retrieves the tweets for each borough
				list = getTweetsForBorough(key, false);
				total = total + list.size();//count total tweets found in london
				tweetCount.put(key, (double) list.size());
			}
		}
		//get the ration rather then actual number of tweets for a borough
		for (String key : tweetCount.keySet()) {
			tweetCount.put(key, (tweetCount.get(key)/total));
		}
		
		return tweetCount;
	}

	
	public HashMap<String, Double> reTweets() throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<String> list;
		HashMap<String, Double> tweetCount = new HashMap<String, Double>();
		double count = 0;
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {
				list = getTweetsForBorough(key, false);
				for (String tweet : list) {
					if (tweet.contains("RT ")){
						count++;
					}
				}
				tweetCount.put(key, count/list.size());
			}
		}
		
		return tweetCount;
	}
	
	//create a social factors object for devices
	public void deviceFactors() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection tweets = db.getCollection("tweets");
		DB db2 = mongoClient.getDB( "deviceBreakdown" );
		DBCollection deviceStore = db2.getCollection("deviceFactors");
		deviceStore.drop();
		deviceStore = db2.getCollection("deviceFactors");
		DB areas = mongoClient.getDB("areas");
		DBCollection boroughs = areas.getCollection("boroughs");
		
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		DBCursor results;
		HashSet<String> devices = getDevices();
		HashMap<String, Integer> placeRatio;
		BasicDBObject places;
		BasicDBObject combiner;
		String placeName;
		int count = 1;
		CodeNameMap cnm = new CodeNameMap();
		SocialFactors deviceFactors;
		
		boolean relevant;
		SocialFactors boroughFactors;
		int total;
		
		for (String device : devices) {
			System.out.println("----------");
			System.out.println(device);
			String newName = formatDeviceName(device);
			
			BasicDBObject query = new BasicDBObject();
			query.append("source", device);
			placeRatio = new HashMap<String, Integer>();
			results = tweets.find(query);//find all tweets using current device
			deviceFactors = new SocialFactors(newName.trim());
			total=0;
			
			while (results.hasNext()){
				places = (BasicDBObject)results.next().get("place");
				placeName = "";
				if (places != null) {//need to get name for place tweet occured, make sure its a borough
					placeName = places.getString("name");
				}
				else {
					continue;
				}
				count = 1;
				//increase total for the current place for the device
				if (placeRatio.containsKey(placeName)) {
					count = placeRatio.get(placeName);
			        count++;
				}
				placeRatio.put(placeName, count);
			}
			
			double crimeRate = 0;
			double housePrice = 0;
			double educationRating = 0;
			double transportRating = 0;
			double meanAge = 0;
			double drugRate = 0;
			double employmentRate = 0;
			double voteTurnout = 0;
			
			relevant = false;
			for (String k : placeRatio.keySet()) {
				if (cnm.getCode(k) != null) {
					query = new BasicDBObject("locations", new BasicDBObject("ward0", cnm.getCode(k).substring(0, 4)));
					boroughFactors = new SocialFactors((BasicDBObject) boroughs.findOne(query));
					count = placeRatio.get(k);
					relevant = true;
					crimeRate = crimeRate + (boroughFactors.getCrimeRate()*count);
					housePrice = housePrice + (boroughFactors.getHousePrice()*count);
					educationRating = educationRating + (boroughFactors.getEducationRating()*count);
					transportRating = transportRating + (boroughFactors.getTransportRating()*count);
					meanAge = meanAge + (boroughFactors.getMeanAge()*count);
					drugRate = drugRate + (boroughFactors.getDrugRate()*count);
					employmentRate = employmentRate + (boroughFactors.getEmploymentRate()*count);
					voteTurnout = voteTurnout + (boroughFactors.getVoteTurnout()*count);
					
					total=total+count;
					System.out.println(k);
					System.out.println(count);
					System.out.println(total);
					System.out.println(boroughFactors.getDBObject());
				}
			}
			
			if (relevant) {
				deviceFactors.setCrimeRate(crimeRate/total);
				deviceFactors.setDrugRate(drugRate/total);
				deviceFactors.setEducationRating(educationRating/total);
				deviceFactors.setEmploymentRate(employmentRate/total);
				deviceFactors.setHousePrice(housePrice/total);
				deviceFactors.setMeanAge(meanAge/total);
				deviceFactors.setTransportRating(transportRating/total);
				deviceFactors.setVoteTurnout(voteTurnout/total);
				System.out.println(deviceFactors.getDBObject());
				
				query = new BasicDBObject("locations", new BasicDBObject("ward0", newName));
				
				try {
					combiner = (BasicDBObject) deviceStore.find(query).next();
				} catch (Exception e) {
					combiner = null;
				}
				
				if (combiner != null) {
					deviceFactors.combineLocations(new SocialFactors(combiner));
					deviceStore.remove(combiner);
				}
				
				deviceStore.insert(deviceFactors.getDBObject());
				relevant = false;
			}
			
			
		}
		
	}
	
	public String formatDeviceName(String source) {
		if (!source.equals("web")) {
			source = source.substring(0, source.length() - 4);
			source = source.substring(source.lastIndexOf(">")+1);
			source = source.replace(".", "DOT");
			source = source.replace("®", "");
			source = source.trim();
			source = source.replaceAll("\\s+"," ");
			source = source.replace(" ", "_");
			if (source.startsWith("Plume")) {
				source = "Plume_for_Android";
			}
		}
		return source;
	}

	public void createDeviceList() throws UnknownHostException {
		HashSet<String> deviceList = getDevices();
		HashSet<String> deviceListFormatted = new HashSet<String>();
	
		for (String s : deviceList) {
			deviceListFormatted.add(formatDeviceName(s));
		}
		
		MongoClient mongoClient = new MongoClient("localhost");
		DB db2 = mongoClient.getDB( "deviceBreakdown" );
		
		DBCollection coll = db2.getCollection("deviceList");
		coll.drop();
		coll = db2.getCollection("deviceList");
		
		BasicDBObject insert = new BasicDBObject().append("list", deviceListFormatted);
		coll.insert(insert);
		System.out.println(insert);
	}
	//retrieves all devices used in tweets
	public HashSet<String> getDevices() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection tweets = db.getCollection("tweets");
		DBCursor dBC = tweets.find();
		CodeNameMap cnm = new CodeNameMap();
		HashSet<String> devices = new HashSet<String>();
		while (dBC.hasNext()) {
			BasicDBObject deviceName = (BasicDBObject) dBC.next();
			String place = "";
			
			try {
				BasicDBObject places = (BasicDBObject)dBC.next().get("place");
				place = places.getString("name");
			} catch (Exception e) {continue;}
			
			System.out.println(place);
			if (cnm.getCode(place) != null) {
				devices.add(deviceName.getString("source"));
			}
		}
		return devices;
	}

	public void deviceBreakdown() throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<String> list;
		HashMap<String, Double> sourceCount = new HashMap<String, Double>();
		HashMap<String, Double> sourceProportion = new HashMap<String, Double>();
		double count = 0;
		
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll;
		
		coll = db.getCollection("devicesForBoroughs");
		coll.drop();
		coll = db.getCollection("devicesForBoroughs");
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {
				
				list = getTweetsForBorough(key, true);
				sourceProportion = new HashMap<String, Double>();
				sourceCount = new HashMap<String, Double>();
				
				for (String source : list) {
					if (!source.equals("web")) {
						source = formatDeviceName(source);
						System.out.println(source);
					}
					if (sourceCount.containsKey(source)) {
						count = sourceCount.get(source) + 1;
						sourceCount.put(source, count);
					}
					else {
						sourceCount.put(source, (double) 1);
					}
				}
				
				for (String source : sourceCount.keySet()) {
					count = sourceCount.get(source) / list.size();
					sourceProportion.put(source, count);
					System.out.println(source);
				}
				
				BasicDBObject insertObject = new BasicDBObject();
				insertObject.append("borough", key);
				insertObject.append("sources", sourceProportion);
				coll.insert(insertObject);
			}
		}
	}
	
}












