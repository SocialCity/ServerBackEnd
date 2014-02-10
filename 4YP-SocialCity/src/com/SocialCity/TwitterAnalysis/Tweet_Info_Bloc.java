package com.SocialCity.TwitterAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

//used to collect all the information gained from analysing a set of tweets
public class Tweet_Info_Bloc {
	
	private String bloc_name = "DEFAULT";
	
	private double mean_matched_ratio;
	private double mean_valience;
	private double mean_activity;
	private double mean_imagery;
	
	private ArrayList<TweetScore> scores;
	
	private HashMap<String, Word_Stats> adjective_map = new HashMap<String,Word_Stats>();
	private HashMap<String, Word_Stats> dal_map = new HashMap<String,Word_Stats>();
	private HashMap<String, Word_Stats> noun_map = new HashMap<String,Word_Stats>();
	private HashMap<String, Word_Stats> verb_map = new HashMap<String,Word_Stats>();

	
	Tweet_Info_Bloc(ArrayList<TweetScore> scores){
		// store scores
		this.scores = scores;
		Iterator<TweetScore> it_ts= scores.iterator();
		TweetScore tweet_sc;
		
		//************calculate totals and averages********************//
		double total_valience =  0;
		double total_activity = 0;
		double total_imagery = 0;
		double total_matched_ratio = 0;
		int count = 0;
		
		
		while (it_ts.hasNext()){
			tweet_sc = it_ts.next();
			
			// total the matched ratios from all tweets
			total_matched_ratio = total_matched_ratio + tweet_sc.get_matched_ratio();	
			// total the val, act, img values for valid tweets (matched words with the DAL set)
			if (tweet_sc.get_Dal_classification() == DAL_Classification.VALID){
				total_valience = total_valience + tweet_sc.get_valience();
				total_activity = total_activity + tweet_sc.get_active();
				total_imagery = total_imagery + tweet_sc.get_image();
				count++;
			//************** finished taking totals ***************************//
				
			//**************** start analysing per word found in each tweet score object ****** //
			Iterator<String> dal_words = tweet_sc.get_matched_DAL_words().iterator();
			Iterator<String> adjectives = tweet_sc.get_Matched_adjectives().iterator();
			Iterator<String> verbs  = tweet_sc.get_Matched_verbs().iterator();
			Iterator<String> nouns = tweet_sc.get_Matched_nouns().iterator();
			
			while (dal_words.hasNext()){
				String word = dal_words.next();
				if(dal_map.containsKey(word)){
					dal_map.get(word).add_score(tweet_sc);
				}
				else{
					Word_Stats n = new Word_Stats(word);
					n.add_score(tweet_sc);
					dal_map.put(word, n);
				}
			}
			
			while (adjectives.hasNext()){
				String word = adjectives.next();
				if(adjective_map.containsKey(word)){
					adjective_map.get(word).add_score(tweet_sc);
				}
				else{
					Word_Stats n = new Word_Stats(word);
					n.add_score(tweet_sc);
					adjective_map.put(word, n);
				}
			}
			
			while (nouns.hasNext()){
				String word = nouns.next();
				if(noun_map.containsKey(word)){
					noun_map.get(word).add_score(tweet_sc);
				}
				else{
					Word_Stats n = new Word_Stats(word);
					n.add_score(tweet_sc);
					noun_map.put(word, n);
				}
			}
			
			while (verbs.hasNext()){
				String word = verbs.next();
				if(verb_map.containsKey(word)){
					verb_map.get(word).add_score(tweet_sc);
				}
				else{
					Word_Stats n = new Word_Stats(word);
					n.add_score(tweet_sc);
					verb_map.put(word, n);
				}
			}
			
				
			}
		}
		
		//calculate and store total averages
		mean_matched_ratio = total_matched_ratio / scores.size();
		mean_valience = total_valience / count;
		mean_activity = total_activity / count;
		mean_imagery = total_imagery / count;
		
				
	}

	public ArrayList<Word_Stats> get_adjective_stats_freqsorted(){
		//returns a sorted list of adjectives, sorted in descending order of frequency
		
		//build array list from adjective map set values
		ArrayList<Word_Stats> sorted = new ArrayList<Word_Stats>(adjective_map.values());
		Collections.sort(sorted);
		Collections.reverse(sorted);
		return sorted;
	}
	
	public ArrayList<Word_Stats> get_noun_stats_freqsorted(){
		//returns a sorted list of adjectives, sorted in descending order of frequency
		
		//build array list from adjective map set values
		ArrayList<Word_Stats> sorted = new ArrayList<Word_Stats>(noun_map.values());
		Collections.sort(sorted);
		Collections.reverse(sorted);
		return sorted;
	}
	
	public ArrayList<Word_Stats> get_verb_stats_freqsorted(){
		//returns a sorted list of adjectives, sorted in descending order of frequency
		
		//build array list from adjective map set values
		ArrayList<Word_Stats> sorted = new ArrayList<Word_Stats>(verb_map.values());
		Collections.sort(sorted);
		Collections.reverse(sorted);
		return sorted;
	}
	
	public ArrayList<Word_Stats> get_DAL_stats_freqsorted(){
		//returns a sorted list of adjectives, sorted in descending order of frequency
		
		//build array list from adjective map set values
		ArrayList<Word_Stats> sorted = new ArrayList<Word_Stats>(dal_map.values());
		Collections.sort(sorted);
		Collections.reverse(sorted);
		return sorted;
	}
	
	public ArrayList<TweetScore> get_tweet_scores() {
		return scores;
	}

	public double get_mean_matched_ratio() {
		return mean_matched_ratio;
	}

	public double get_mean_valience() {
		return mean_valience;
	}

	public double get_mean_activity() {
		return mean_activity;
	}

	public double get_mean_imagery() {
		return mean_imagery;
	}

	public String get_bloc_name() {
		return bloc_name;
	}

	public void set_bloc_name(String name) {
		bloc_name = name;
	}
}
