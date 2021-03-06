package com.SocialCity.TwitterAnalysis;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.DataParsers.CollectionReader;
import com.SocialCity.SocialFactor.AreaWords;
import com.SocialCity.SocialFactor.SocialFactors;
import com.SocialCity.SocialFactor.Word;
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
	
	//top hash tags found and stored
	public static void topHashTags(String tweetsName, String hashTagName, String topTagsName, String tagListName) throws UnknownHostException{
		HashMap<String, Integer> tagMap = new HashMap<String, Integer> ();
		int count;
		BasicDBObject dbo;
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = null;
		DBCollection sortedColl = null;
		DBCollection tagList = null;
		coll = db.getCollection(tweetsName);
		
		BasicDBObject query = new BasicDBObject();
		query.put("text",new BasicDBObject("$regex", String.format(".*((?i)%s).*", "#")));
		DBCursor dbC = coll.find(query);
		
		//use string pattern to filter hash tags out of tweets and store their frequency
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
		
		coll = db.createCollection(hashTagName, null);
		
		//place tag and count into object for database storing
		for (String k : tagMap.keySet()) {
			dbo = new BasicDBObject();
			dbo.put("hashtag", k);
			dbo.put("count", tagMap.get(k));
			coll.insert(dbo);
		}
		
		dbC = coll.find().sort(new BasicDBObject("count", -1));
		//sort collection
		sortedColl = db.createCollection(topTagsName, null);
		tagList = db.createCollection(tagListName, null);
		
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
	
	//create word lists and sentiment score for hash tags
	public static void hashtagWords(String tweetsName, String hashSentiment, String wordName, String time) throws UnknownHostException {
		ArrayList<String> tagList = getTagArrayList(time);
		BasicDBObject query;
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = db.getCollection(tweetsName);
		DBCollection newColl = db.createCollection(hashSentiment, null);
		BasicDBObject dbo;
		String text;
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt", "resources/wordnet-core-words.txt", "resources/Categories.txt");
		Tweet_Info_Bloc resultBlock;
		ArrayList<Tweet_Obj> tw_list;
		ArrayList<Word_Stats> words;

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
		DBCollection wordStore = db.getCollection(wordName);
		int counter = 0;
		
		//go through each hash tag in database
		for (String s: tagList) {
			counter++;
			query = new BasicDBObject();
			//find all tweets containing that hash tag
			query.put("text",new BasicDBObject("$regex", String.format(".*((?i)%s).*", "#"+s)));
			DBCursor dbC = coll.find(query);
			
			tw_list = new ArrayList<Tweet_Obj>();
			
			//create text list for tweet analysis
			while (dbC.hasNext()) {
				dbo = (BasicDBObject) dbC.next();
				text =  dbo.getString("text");
				tw_list.add(new Tweet_Obj(text));
			}
			
			resultBlock = ta.analyse_tweets(tw_list);

			act = resultBlock.get_mean_activity();
			image = resultBlock.get_mean_imagery();
			valience = resultBlock.get_mean_valience();
			double frequency = tw_list.size();
			//store average scores for a hash tag
			map = new BasicDBObject ();
			map.put("code", s);
			map.put("activation", act);
			map.put("imagery", image);
			map.put("pleasantness", valience);
			map.put("frequency", frequency);
			newColl.insert(map);
			
			adj = resultBlock.get_adjective_stats_freqsorted();
			noun = resultBlock.get_noun_stats_freqsorted();
			verb = resultBlock.get_verb_stats_freqsorted();
			dal = resultBlock.get_DAL_stats_freqsorted();
			cats = resultBlock.get_Category_stats_freqsorted();
		//	create lists of each word category for the hash tag and store it in the database
			aW = new AreaWords(s);
			adjW = new ArrayList<Word>();
			nounW = new ArrayList<Word>();
			verbW = new ArrayList<Word>();
			dalW = new ArrayList<Word>();
			catsW = new ArrayList<Word>();
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
		
		mongoClient.close();
	}
	
	//for an area create the list of all hashtags for that area
	public static void areaHashtag(String dbName, String tweetsName) throws UnknownHostException {
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = db.createCollection(dbName, null);
		DBCollection tweets = db.getCollection(tweetsName);
		CodeNameMap cnm = new CodeNameMap();
		Set<String> codes = cnm.getBoroughCodes();
		BasicDBObject query = new BasicDBObject();
		HashSet<String> tags;
		String locale;

		BasicDBObject dbo;
		
		for (String s : codes) {
			locale = cnm.getName(s);
			query.put("place.name", locale);
			DBCursor dbC = tweets.find(query);
			
			tags = new HashSet<String>();
			while (dbC.hasNext()){
				String text = (String) (dbC.next().get("text"));
				Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
				Matcher mat = MY_PATTERN.matcher(text);
				
				while (mat.find()) {
					tags.add(mat.group(1).toLowerCase());
				}
			}
			dbo = new BasicDBObject();
			dbo.put("code", s);
			dbo.put("hashtags", tags);
			coll.insert(dbo);
		}
		mongoClient.close();
	}
	//return the list of top 100 hash tags for user request
	public static ArrayList<String> getTagArrayList(String time) throws UnknownHostException {
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll;

		if (time == null) {
			coll = db.getCollection(CollectionReader.returnName("tagList"));
		}
		else {
			coll = db.getCollection("tagList_" + time);
		}
		
		ArrayList<String> ret = (ArrayList<String>)((BasicDBObject) coll.findOne()).get("tags");
		mongoClient.close();
		return ret;
	}
	//calls list creator function to return
	public static String getTagList(String time) throws UnknownHostException {
		ArrayList<String> names = getTagArrayList(time);
		
		Gson gson = new Gson();
		return gson.toJson(names);
	}
	
	//create weighted average of tag social factors
	public static void tagLocationInfo(String tweetsName, String tagInfoName, String topTagName, String tagListName) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = db.getCollection(topTagName);
		DBCollection tweets = db.getCollection(tweetsName);
		DBCollection list = db.getCollection(tagListName);
		DBCollection hashTagInfo = db.createCollection(tagInfoName, null);
		ArrayList<String> names = new ArrayList<String>();
		int tagCount = 0;
		boolean relevant;
		DBCursor dbC = coll.find();
		DBCursor results;
		DB areas = mongoClient.getDB("areas");
		DBCollection boroughs = areas.getCollection("boroughs2012");
		HashMap<String, Integer> placeRatio;
		String placeName;
		CodeNameMap cnm = new CodeNameMap();
		SocialFactors boroughFactors;
		SocialFactors tagFactors;
		
		//get count of locations for each tag and create social factors
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
			//weight social factors based on count for each location
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
				}
			}
			//average social factor information
			if (relevant) {
				tagFactors.setCrimeRate(crimeRate/total);
				tagFactors.setGCSEScore(GCSEScore/total);
				tagFactors.setTransportRating(transportRating/total);
				tagFactors.setUnemploymentRate(unemploymentRate/total);
				tagFactors.setHousePrice(housePrice/total);
				tagFactors.setIncomeSupport(income/total);
				tagFactors.setIncapacityBenefit(incapacity/total);
				tagFactors.setSchoolAbscences(abscences/total);
				tagFactors.setChildInNoWorkHouse(childInNoWork/total);
				tagFactors.setDeliberateFires(fires/total);
				
				hashTagInfo.insert(tagFactors.getDBObject());
				tagCount++;
				names.add(hashTag.getString("hashtag"));
				relevant = false;
			}
		}
		BasicDBObject tag = new BasicDBObject();
		tag.put("tags", names);
		list.drop();
		list = db.getCollection(tagListName);
		list.insert(tag);
		mongoClient.close();
	}
	//for each area create list of devices without proportions
	public static void areaDevices(String areaDevices, String tweetName) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		DBCollection coll = db.createCollection(areaDevices, null);
		DBCollection tweets = db.getCollection(tweetName);
		BasicDBObject dbo;
		BasicDBObject query = new BasicDBObject();
		HashSet<String> devices;
		String locale;
		String source;
		
		CodeNameMap cnm = new CodeNameMap();
		Set<String> codes = cnm.getBoroughCodes();
		
		for (String s : codes) {
			locale = cnm.getName(s);
			query.put("place.name", locale);
			DBCursor dbC = tweets.find(query);
			devices = new HashSet<String>();
			while (dbC.hasNext()) {
				dbo = (BasicDBObject) dbC.next();
				source = (String)dbo.get("source");
				if (!source.equals("web")) {
					source = TweetByArea.formatDeviceName(source);
				}
				devices.add(source);
			}
		
			dbo = new BasicDBObject();
			dbo.put("code", s);
			dbo.put("devices", devices);
			coll.insert(dbo);
		}
		mongoClient.close();
	}
	

}
