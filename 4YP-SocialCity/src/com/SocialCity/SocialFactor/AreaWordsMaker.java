package com.SocialCity.SocialFactor;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.TwitterAnalysis.Tweet_Info_Bloc;
import com.SocialCity.TwitterAnalysis.Tweet_Obj;
import com.SocialCity.TwitterAnalysis.TwitterAnalyser;
import com.SocialCity.TwitterAnalysis.Word_Stats;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class AreaWordsMaker {

	public static void createDatabase(String tweetName, String dbName) throws UnknownHostException {
		String name = "";
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB( "tweetInfo" );
		BasicDBObject query;
		String text = "";
		DBCollection tweets = db.getCollection(tweetName);
		DBCollection wordStore = db.getCollection(dbName);
		wordStore.drop();
		wordStore = db.createCollection(dbName, null);
		TwitterAnalyser ta = new TwitterAnalyser("resources/DAL.txt", "resources/wordnet-core-words.txt");
		CodeNameMap cnm = new CodeNameMap();
		Set<String> codes = cnm.getBoroughCodes();
		ArrayList<Tweet_Obj> tw_list;
		BasicDBObject dbo;
		Tweet_Info_Bloc resultBlock;

		AreaWords aW;
		ArrayList<Word_Stats> adj;
		ArrayList<Word_Stats> noun;
		ArrayList<Word_Stats> verb;
		ArrayList<Word_Stats> dal;
		
		ArrayList<Word> adjW = new ArrayList<Word>();
		ArrayList<Word> nounW = new ArrayList<Word>();
		ArrayList<Word> verbW = new ArrayList<Word>();
		ArrayList<Word> dalW = new ArrayList<Word>();
		
		int i = 0;
		
		for (String s : codes) {
			System.out.println(s);
			name = cnm.getName(s);
			
			query = new BasicDBObject("place.name", name);
			DBCursor t = tweets.find(query);
			tw_list = new ArrayList<Tweet_Obj>();
			
			while (t.hasNext()) {
				dbo = (BasicDBObject) t.next();
				text =  dbo.getString("text");
				tw_list.add(new Tweet_Obj(text));
			}
			
			System.out.println(tw_list.size());
			resultBlock = ta.analyse_tweets(tw_list);
			
			adj = resultBlock.get_adjective_stats_freqsorted();
			noun = resultBlock.get_noun_stats_freqsorted();
			verb = resultBlock.get_verb_stats_freqsorted();
			dal = resultBlock.get_DAL_stats_freqsorted();
			
			aW = new AreaWords(s);
			
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
			
			aW.setAdjective(adjW);
			aW.setNouns(nounW);
			aW.setVerb(verbW);
			aW.setDAL(dalW);
			
			wordStore.insert(aW.getDBObject());
		}
		
	}
}
