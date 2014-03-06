package com.SocialCity.TwitterAnalysis;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bson.BasicBSONObject;

import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.DataParsers.CollectionReader;
import com.SocialCity.SocialFactor.AreaWords;
import com.SocialCity.SocialFactor.SocialFactors;
import com.SocialCity.SocialFactor.Word;
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
	//tweetName can be null, it is not null when databases being switched
	public ArrayList<DBObject> getTweetsForBorough(String boroughCode, String tweetName) throws UnknownHostException {
		BasicDBObject query;
		String name = codeNameMap.getName(boroughCode);
		System.out.println(name);
		ArrayList<DBObject> tweetList = new ArrayList<DBObject>();
		
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = null;
		
		if (tweetName == null) {
			coll = db.getCollection(CollectionReader.returnName("tweets"));
		}
		else {
			coll = db.getCollection(tweetName);
		}
		
		System.out.println(name);
		query = new BasicDBObject("place.name", name);
		DBCursor tweets = coll.find(query);
		//System.out.println(tweets.count());
		int count = 0;
		for (DBObject dbo : tweets) {
			tweetList.add(dbo);
		}

		
		return tweetList;
	}
	
	//calculates how much each borough contributes to the total tweet count available
	public HashMap<String, Double> tweetProportion(String tweetName) throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<DBObject> list;
		HashMap<String, Double> tweetCount = new HashMap<String, Double>();
		int total = 0;
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {//retrieves the tweets for each borough
				list = getTweetsForBorough(key, tweetName);
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

	
	public HashMap<String, Double> reTweets(String tweetName) throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<DBObject> list;
		HashMap<String, Double> tweetCount = new HashMap<String, Double>();
		double retweetCount = 0;
		double total = 0;
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection tweets = db.getCollection("retweets");
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {
				list = getTweetsForBorough(key, tweetName);
				total = list.size();
				for (DBObject tweet : list) {
					if (tweet.containsField("retweeted_status")) {
						retweetCount++;
						System.out.println("anything");
					}
				}
				if (total == 0)
					total = 1;
				
				tweetCount.put(key, (double) retweetCount/total);
			}
		}
		
		return tweetCount;
	}
	
	public void tweetProportions(String propName, String tweetName) throws UnknownHostException{
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		

		DBCollection tweets = db.createCollection(propName, null);

		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		HashMap<String, Double> tweetProportion = tweetProportion(tweetName);
		HashMap<String, Double> retweetProportion = reTweets(tweetName);
		DBObject proportions;
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {
				proportions = new BasicDBObject();
				proportions.put("location", key);
				proportions.put("tweets_global_proportion", tweetProportion.get(key));
				proportions.put("retweets_local_proportion", retweetProportion.get(key));
				tweets.insert(proportions);
			}
		}
		
	}
	
	//create a social factors object for devices
	public void deviceFactors(String tweetName, String devFacName, String devWordsName, String wordName) throws UnknownHostException {
		
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		
		DBCollection tweets = db.getCollection(tweetName);
		DB db2 = mongoClient.getDB( "deviceBreakdown" );
		
		DBCollection wordStore = db.getCollection(wordName);
		DBCollection deviceStore = db2.getCollection(devFacName);
		DBCollection newColl = db.createCollection(devWordsName, null);
		DB areas = mongoClient.getDB("areas");
		DBCollection boroughs = areas.getCollection("boroughs2012");
		ArrayList<Tweet_Obj> tw_list;
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		DBCursor results;
		HashSet<String> devices = getDevices(tweetName);
		HashMap<String, Integer> placeRatio;
		BasicDBObject places;
		BasicDBObject combiner;
		BasicDBObject dbo;
		String placeName;
		int count = 1;
		CodeNameMap cnm = new CodeNameMap();
		SocialFactors deviceFactors;
		String text;
		boolean relevant;
		SocialFactors boroughFactors;
		int total;
		ArrayList<Word_Stats> words;
		Tweet_Info_Bloc resultBlock;
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt", "resources/wordnet-core-words.txt", "resources/Categories.txt");
		double act = 0;
		double image = 0;
		double valience = 0;
		BasicDBObject map;
		ArrayList<Word_Stats> adj;
		ArrayList<Word_Stats> noun;
		ArrayList<Word_Stats> verb;
		ArrayList<Word_Stats> dal;
		ArrayList<Word_Stats> cats;
		ArrayList<Word> adjW = new ArrayList<Word>();
		ArrayList<Word> nounW = new ArrayList<Word>();
		ArrayList<Word> verbW = new ArrayList<Word>();
		ArrayList<Word> dalW = new ArrayList<Word>();
		ArrayList<Word> catsW = new ArrayList<Word>();
		AreaWords aW;
		
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
			tw_list = new ArrayList<Tweet_Obj>();
			
			while (results.hasNext()){
				dbo = (BasicDBObject) results.next();
				places = (BasicDBObject) dbo.get("place");
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
				text =  dbo.getString("text");
				tw_list.add(new Tweet_Obj(text));
			}
			
			double crimeRate = 0;
			double housePrice = 0;
			double GCSEScore = 0;
			double transportRating = 0;
			double unemploymentRate = 0;
			double income = 0;
			double incapacity = 0;
			double abscences = 0;
			double childInNoWork = 0;
			double fires = 0;
			
			relevant = false;
			for (String k : placeRatio.keySet()) {
				if (cnm.getCode(k) != null) {
					query = new BasicDBObject("locations", new BasicDBObject("ward0", cnm.getCode(k).substring(0, 4)));
					boroughFactors = new SocialFactors((BasicDBObject) boroughs.findOne(query));
					count = placeRatio.get(k);
					relevant = true;
					crimeRate = crimeRate + (boroughFactors.getCrimeRate()*count);
					housePrice = housePrice + (boroughFactors.getHousePrice()*count);
					GCSEScore = GCSEScore + (boroughFactors.getGCSEScore()*count);
					transportRating = transportRating + (boroughFactors.getTransportRating()*count);
					unemploymentRate = unemploymentRate + (boroughFactors.getUnemploymentRate()*count);
					income = income + (boroughFactors.getIncomeSupport()*count);
					incapacity = incapacity + (boroughFactors.getIncapacityBenefit()*count);
					abscences = abscences + (boroughFactors.getSchoolAbscences()*count);
					childInNoWork = childInNoWork + (boroughFactors.getChildInNoWorkHouse()*count);
					fires = fires + (boroughFactors.getDeliberateFires()*count);
					
					total=total+count;
					System.out.println(k);
					System.out.println(count);
					System.out.println(total);
					System.out.println(boroughFactors.getDBObject());
				}
			}
			
			if (relevant) {
				deviceFactors.setCrimeRate(crimeRate/total);
				deviceFactors.setGCSEScore(GCSEScore/total);
				deviceFactors.setTransportRating(transportRating/total);
				deviceFactors.setUnemploymentRate(unemploymentRate/total);
				deviceFactors.setHousePrice(housePrice/total);
				deviceFactors.setIncomeSupport(income/total);
				deviceFactors.setIncapacityBenefit(incapacity/total);
				deviceFactors.setSchoolAbscences(abscences/total);
				deviceFactors.setChildInNoWorkHouse(childInNoWork/total);
				deviceFactors.setDeliberateFires(fires/total);
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
			
			resultBlock = ta.analyse_tweets(tw_list);

			act = resultBlock.get_mean_activity();
			image = resultBlock.get_mean_imagery();
			valience = resultBlock.get_mean_valience();
			
			map = new BasicDBObject ();
			
			map.put("code", newName);
			map.put("activation", act);
			map.put("imagery", image);
			map.put("pleasantness", valience);
			
			newColl.insert(map);
			
			adj = resultBlock.get_adjective_stats_freqsorted();
			noun = resultBlock.get_noun_stats_freqsorted();
			verb = resultBlock.get_verb_stats_freqsorted();
			dal = resultBlock.get_DAL_stats_freqsorted();
			cats = resultBlock.get_Category_stats_freqsorted();
			System.out.println("Categories: " + cats.size());
			
			adjW = new ArrayList<Word>();
			nounW = new ArrayList<Word>();
			verbW = new ArrayList<Word>();
			dalW = new ArrayList<Word>();
			catsW = new ArrayList<Word>();
			
			aW = new AreaWords(newName);
			int i = 0;
			while (i < 10 && i < adj.size()) {
				adjW.add(new Word(adj.get(i)));
				i++;
			}
			
			i = 0;
			
			while (i < 10 && i < noun.size()) {
				nounW.add(new Word(noun.get(i)));
				i++;
			}
			
			i = 0;
			
			while (i < 10 && i < verb.size()) {
				verbW.add(new Word(verb.get(i)));
				i++;
			}
			
			i = 0;
			
			while (i < 10 && i < dal.size()) {
				dalW.add(new Word(dal.get(i)));
				i++;
			}
			
			i = 0;
			
			while (i < 10 && i < cats.size()) {
				catsW.add(new Word(cats.get(i)));
				i++;
			}
			
			aW.setAdjective(adjW);
			aW.setNouns(nounW);
			aW.setVerb(verbW);
			aW.setDAL(dalW);
			aW.setCatagories(catsW);
			
			wordStore.insert(aW.getDBObject());
			
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

	public void createDeviceList(String tweetName, String devListName) throws UnknownHostException {
		HashSet<String> deviceList = getDevices(tweetName);
		HashSet<String> deviceListFormatted = new HashSet<String>();
	
		for (String s : deviceList) {
			deviceListFormatted.add(formatDeviceName(s));
		}
		
		MongoClient mongoClient = new MongoClient("localhost");
		DB db2 = mongoClient.getDB( "deviceBreakdown" );
		
		DBCollection coll = db2.getCollection(devListName);
		
		BasicDBObject insert = new BasicDBObject().append("list", deviceListFormatted);
		coll.insert(insert);
		System.out.println(insert);
	}
	
	//retrieves all devices used in tweets
	public HashSet<String> getDevices(String tweetName) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection tweets; 

		if (tweetName == null) {
			tweets = db.getCollection(CollectionReader.returnName("tweets"));
		}
		else {
			tweets = db.getCollection(tweetName);
		}
		
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

	public void deviceBreakdown(String tweetName, String devForBoName) throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<DBObject> list;
		HashMap<String, Double> sourceCount = new HashMap<String, Double>();
		HashMap<String, Double> sourceProportion = new HashMap<String, Double>();
		double count = 0;
		String source = "";
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "deviceBreakdown" );
		DBCollection coll;
		
		coll = db.getCollection(devForBoName);
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {
				
				list = getTweetsForBorough(key, tweetName);
				sourceProportion = new HashMap<String, Double>();
				sourceCount = new HashMap<String, Double>();
				
				for (DBObject tweet : list) {
					source = (String)tweet.get("source");
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
				
				for (String source2 : sourceCount.keySet()) {
					count = sourceCount.get(source2) / list.size();
					sourceProportion.put(source2, count);
					System.out.println(source2);
				}
				
				BasicDBObject insertObject = new BasicDBObject();
				insertObject.append("borough", key);
				insertObject.append("sources", sourceProportion);
				coll.insert(insertObject);
			}
		}
	}
	
}












