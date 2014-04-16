package com.SocialCity.DatabaseSwitching;

import java.io.IOException;
import java.net.UnknownHostException;

import com.SocialCity.DataParsers.CollectionReader;
import com.SocialCity.SocialFactor.AreaWordsMaker;
import com.SocialCity.TwitterAnalysis.HashTag;
import com.SocialCity.TwitterAnalysis.TweetByArea;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Updaters {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	//VERY IMPORTANT - STOP THE TWEET COLLECTOR BEFORE YOU USE THIS.
	public static void update(String tweetName) throws UnknownHostException {
		//get date for timestamp
		String date= new java.util.Date().toString();
		
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "updates" );
		DBCollection coll = db.getCollection("times");

		DBObject dbo = new BasicDBObject();

		date = date.replace(" ", "_");
		date = date.replace(":", "_");
	
		dbo.put("date", date.toString());
		coll.insert(dbo);

		
		TweetByArea tba = new TweetByArea();
		
		//creates names for each database
		String hashTagName = "hashTag_" + date.toString();
		String topTagsName = "topHashTags_" + date.toString();
		String tagListName = "tagList_" + date.toString();
		String tagInfoName = "tagInfo_" + date.toString();
		String propName = "tweetProportions_" + date.toString();
		String devFacName = "deviceFactors_" + date.toString();
		String devListName = "deviceList_" + date.toString();
		String devForBoName = "devicesForBoroughs_" + date.toString();
		String wordsName = "wordsInfo_" + date.toString();
		String devSent = "deviceSentiment_" + date.toString();
		String devWords = "deviceWords_" + date.toString();
		String areaSentiment = "areaSentiment_" + date.toString();
		String hashSentiment = "hashtagSentiment_" + date.toString();
		String hashWordName = "hashtagWords_" + date.toString();
		String areaTags = "areaHashtags_" + date.toString();
		String areaDevices = "areaDevices_" + date.toString();
		
		//update each database
		System.out.println("Starting update: 0/11");
		HashTag.topHashTags(tweetName,hashTagName,topTagsName,tagListName); 
		System.out.println("Update: 1/11");
		HashTag.tagLocationInfo(tweetName, tagInfoName, topTagsName, tagListName);
		System.out.println("Update: 2/11");
		tba.tweetProportions(propName, tweetName);
		System.out.println("Update: 3/11");
		tba.deviceFactors(tweetName, devFacName, devSent, devWords );
		System.out.println("Update: 4/11");
		tba.createDeviceList(tweetName, devListName);
		System.out.println("Update: 5/11");
		tba.deviceBreakdown(tweetName, devForBoName);
		System.out.println("Update: 6/11");
		AreaWordsMaker.createDatabase(tweetName, wordsName);
		System.out.println("Update: 7/11");
		AreaWordsMaker.areaSentiment(tweetName, areaSentiment);
		System.out.println("Update: 8/11");
		HashTag.hashtagWords(tweetName, hashSentiment, hashWordName, date.toString());
		System.out.println("Update: 9/11");
		HashTag.areaHashtag(areaTags, tweetName);
		System.out.println("Update: 10/11");
		HashTag.areaDevices(areaDevices, tweetName);
		System.out.println("Update: 11/11");
		
		try {
			//collection reader files get updated
			CollectionReader.editName("hashtag", hashTagName);
			CollectionReader.editName("topHashTags", topTagsName);
			CollectionReader.editName("tagList", tagListName);
			CollectionReader.editName("tagInfo", tagInfoName);
			CollectionReader.editName("tweetProportions", propName);
			CollectionReader.editName("deviceFactors", devFacName);
			CollectionReader.editName("deviceList", devListName);
			CollectionReader.editName("devicesForBoroughs", devForBoName);
			CollectionReader.editName("wordsInfo", wordsName);
			CollectionReader.editName("deviceSentiment", devSent);
			CollectionReader.editName("deviceWords", devWords);
			CollectionReader.editName("areaSentiment", areaSentiment);
			CollectionReader.editName("hashtagSentiment", hashSentiment);
			CollectionReader.editName("hashtagWords", hashWordName);
			CollectionReader.editName("areaHashtags", areaTags);
			CollectionReader.editName("areaDevices", areaDevices);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//delete tweets
		System.out.println("Files updated. Deleting tweets...");
		db = mongoClient.getDB( "tweetInfo" );
		coll = db.getCollection(tweetName);
		coll.drop();
		mongoClient.close();
		System.out.println("Update complete.");

		
	}
	public static void main(String[] args) throws Exception
	{
		update(args[0]);
	}

}
