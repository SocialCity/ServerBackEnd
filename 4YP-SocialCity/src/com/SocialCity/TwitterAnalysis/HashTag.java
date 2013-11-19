package com.SocialCity.TwitterAnalysis;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.SocialFactor.SocialFactors;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

public class HashTag {
	
	public HashTag(){}
	
	public static void topHashTags() throws UnknownHostException{
		HashMap<String, Integer> tagMap = new HashMap<String, Integer> ();
		int count;
		BasicDBObject dbo;
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = null;
		DBCollection sortedColl = null;
		DBCollection tagList = null;
		coll = db.getCollection("tweets");
		

		BasicDBObject query = new BasicDBObject();
		query.put("text",new BasicDBObject("$regex", String.format(".*((?i)%s).*", "#")));
		DBCursor dbC = coll.find(query);
		
		while (dbC.hasNext()) {
			String text = (String) (dbC.next().get("text"));
			Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
			Matcher mat = MY_PATTERN.matcher(text);
			while (mat.find()) {
				count = 1;
				if (tagMap.containsKey(mat.group(1).toLowerCase())) {
					count = tagMap.get(mat.group(1).toLowerCase());
			        count++;
			        
				}
				tagMap.put(mat.group(1).toLowerCase(), count);
			}
		}
		
		for (String k : tagMap.keySet()) {
			System.out.println(k + "  " + tagMap.get(k));
		}
		
		
		coll = db.createCollection("hashTag", null);
		coll.drop();
		
		for (String k : tagMap.keySet()) {
			dbo = new BasicDBObject();
			dbo.put("hashtag", k);
			dbo.put("count", tagMap.get(k));
			coll.insert(dbo);
		}
		
		dbC = coll.find().sort(new BasicDBObject("count", -1));
		sortedColl = db.getCollection("topHashTags");
		sortedColl.drop();
		sortedColl = db.createCollection("topHashTags", null);
		tagList = db.getCollection("tagList");
		tagList.drop();
		tagList = db.createCollection("tagList", null);
		BasicDBObject tag;
		ArrayList<String> names = new ArrayList<String>();
		while (dbC.hasNext()) {
			tag = (BasicDBObject) dbC.next();
			sortedColl.insert(tag);
			names.add(tag.getString("hashtag"));
		}
		
		tag = new BasicDBObject();
		tag.put("tags", names);
		tagList.insert(tag);
		mongoClient.close();
	} 
	
	public static String getTagList() throws UnknownHostException {
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = db.getCollection("tagList");
		
		ArrayList<String> names = (ArrayList<String>) ((BasicDBObject) coll.findOne()).get("tags");
		
		Gson gson = new Gson();
		return gson.toJson(names);
	}
	
	public static void tagLocationInfo() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = db.getCollection("topHashTags");
		DBCollection tweets = db.getCollection("tweets");
		
		DBCollection hashTagInfo = db.getCollection("tagInfo");
		hashTagInfo.drop();
		hashTagInfo = db.createCollection("tagInfo", null);
		int tagCount = 0;
		boolean relevant;
		DBCursor dbC = coll.find();
		DBCursor results;
		DB areas = mongoClient.getDB("areas");
		DBCollection boroughs = areas.getCollection("boroughs");
		HashMap<String, Integer> placeRatio;
		String placeName;
		CodeNameMap cnm = new CodeNameMap();
		SocialFactors boroughFactors;
		SocialFactors tagFactors;
		
		while (dbC.hasNext() && tagCount < 100) {
			
			BasicDBObject hashTag = (BasicDBObject) dbC.next();
			BasicDBObject query = new BasicDBObject();
			BasicDBObject places = new BasicDBObject();
			query.put("text",new BasicDBObject("$regex", String.format(".*((?i)%s).*", "#"+hashTag.getString("hashtag"))));
			results = tweets.find(query);
			int count;
			int total=0;
			tagFactors = new SocialFactors(hashTag.getString("hashtag"));
			placeRatio = new HashMap<String, Integer>();
			System.out.println("----------");
			System.out.println(hashTag.getString("hashtag"));
			
			while (results.hasNext()){
				places = (BasicDBObject)results.next().get("place");
				placeName = places.getString("name");
				
				count = 1;
				if (placeRatio.containsKey(placeName)) {
					count = placeRatio.get(placeName);
			        count++;
				}
				placeRatio.put(placeName, count);
				
			}
			//System.out.println(placeRatio);
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
					//System.out.println(k);
					//System.out.println(count);
					//System.out.println(total);
					//System.out.println(boroughFactors.getDBObject());
				}
			}
			
			if (relevant) {
				tagFactors.setCrimeRate(crimeRate/total);
				tagFactors.setDrugRate(drugRate/total);
				tagFactors.setEducationRating(educationRating/total);
				tagFactors.setEmploymentRate(employmentRate/total);
				tagFactors.setHousePrice(housePrice/total);
				tagFactors.setMeanAge(meanAge/total);
				tagFactors.setTransportRating(transportRating/total);
				tagFactors.setVoteTurnout(voteTurnout/total);
				System.out.println(tagFactors.getDBObject());
				hashTagInfo.insert(tagFactors.getDBObject());
				tagCount++;
				System.out.println(tagCount);
				relevant = false;
			}
		}
		
	}
	

}
