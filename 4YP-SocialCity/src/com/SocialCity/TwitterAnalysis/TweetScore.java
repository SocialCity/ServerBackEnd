package com.SocialCity.TwitterAnalysis;

import com.SocialCity.TwitterAnalysis.Twokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TweetScore {
	
	private Double val = null;
	private Double active = null;
	private Double image = null;

	public TweetScore(Double val, Double active, Double image){
		this.val = val;
		this.active = active;
		this.image = image;
	}
	
/*	public WordScore(Double val, Double active){
		this.val = val;
		this.active = active;
	} */
	
	public Double get_valience(){
		return val;
	}
	
	public Double get_active(){
		return active;
	}
	
	public Double get_image(){
		return image;
	}

	
}
