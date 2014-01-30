package com.SocialCity.TwitterAnalysis.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.SocialCity.TwitterAnalysis.DAL_Classification;
import com.SocialCity.TwitterAnalysis.TweetScore;
import com.SocialCity.TwitterAnalysis.Tweet_Info_Bloc;
import com.SocialCity.TwitterAnalysis.Tweet_Obj;
import com.SocialCity.TwitterAnalysis.TwitterAnalyser;
import com.SocialCity.TwitterAnalysis.Twokenizer;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Test_Analyser {

	static final String mongoHost = "localhost";
	static final String mongo_DB = "4YP";
	static final String mongoCollection = "tweets";
	
	public static void main(String[] args) {
		
		//bloc for results
		Tweet_Info_Bloc result_bloc;
		
		//get and prepare tweets
//		ArrayList<String> tweets = getTweets();
//		ArrayList<Tweet_Obj> tw_list = new ArrayList<Tweet_Obj>();
//		Iterator<String> i = tweets.iterator();
//		while(i.hasNext()){
//			tw_list.add(new Tweet_Obj(i.next()));
//		}
		
		//build analyser
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt", "resources/wordnet-core-words.txt");
		
		//analyse tweets
//		result_bloc = ta.analyse_tweets(tw_list);
		
		// ***** print out variables from result_bloc ******* //
	
	}

	public static ArrayList getTweets(){
		BasicDBObject query;
		ArrayList<String> tweets= new ArrayList<String>();
		String name = "Westminster";

		try{
			MongoClient mongoClient;
			mongoClient = new MongoClient(mongoHost);
			DB db = mongoClient.getDB(mongo_DB);
			DBCollection coll = null;
			coll = db.getCollection(mongoCollection);		
			query = new BasicDBObject("place.name", name);
			
			System.out.println("fetching tweets");
			
			// use the query for a certain place
			DBCursor t = coll.find(query);
			
			//get all tweets
			//DBCursor t =coll.find();
			for (DBObject dbo : t) {;
				tweets.add((String) dbo.get("text"));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("returning tweets");
		return tweets;
		
		
		
	}
}
