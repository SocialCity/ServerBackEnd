package com.SocialCity.TwitterAnalysis;

import com.SocialCity.TwitterAnalysis.Twokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TweetScore {
	
	private String tweet = null;
	private Double val = null;
	private Double active = null;
	private Double image = null;
	private Double matched_ratio = null;
	private ArrayList<String> matched_words = new ArrayList();
	private ArrayList<String> hashtags = new ArrayList(); 

	
	private T_Classification t_classification = null;
	
	private DAL_Classification dal_classification = null;
	
	private String location = null;
	
	private Set nouns = null;

	public TweetScore(String tweet, Double val, Double active, Double image, Double matched_ratio, DAL_Classification dal_classification){
		this.tweet = tweet;
		this.val = val;
		this.active = active;
		this.image = image;
		this.matched_ratio = matched_ratio;
		this.dal_classification = dal_classification;
		
	}
	
	public void add_hashtag(String hashtag){
		hashtags.add(hashtag);
	}
	
	public void add_hashtags(ArrayList<String> hashtags){
		this.hashtags.addAll(hashtags);
	}
	
	public ArrayList get_hashtags(){
		return hashtags;
	}
	
	public void add_matched_word(String word){
		matched_words.add(word);
	}
	
	public void add_matched_words(ArrayList<String> words){
		matched_words.addAll(words);
	}
	
	public ArrayList get_words(){
		return matched_words;
	}
	

	public String get_tweet(){
		return tweet;
	}
	
	
	public Double get_valience(){
		return val;
	}
	
	public Double get_active(){
		return active;
	}
	
	public Double get_image(){
		return image;
	}
	
	public Double get_matched_ratio(){
		return matched_ratio;
	}

	public DAL_Classification get_Dal_classification() {
		return dal_classification;
	}

	
}
