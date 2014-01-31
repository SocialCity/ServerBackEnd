package com.SocialCity.TwitterAnalysis;

import java.util.ArrayList;
import java.util.Iterator;

public class Word_Stats implements Comparable<Word_Stats> {

	private String word; //the actual word
	private Integer frequency; // how often the word is used
	private Integer retweet_freq; // how often the word is used in a retweet tweet
	private Integer DAL_valid_freq; // how often the word is used in DAL valid tweets
	
	private ArrayList<Double> DAL_activity; // a list of the activity scores that tweets have with the word in
	private ArrayList<Double> DAL_image; // a list of the image scores that tweets have with the word in
	private ArrayList<Double> DAL_valience;	// a list of the valience scores that tweets have with the word in
	
	private boolean stats_uptodate = true;
	
	double stat_activity_varience;
	double stat_image_variance;
	double stat_valience_variance;
	
	double stat_activity_mean;
	double stat_image_mean;
	double stat_valience_mean;
	
	

	public Word_Stats(String word){
		this.word = word;
	}
	
	public Double get_valience_mean(){
		
		double total = 0;
		if (stats_uptodate == false){
			Iterator<Double> it = DAL_valience.iterator();
			
			while (it.hasNext()){
				total = total + it.next().doubleValue();
			}
			total = total / DAL_valience.size();
			
			stat_valience_mean = total;
			return stat_valience_mean;
		}
		else{
			return stat_valience_mean;
		}
	}
	
	public Double get_activity_mean(){
		
		double total = 0;
		if (stats_uptodate == false){
			Iterator<Double> it = DAL_activity.iterator();
			
			while (it.hasNext()){
				total = total + it.next().doubleValue();
			}
			total = total / DAL_activity.size();
			
			stat_activity_mean = total;
			return stat_activity_mean;
		}
		else{
			return stat_activity_mean;
		}
	}
		
	public Double get_imagery_mean(){
			
		double total = 0;
		if (stats_uptodate == false){
			Iterator<Double> it = DAL_image.iterator();
				
			while (it.hasNext()){
				total = total + it.next().doubleValue();
			}
			total = total / DAL_image.size();
			
			stat_image_mean = total;
			return stat_image_mean;
		}
		else{
			return stat_image_mean;
		}
	}
	
	@Override
	public int compareTo(Word_Stats stat) {
		return frequency.compareTo(stat.get_Frequency());
	}

	public int get_Frequency() {
		return frequency;
	}
		
	public void add_score(TweetScore score){
			frequency++;
			DAL_activity.add(score.get_active());
			DAL_image.add(score.get_image());
			DAL_valience.add(score.get_valience());
			
			if (score.get_Dal_classification() == DAL_Classification.VALID){
				DAL_valid_freq++;
			}
			
			if (score.get_retweet_flag() == true){
				retweet_freq++;
			}
			
			stats_uptodate = false;
	}
	
	public ArrayList<Double> get_activity_scores(){
		return DAL_activity;
	}
	
	public ArrayList<Double> get_valience_scores(){
		return DAL_valience;
	}
	
	public ArrayList<Double> get_imagery_scores(){
		return DAL_image;
	}

	public String get_word() {
		return word;
	}

}
	

