package com.SocialCity.TwitterAnalysis;

import com.SocialCity.TwitterAnalysis.Twokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TweetScore {
	
	private String tweet = null;
	private Double val = null;
	private Double active = null;
	private Double image = null;
	private Double matched_ratio = null;

	public TweetScore(String tweet, Double val, Double active, Double image, Double matched_ratio){
		this.tweet = tweet;
		this.val = val;
		this.active = active;
		this.image = image;
		this.matched_ratio = matched_ratio;
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
		return matched_ratio
				;
	}

	
}
