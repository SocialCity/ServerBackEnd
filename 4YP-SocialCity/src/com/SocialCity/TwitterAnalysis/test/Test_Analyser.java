package com.SocialCity.TwitterAnalysis.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt");
	}

}
