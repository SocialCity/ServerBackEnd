package com.SocialCity.TwitterAnalysis.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.SocialCity.TwitterAnalysis.TweetScore;
import com.SocialCity.TwitterAnalysis.TwitterAnalyser;
import com.SocialCity.TwitterAnalysis.Twokenizer;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Test_Analyser {

	public static void main(String[] args) {
		ArrayList<String> tweets = getTweets();
		ArrayList<TweetScore> scores = new ArrayList<TweetScore>(); 
		
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt");
			
		
		
		String text = null;
		Iterator<String> i = tweets.iterator();
		while (i.hasNext()){
			//pass the tweet
			scores.add(ta.analyse_tweet(i.next()));
		}
		
		//print out scores
		TweetScore tweet_sc = null;
		Iterator<TweetScore> it_ts = scores.iterator();
		while (it_ts.hasNext()){
			System.out.println("****************");
			tweet_sc = it_ts.next();
			System.out.println(tweet_sc.get_tweet());
			System.out.println("valience: " + tweet_sc.get_valience());
			System.out.println("activity: " + tweet_sc.get_active());
			System.out.println("imagery: " + tweet_sc.get_image());
			System.out.println("matched ratio: " + tweet_sc.get_matched_ratio());
			System.out.println("****************");
		}
		
		
	}

	public static ArrayList getTweets(){
		BasicDBObject query;
		ArrayList<String> tweets= new ArrayList<String>();
		String name = "Westminster";

		try{
			MongoClient mongoClient;
			mongoClient = new MongoClient("localhost");
			DB db = mongoClient.getDB( "tweetInfo" );
			DBCollection coll = null;
			coll = db.getCollection("tweets");		
			query = new BasicDBObject("place.name", name);
			
			System.out.println("fetching tweets");
			
			DBCursor t = coll.find(query);
			for (DBObject dbo : t) {
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
