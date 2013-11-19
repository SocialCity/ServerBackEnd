package com.SocialCity.TwitterAnalysis;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import com.SocialCity.Area.CodeNameMap;
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
	
	public ArrayList<String> getTweetsForBorough(String boroughCode) throws UnknownHostException {
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
		for (DBObject dbo : tweets) {
			tweetText.add((String) dbo.get("text"));
			//System.out.println(dbo.get("text"));
		}

		
		return tweetText;
	}
	
	public HashMap<String, Double> tweetProportion() throws UnknownHostException {
		HashMap<String, String> nameMap = codeNameMap.getNameMap();
		ArrayList<String> list;
		HashMap<String, Double> tweetCount = new HashMap<String, Double>();
		int total = 0;
		
		for (String key : nameMap.keySet()) {
			if (key.length() == 4) {
				list = getTweetsForBorough(key);
				total = total + list.size();
				tweetCount.put(key, (double) list.size());
			}
		}
		
		for (String key : tweetCount.keySet()) {
			tweetCount.put(key, (tweetCount.get(key)/total));
		}
		
		return tweetCount;
	}

}












