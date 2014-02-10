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
	private ArrayList<String> matched_DAL_words = new ArrayList<String>();
	private ArrayList<String> matched_nouns = new ArrayList<String>();
	private ArrayList<String> matched_verbs = new ArrayList<String>();
	private ArrayList<String> matched_adjectives = new ArrayList<String>();
	private ArrayList<String> hashtags = new ArrayList<String>();
	private ArrayList<String> AT_tags = new ArrayList<String>();

	
	private T_Classification t_classification = null;
	
	private DAL_Classification dal_classification = null;
	
	private boolean retweet_flag;
	
	private String location = null;
	
	private Set nouns = null;

	public TweetScore(String tweet, Double val, Double active, Double image, Double matched_ratio, DAL_Classification dal_classification, Boolean retweet){
		this.tweet = tweet;
		this.val = val;
		this.active = active;
		this.image = image;
		this.matched_ratio = matched_ratio;
		this.dal_classification = dal_classification;
		retweet_flag = retweet;
		
	}
	
	public void add_at_tag(String at_tag){
		AT_tags.add(at_tag);
	}
	
	public void add_at_tags(ArrayList<String> at_tags){
		this.AT_tags.addAll(at_tags);
	}
	
	public ArrayList<String> get_at_tags(){
		return AT_tags;
	}
	
	
	public boolean get_retweet_flag(){
		return retweet_flag;
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

	public ArrayList<String> get_Matched_nouns() {
		return matched_nouns;
	}

	public void set_Matched_nouns(ArrayList<String> matched_nouns) {
		this.matched_nouns = matched_nouns;
	}

	public ArrayList<String> get_Matched_verbs() {
		return matched_verbs;
	}

	public void set_Matched_verbs(ArrayList<String> matched_verbs) {
		this.matched_verbs = matched_verbs;
	}

	public ArrayList<String> get_Matched_adjectives() {
		return matched_adjectives;
	}

	public void set_Matched_adjectives(ArrayList<String> matched_adjectives) {
		this.matched_adjectives = matched_adjectives;
	}

	public ArrayList<String> get_matched_DAL_words() {
		return matched_DAL_words;
	}

	public void set_matched_DAL_words(ArrayList<String> matched_DAL_words) {
		this.matched_DAL_words = matched_DAL_words;
	}

	
}
