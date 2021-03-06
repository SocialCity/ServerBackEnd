package com.SocialCity.SocialFactor;

import java.util.ArrayList;
import com.mongodb.BasicDBObject;

public class AreaWords{

	private String code;
	private ArrayList<Word> nouns;
	private ArrayList<Word> adjective;
	private ArrayList<Word> verb;
	private ArrayList<Word> DAL;
	private ArrayList<Word> Cats;
	
	public AreaWords (String code) {
		setCode(code);
		nouns = new ArrayList<Word>();
		adjective = new ArrayList<Word>();
		verb = new ArrayList<Word>();
		DAL = new ArrayList<Word>();
		Cats = new ArrayList<Word>();
	}
	
	//uses a DBObject to create an AreaWords object
	public AreaWords (BasicDBObject dbo) {
		BasicDBObject dbo2;
		BasicDBObject dbo3;
		
		nouns = new ArrayList<Word>();
		adjective = new ArrayList<Word>();
		verb = new ArrayList<Word>();
		DAL = new ArrayList<Word>();
		Cats = new ArrayList<Word>();
		Word word;
		setCode(dbo.getString("code"));
		
		int i = 0;
		dbo2 = (BasicDBObject) dbo.get("nouns");
		//words stored in nested dbo objects, so values need to be extracted
		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			nouns.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("adjectives");

		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			adjective.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("verb");

		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			verb.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("DAL");

		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			DAL.add(word);
			i++;
		}
		
		i = 0;
		dbo2 = (BasicDBObject) dbo.get("Catagories");

		while (!(dbo2.get("word"+i) == null)){
			dbo3 = (BasicDBObject) dbo2.get("word" + i);
			word = new Word(dbo3.getString("word"), dbo3.getDouble("frequency"), dbo3.getDouble("pleasantness"), dbo3.getDouble("activation"), dbo3.getDouble("imagery"));
			Cats.add(word);
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
	public ArrayList<Word> getCatagories() {
		return Cats;
	}
	public void setCatagories(ArrayList<Word> dAL) {
		Cats = dAL;
	}
	
	//creates the DB object that is represents this object
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
		
		sub = new BasicDBObject();
		i = 0;
		
		for (Word w : Cats) {
			subSub = new BasicDBObject();
			subSub.append("word", w.getWord());
			subSub.append("frequency", w.getFrequency());
			subSub.append("pleasantness", w.getPleasantness());
			subSub.append("activation", w.getActivation());
			subSub.append("imagery", w.getImagery());
	
			sub.append("word"+i, subSub);
			i++;
		}
		dbo.append("Catagories", sub);
		
		return dbo;
	}

}
