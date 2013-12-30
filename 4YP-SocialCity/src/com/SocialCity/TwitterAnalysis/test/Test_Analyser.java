package com.SocialCity.TwitterAnalysis.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.SocialCity.TwitterAnalysis.DAL_Classification;
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

	static final String mongoHost = "localhost";
	static final String mongo_DB = "4YP";
	static final String mongoCollection = "tweets";
	
	public static void main(String[] args) {
		ArrayList<String> tweets = getTweets();
		ArrayList<TweetScore> scores = new ArrayList<TweetScore>(); 
		
		//hashmap storing words that were matched and how many times they were matched
		HashMap<String, Integer> matched_words = new HashMap<String, Integer>();
		HashMap<String, Integer> hashtag_map = new HashMap<String, Integer>();

		
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt", "resources/wordnet-core-words.txt");
			
		
		
		String text = null;
		Iterator<String> i = tweets.iterator();
		while (i.hasNext()){
			//pass and analyse the tweet
			scores.add(ta.analyse_tweet(i.next()));
		}
		
		//analytics setup
		double total_matched_ratio = 0;
		double total_valience = 0;
		double total_activity = 0;
		double total_imagery = 0;
		double count = 0;
		TweetScore tweet_sc = null;
		Iterator<TweetScore> it_ts = scores.iterator();
		
		// look at each tweet score
		while (it_ts.hasNext()){
			tweet_sc = it_ts.next();
			
			// total the matched ratios from all tweets
			total_matched_ratio = total_matched_ratio + tweet_sc.get_matched_ratio();
			
			// total the val, act, img values for valid tweets (matched words with the DAL set)
			if (tweet_sc.get_Dal_classification() == DAL_Classification.VALID){
				total_valience = total_valience + tweet_sc.get_valience();
				total_activity = total_activity + tweet_sc.get_active();
				total_imagery = total_imagery + tweet_sc.get_image();
				count++;
			}
			
		
				
			//get an iterator for the words that were matched from the tweet
			Iterator words_it = tweet_sc.get_words().iterator();
			
			//build hashmap of words that were popular
			while (words_it.hasNext()){
				String word = (String) words_it.next();
				if (matched_words.containsKey(word))
				{
					int score = matched_words.get(word);
					score++;
					matched_words.put(word, score);
				}
				else{
					matched_words.put((String) word, 1);
				}
			}
				
				//get an iterator for the hashtags that were matched from the tweet
				Iterator hashtags_it = tweet_sc.get_hashtags().iterator();
				
				//build hashmap of hashtags that were popular
				while (hashtags_it.hasNext()){
					String tag = (String) hashtags_it.next();
					if (hashtag_map.containsKey(tag))
					{
						int score = hashtag_map.get(tag);
						score++;
						hashtag_map.put(tag, score);
					}
					else{
						hashtag_map.put((String) tag, 1);
					}
			}
			
			
		/*	System.out.println("****************");
			System.out.println(tweet_sc.get_tweet());
			System.out.println("valience: " + tweet_sc.get_valience());
			System.out.println("activity: " + tweet_sc.get_active());
			System.out.println("imagery: " + tweet_sc.get_image());
			System.out.println("matched ratio: " + tweet_sc.get_matched_ratio());
			System.out.println("****************"); */
		}
		
		//analytics over the sentiment analyser
		
		double avg_matched_ratio = total_matched_ratio / tweets.size();
		double avg_valience = total_valience / count;
		double avg_activity = total_activity / count;
		double avg_imagery = total_imagery / count;
		
		
		
		
		//print out analytic
		
		//print out matched words
				Iterator matched_words_it = matched_words.keySet().iterator();
				while (matched_words_it.hasNext()){
					String word = (String) matched_words_it.next();
	//			System.out.println(word + " " + matched_words.get(word));
				}
				
		//print out hashtags
				Iterator tags_it = hashtag_map.keySet().iterator();
				while (tags_it.hasNext()){
					String hashtag = (String) tags_it.next();
	//			System.out.println(hashtag + " " + hashtag_map.get(hashtag));
				}
		
		System.out.println("************ averages ***********");
		System.out.println("average valience: " + avg_valience);
		System.out.println("average activity: " + avg_activity);
		System.out.println("average imagery: " + avg_imagery);
		System.out.println("average matched ratio: " + avg_matched_ratio);
		System.out.println("Total Number of Tweets: " + tweets.size());
		System.out.println("DAL valid tweets: " + count);
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
