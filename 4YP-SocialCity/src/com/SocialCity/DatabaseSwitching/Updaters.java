package com.SocialCity.DatabaseSwitching;

import java.io.IOException;
import java.net.UnknownHostException;

import com.SocialCity.DataParsers.CollectionReader;
import com.SocialCity.TwitterAnalysis.HashTag;
import com.SocialCity.TwitterAnalysis.TweetByArea;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Updaters {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	//VERY IMPORTANT - STOP THE TWEET COLLECTOR BEFORE YOU USE THIS.
	public static void update(String tweetName) throws UnknownHostException {
		
		String date= new java.util.Date().toString();
		date = date.replace(" ", "_");
		TweetByArea tba = new TweetByArea();
		
		String hashTagName = "hashTag_" + date;
		String topTagsName = "topHashTags_" + date;
		String tagListName = "tagList_" + date;
		String tagInfoName = "tagInfo_" + date;
		String propName = "tweetProportions_" + date;
		String devFacName = "deviceFactors_" + date;
		String devListName = "deviceList_" + date;
		String devForBoName = "devicesForBoroughs_" + date;
		
		HashTag.topHashTags(tweetName,hashTagName,topTagsName,tagListName); 
		HashTag.tagLocationInfo(tweetName, tagInfoName, topTagsName);
		tba.tweetProportions(propName, tweetName);
		tba.deviceFactors(tweetName, devFacName);
		tba.createDeviceList(tweetName, devListName);
		tba.deviceBreakdown(tweetName, devForBoName);
		
		try {
			CollectionReader.editName("hashtag", hashTagName);
			CollectionReader.editName("topHashTags", topTagsName);
			CollectionReader.editName("tagList", tagListName);
			CollectionReader.editName("tagInfo", tagInfoName);
			CollectionReader.editName("tweetProportions", propName);
			CollectionReader.editName("deviceFactors", devFacName);
			CollectionReader.editName("deviceList", devListName);
			CollectionReader.editName("devicesForBoroughs", devForBoName);
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

}
