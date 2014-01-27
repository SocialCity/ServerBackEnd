package com.SocialCity.TwitterAnalysis;

import java.util.ArrayList;
import java.util.Iterator;

//used to collect all the information gained from analysing a set of tweets
public class Tweet_Info_Bloc {
	
	private String bloc_name = "DEFAULT";
	
	private double mean_matched_ratio;
	private double mean_valience;
	private double mean_activity;
	private double mean_imagery;
	
	private ArrayList<TweetScore> scores;
	
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
			}
		}
		
		//store averages
		mean_matched_ratio = total_matched_ratio / scores.size();
		mean_valience = total_valience / count;
		mean_activity = total_activity / count;
		mean_imagery = total_imagery / count;
		//************finished calculating totals and averages********************//
		
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
