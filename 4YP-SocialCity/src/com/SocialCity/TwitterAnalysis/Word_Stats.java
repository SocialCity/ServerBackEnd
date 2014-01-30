package com.SocialCity.TwitterAnalysis;

import java.util.ArrayList;

public class Word_Stats implements Comparable<Integer> {

	private String word;
	private Integer frequency;
	
	private ArrayList<Double> DAL_activity;
	private ArrayList<Double> DAL_image;
	private ArrayList<Double> DAL_valience;
	
	private boolean stats_uptodate = true;
	
	Double stat_activity_varience;
	Double stat_image_variance;
	Double stat_valience_variance;
	
	Double stat_activity_mean;
	Double stat_image_mean;
	Double stat_valience_mean;
	

	public Word_Stats(String word){
		this.word = word;
	}
	
	
	public Double get_varience_activity(){
		//todo
		stats_uptodate = true;
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public int compareTo(Integer frequency_count) {
		return frequency.compareTo(frequency_count);
	}

	public int get_Frequency() {
		return frequency;
	}

	public void set_Frequency(int frequency) {
		this.frequency = frequency;
	}
		
	public void add_score(Double activity, Double imagery, Double valience){
			DAL_activity.add(activity);
			DAL_image.add(imagery);
			DAL_valience.add(valience);
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
}
	

