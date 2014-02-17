package com.SocialCity.SocialFactor;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.SocialCity.Area.CodeNameMap;
import com.SocialCity.TwitterAnalysis.Tweet_Info_Bloc;
import com.SocialCity.TwitterAnalysis.Tweet_Obj;
import com.SocialCity.TwitterAnalysis.TwitterAnalyser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class AreaWords{

	private String code;
	private ArrayList<Word> nouns;
	private ArrayList<Word> adjective;
	private ArrayList<Word> verb;
	private ArrayList<Word> DAL;
	
	public AreaWords (String code) {
		setCode(code);
		nouns = new ArrayList<Word>();
		adjective = new ArrayList<Word>();
		verb = new ArrayList<Word>();
		DAL = new ArrayList<Word>();
	}
	
	public AreaWords (BasicDBObject dbo) {
		BasicDBObject dbo2;
		BasicDBObject dbo3;
		
		nouns = new ArrayList<Word>();
		adjective = new ArrayList<Word>();
		verb = new ArrayList<Word>();
		DAL = new ArrayList<Word>();
		Word word;
		setCode(dbo.getString("code"));
		
		int i = 0;
		dbo2 = (BasicDBObject) dbo.get("nouns");
		//System.out.println("nouns");
		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			nouns.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("adjectives");
		//System.out.println("adjective");
		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			adjective.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("verb");
		//System.out.println("verb");
		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			verb.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("DAL");
		//System.out.println("DAL");
		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			DAL.add(word);
			i++;
		}
		
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList<Word> getNouns() {
		return nouns;
	}
	public void setNouns(ArrayList<Word> nouns) {
		this.nouns = nouns;
	}
	public ArrayList<Word> getAdjective() {
		return adjective;
	}
	public void setAdjective(ArrayList<Word> adjective) {
		this.adjective = adjective;
	}
	public ArrayList<Word> getVerb() {
		return verb;
	}
	public void setVerb(ArrayList<Word> verb) {
		this.verb = verb;
	}
	public ArrayList<Word> getDAL() {
		return DAL;
	}
	public void setDAL(ArrayList<Word> dAL) {
		DAL = dAL;
	}
	
	public BasicDBObject getDBObject() {
		BasicDBObject dbo = new BasicDBObject();
		BasicDBObject sub;
		BasicDBObject subSub;
		
		dbo.append("code", code);
		
		sub = new BasicDBObject();
		int i = 0;
		for (Word w : adjective) {
			subSub = new BasicDBObject();
			subSub.append("word", w.getWord());
			subSub.append("frequency", w.getFrequency());
			subSub.append("pleasantness", w.getPleasantness());
			subSub.append("activation", w.getActivation());
			subSub.append("imagery", w.getImagery());
	
			sub.append("word"+i, subSub);
			i++;
		}
		dbo.append("adjectives", sub);
		
		sub = new BasicDBObject();
		i = 0;
		
		for (Word w : nouns) {
			subSub = new BasicDBObject();
			subSub.append("word", w.getWord());
			subSub.append("frequency", w.getFrequency());
			subSub.append("pleasantness", w.getPleasantness());
			subSub.append("activation", w.getActivation());
			subSub.append("imagery", w.getImagery());
	
			sub.append("word"+i, subSub);
			i++;
		}
		dbo.append("nouns", sub);
		
		sub = new BasicDBObject();
		i = 0;
		
		for (Word w : verb) {
			subSub = new BasicDBObject();
			subSub.append("word", w.getWord());
			subSub.append("frequency", w.getFrequency());
			subSub.append("pleasantness", w.getPleasantness());
			subSub.append("activation", w.getActivation());
			subSub.append("imagery", w.getImagery());
	
			sub.append("word"+i, subSub);
			i++;
		}
		dbo.append("verb", sub);
		
		sub = new BasicDBObject();
		i = 0;
		
		for (Word w : DAL) {
			subSub = new BasicDBObject();
			subSub.append("word", w.getWord());
			subSub.append("frequency", w.getFrequency());
			subSub.append("pleasantness", w.getPleasantness());
			subSub.append("activation", w.getActivation());
			subSub.append("imagery", w.getImagery());
	
			sub.append("word"+i, subSub);
			i++;
		}
		dbo.append("DAL", sub);
		
		return dbo;
	}

}
