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
		
		String date= new java.util.Date().toString();
		
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "updates" );
		DBCollection coll = db.getCollection("times");
		DBObject dbo = new BasicDBObject();
		
		date = date.replace(" ", "_");
		
		dbo.put("date", date.toString());
		coll.insert(dbo);
		
		TweetByArea tba = new TweetByArea();
		
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
		
		HashTag.topHashTags(tweetName,hashTagName,topTagsName,tagListName); 
		HashTag.tagLocationInfo(tweetName, tagInfoName, topTagsName, tagListName);
		tba.tweetProportions(propName, tweetName);
		tba.deviceFactors(tweetName, devFacName, devSent, devWords );
		tba.createDeviceList(tweetName, devListName);
		tba.deviceBreakdown(tweetName, devForBoName);
		AreaWordsMaker.createDatabase(tweetName, wordsName);
		AreaWordsMaker.areaSentiment(tweetName, areaSentiment);
		HashTag.hashtagWords(tweetName, hashSentiment, hashWordName, date.toString());
		System.out.println("out?");
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//tophashtags first
		//tagLocationInfo second
		//tweetProportions
		//devicefactors
		//createDeviceList
		//deviceBreakdown
		
	}
	public static void main(String[] args) throws Exception
	{
		update("tweets");
	}

}
