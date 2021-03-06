package com.SocialCity.TwitterAnalysis.test;

import com.SocialCity.TwitterAnalysis.Twokenizer;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test_tokenizer {
	
	static final String mongoHost = "localhost";
	static final String mongo_DB = "4YP";
	static final String mongoCollection = "tweets";

	public static void main(String[] args) {
	
		//testing twokenizer
		
				//find tweets
		ArrayList<String> tweets= getTweets();
		
		ArrayList<String> squeezed_tweets= new ArrayList<String>();
		ArrayList<List<String>> tokens= new ArrayList<List<String>>();
						
		Twokenizer twokenizer = new Twokenizer();		
		//tokenise tweets
		for (int i = 0; i < tweets.size(); i++){
			//always squeeze whitespace first
			squeezed_tweets.add(twokenizer.squeezeWhitespace(tweets.get(i)));
			//tokenize
			tokens.add(twokenizer.simpleTokenize(squeezed_tweets.get(i)));
		}
		
		//print out tweets
		for (int i =0; i<tokens.size(); i++){
			System.out.println("********* NEW TWEET *************");
			System.out.println(tweets.get(i));
			Iterator it = tokens.get(i).iterator();
			while (it.hasNext()){
			System.out.println(it.next());
			}
		}
		
		
				
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
